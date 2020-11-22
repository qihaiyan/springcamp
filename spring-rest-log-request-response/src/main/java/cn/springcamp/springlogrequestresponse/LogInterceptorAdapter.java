package cn.springcamp.springlogrequestresponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
public class LogInterceptorAdapter extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
                && request.getMethod().equals(HttpMethod.GET.name())) {

            ServletRequest servletRequest = new ContentCachingRequestWrapper(request);
            Map<String, String[]> params = servletRequest.getParameterMap();

            // 从 request 中读取请求参数并打印
            params.forEach((key, value) -> log.info("logInterceptor " + key + "=" + Arrays.toString(value)));
            // 避免从 inputStream 中读取body并打印

        }
        return true;
    }
}
