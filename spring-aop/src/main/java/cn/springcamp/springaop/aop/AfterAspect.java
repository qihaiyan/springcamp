package cn.springcamp.springaop.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AfterAspect {
    @AfterReturning(value = "CommonJoinPointConfig.serviceLayerExecution()",
            returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        log.info(" -------------> AfterReturning Aspect ");
        log.info(" -------------> " + joinPoint + " returned with value " + result);
    }

    @After("CommonJoinPointConfig.serviceLayerExecution()")
    public void after(JoinPoint joinPoint) {
        log.info(" -------------> After Aspect ");
        log.info(" -------------> after execution of " + joinPoint);
    }
}
