#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""展平 multi-release jar：把 META-INF/versions/<n>/ 下的类提升到基础路径。

背景：clickhouse-jdbc 0.4.6 的 http 变体把 Java 11+ 连接实现只放在
META-INF/versions/11/ 下（基础路径无该类），且依赖 Apache HttpClient5。
Flink 的 ChildFirstClassLoader（URLClassLoader 系）不解析 multi-release
目录，TaskManager 上加载连接实现时报 NoClassDefFoundError。
本脚本将 versions 类覆盖合并到基础路径、移除 versions 条目与清单的
Multi-Release 属性，产出普通 jar（Java 17 运行时行为不变）。

用法：
    python3 flatten_mr_jar.py <jar>            # 原地展平
    python3 flatten_mr_jar.py <in.jar> <out.jar>  # 展平到新文件
"""
import os
import sys
import tempfile
import zipfile


def flatten(src, dst):
    zin = zipfile.ZipFile(src, "r")
    names = zin.namelist()
    out = {}  # name -> bytes，versions 条目后写覆盖基础条目
    order = []
    mr_prefixes = tuple("META-INF/versions/%d/" % v for v in range(9, 40))
    # 第一遍：基础条目
    for name in names:
        if name.startswith(mr_prefixes) or name == "META-INF/MANIFEST.MF":
            continue
        if name.endswith("/"):
            continue
        out[name] = zin.read(name)
        order.append(name)
    # 第二遍：versions 条目按版本升序覆盖（高版本覆盖低版本）
    ver_entries = sorted(
        (n for n in names if n.startswith(mr_prefixes) and not n.endswith("/")),
        key=lambda n: int(n.split("/")[2]),
    )
    for n in ver_entries:
        rel = n.split("/", 3)[3]
        out[rel] = zin.read(n)
        if rel not in order:
            order.append(rel)
    # 清单去掉 Multi-Release 属性
    manifest = (
        zin.read("META-INF/MANIFEST.MF")
        .decode("utf-8")
        .replace("Multi-Release: true\r\n", "")
        .replace("Multi-Release: true\n", "")
    )
    zin.close()

    fd, tmp = tempfile.mkstemp(suffix=".jar", dir=os.path.dirname(os.path.abspath(dst)) or ".")
    os.close(fd)
    with zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as zout:
        zout.writestr("META-INF/MANIFEST.MF", manifest)
        for name in order:
            zout.writestr(name, out[name])
    # mkstemp 固定以 0600 创建文件；保留源 jar 的权限位，否则镜像内 COPY 出的
    # jar 为 root:600，flink(9999) 用户无法读取导致 ClassNotFoundException
    os.chmod(tmp, os.stat(src).st_mode & 0o7777)
    os.replace(tmp, dst)
    print("flatten 完成：%d 个条目（提升 %d 个 versions 条目）" % (len(out) + 1, len(ver_entries)))


def main():
    if len(sys.argv) not in (2, 3):
        print("用法: python3 flatten_mr_jar.py <jar> [out.jar]", file=sys.stderr)
        sys.exit(2)
    src = sys.argv[1]
    dst = sys.argv[2] if len(sys.argv) == 3 else src
    flatten(src, dst)


if __name__ == "__main__":
    main()
