// iframe下载文件，没又身份验证的get接口可以使用
export function iframeDownloads(lastUrl) {
  var downloadFileUrl = window.g.API_URL + lastUrl;
  var elemIF = document.createElement("iframe");
  elemIF.src = downloadFileUrl;
  elemIF.style.display = "none";
  document.body.appendChild(elemIF);
}
// 设备二进制文件
export function blobDownloads(data, fileName) {
  // console.log(data, fileName, "fileName");
  if (!data) {
    return;
  }
  // type: 'application/vnd.ms-excel'
  const url = window.URL.createObjectURL(new Blob([data]));
  // console.log(url, "blodUrl");
  // console.log(new Blob([data]), "blodUrl");
  const link = document.createElement("a");
  link.style.display = "none";
  link.href = url;
  link.setAttribute("download", fileName);
  document.body.appendChild(link);
  link.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(link); // 下载完毕删除a标签
}
