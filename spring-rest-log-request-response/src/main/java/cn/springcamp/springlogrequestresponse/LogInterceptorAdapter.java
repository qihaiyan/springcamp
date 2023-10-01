package cn.springcamp.springlogrequestresponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
public class LogInterceptorAdapter implements HandlerInterceptor {

    public void afterCompletion(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
                                @Nullable Exception ex) {
        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
                && request.getMethod().equals(HttpMethod.GET.name())) {

            Map<String, String[]> params = request.getParameterMap();

            // 从 request 中读取请求参数并打印
            params.forEach((key, value) -> log.info("logInterceptor " + key + "=" + Arrays.toString(value)));
            // 避免从 inputStream 中读取body并打印

        }
        log.info("logInterceptor afterCompletion status=" + response.getStatus());
    }
}
