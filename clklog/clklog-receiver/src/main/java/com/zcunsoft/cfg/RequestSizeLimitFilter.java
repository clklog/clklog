package com.zcunsoft.cfg;

import com.zcunsoft.util.LimitedInputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ReadListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 请求体大小限制过滤器.
 *
 * <p>在进入 Controller 前拦截超大请求：优先校验 Content-Length 头，超限直接返回 413；
 * 无 Content-Length（如 chunked 传输）时包装请求，限制读取字节数。</p>
 */
public class RequestSizeLimitFilter implements Filter {

    /** 单请求允许的最大字节数 */
    private final int maxRequestSize;

    /**
     * 构造方法.
     *
     * @param receiverSetting 接收服务配置
     */
    public RequestSizeLimitFilter(ReceiverSetting receiverSetting) {
        this.maxRequestSize = receiverSetting.getMaxRequestSize();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 优先校验 Content-Length，超限直接拒绝，避免读取大请求体
        long contentLength = httpRequest.getContentLengthLong();
        if (contentLength > maxRequestSize) {
            httpResponse.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                    "请求体过大，最大允许 " + maxRequestSize + " 字节");
            return;
        }

        // 无 Content-Length（如 chunked 传输）时包装请求，限制实际读取字节数
        chain.doFilter(new LimitedRequestWrapper(httpRequest, maxRequestSize), response);
    }

    /**
     * 限制请求体读取字节数的 HttpServletRequest 包装器.
     */
    private static class LimitedRequestWrapper extends HttpServletRequestWrapper {

        private final long maxBytes;

        private LimitedServletInputStream limitedStream;

        LimitedRequestWrapper(HttpServletRequest request, long maxBytes) {
            super(request);
            this.maxBytes = maxBytes;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            // 惰性创建，确保同一个请求多次读取时使用同一个受限流
            if (limitedStream == null) {
                limitedStream = new LimitedServletInputStream(super.getInputStream(), maxBytes);
            }
            return limitedStream;
        }
    }

    /**
     * 带大小限制的 ServletInputStream.
     */
    private static class LimitedServletInputStream extends ServletInputStream {

        private final LimitedInputStream delegate;

        /** -2 未读取；-1 已到末尾；>=0 最近一次读取的位置标记 */
        private int lastRead = -2;

        LimitedServletInputStream(InputStream in, long maxBytes) {
            this.delegate = new LimitedInputStream(in, maxBytes);
        }

        @Override
        public boolean isFinished() {
            return lastRead == -1;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // 同步读取场景无需异步监听
        }

        @Override
        public int read() throws IOException {
            int b = delegate.read();
            lastRead = b;
            return b;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int n = delegate.read(b, off, len);
            if (n > 0) {
                lastRead = 0;
            } else if (n == -1) {
                lastRead = -1;
            }
            return n;
        }
    }
}
