package cn.springcamp.springaop.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TrackTimeAspect {

    @Around("@annotation(trackTime)")
    public Object around(ProceedingJoinPoint joinPoint, TrackTime trackTime) throws Throwable {

        Object result = null;
        long startTime = System.currentTimeMillis();

        log.info("Around Advice before joinPoint.proceed");
        result = joinPoint.proceed();
        log.info("Around Advice after joinPoint.proceed");

        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("Time Taken by " + joinPoint + " with param[" + trackTime.param() + "] is " + timeTaken);
        return result;
    }
}
