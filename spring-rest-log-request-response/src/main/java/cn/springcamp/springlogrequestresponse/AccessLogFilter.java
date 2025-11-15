package cn.springcamp.springlogrequestresponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@WebFilter(filterName = "accessLogFilter", urlPatterns = "/*")
public class AccessLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        final boolean isFirstRequest = !isAsyncDispatch(request);

        final boolean shouldWrapRequest = isFirstRequest && !(request instanceof ContentCachingRequestWrapper);
        final HttpServletRequest requestToUse = shouldWrapRequest ? new ContentCachingRequestWrapper(request) : request;

        final boolean shouldWrapResponse = !(response instanceof ContentCachingResponseWrapper);
        final ContentCachingResponseWrapper responseToUse = shouldWrapResponse ? new ContentCachingResponseWrapper(response) : (ContentCachingResponseWrapper) response;

        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            doSaveAccessLog(requestToUse, responseToUse);
            String content = getResponseString(responseToUse);
            writeResponse(responseToUse, String.format("{\"data\":%s,\"msg\":\"\",\"ret\":1}", content));
        }
    }

    private void writeResponse(ContentCachingResponseWrapper response, String responseStr) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.resetBuffer();
        PrintWriter writer = response.getWriter();
        writer.write(responseStr);
        copyResponse(response);
    }

    private void doSaveAccessLog(final HttpServletRequest request,
                                 final HttpServletResponse response) {
        final String requestUri = request.getRequestURI();
        final String requestHeaders = getRequestHeaders(request);
        final String requestParams = getRequestParams(request);
        final String requestString = getRequestString(request);
        final String responseString = getResponseString(response);
        final int responseStatus = response.getStatus();

        final List<String> logs = new ArrayList<>();
        logs.add("uri=" + requestUri);
        logs.add("headers=" + requestHeaders);
        logs.add("status=" + responseStatus);
        logs.add("requestContentType=" + request.getContentType());
        logs.add("responseContentType=" + response.getContentType());
        logs.add("params=" + requestParams);
        logs.add("request=" + requestString);
        logs.add("response=" + responseString);

        log.info(String.join(",", logs));
    }

    private String getRequestHeaders(final HttpServletRequest request) {
        final Enumeration<String> headerNames = request.getHeaderNames();
        final List<String> headers = new ArrayList<>();
        while (headerNames.hasMoreElements()) {
            final String key = headerNames.nextElement();
            headers.add(key + ':' + request.getHeader(key));
        }
        return '[' + String.join(",", headers) + ']';
    }

    private String getRequestParams(final HttpServletRequest request) {
        final Map<String, String[]> requestParams = request.getParameterMap();
        final List<String> pairs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(requestParams)) {
            for (final Map.Entry<String, String[]> entry : requestParams.entrySet()) {
                final String name = entry.getKey();
                final String[] value = entry.getValue();
                if (value == null) {
                    pairs.add(name + "=");
                } else {
                    for (final String v : value) {
                        pairs.add(name + "=" + v);
                    }
                }
            }
        }
        String requestParamsStr = CollectionUtils.isEmpty(pairs) ? "" : String.join("&", pairs);
        if (StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
            requestParamsStr = URLDecoder.decode(requestParamsStr, StandardCharsets.UTF_8);
        }
        return requestParamsStr;
    }

    private String getRequestString(final HttpServletRequest request) {
        final ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            try {
                final byte[] buf = wrapper.getContentAsByteArray();
                return new String(buf, wrapper.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                return "[UNKNOWN]";
            }
        }
        return "";
    }

    private String getResponseString(final HttpServletResponse response) {
        final ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            try {
                final byte[] buf = wrapper.getContentAsByteArray();
                return new String(buf, wrapper.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                return "[UNKNOWN]";
            }
        }
        return "";
    }

    private void copyResponse(final HttpServletResponse response) {
        final ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            try {
                wrapper.copyBodyToResponse();
            } catch (IOException ignored) {
            }
        }
    }
}
