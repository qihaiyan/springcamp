package cn.springcamp.spring.requestbody;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

@ControllerAdvice
public class ModifyBodyAdvice extends RequestBodyAdviceAdapter {
    @Autowired
    HttpServletRequest httpServletRequest;

    @Override
    @NonNull
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter, @NonNull Type targetType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        String requestMethod = httpServletRequest.getMethod();
        String fieldName = "foo";

        if (StringUtils.startsWithIgnoreCase(requestMethod, HttpMethod.PUT.name())
                || StringUtils.startsWithIgnoreCase(requestMethod, HttpMethod.POST.name())
        ) {
            Field field = ReflectionUtils.findField(body.getClass(), fieldName);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                String paramValue = Optional.ofNullable(httpServletRequest.getHeader(fieldName)).orElse("");
                Method method = ReflectionUtils.findMethod(body.getClass(), "set" +
                        StringUtils.capitalize(fieldName), field.getType());
                if (method != null) {
                    ReflectionUtils.invokeMethod(method, body, paramValue);
                }
            }
        }
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);

    }

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,
                            @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}

