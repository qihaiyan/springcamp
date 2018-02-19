package cn.springcamp.springaop.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class BeforeAspect {
    @Before("CommonJoinPointConfig.serviceLayerExecution()")
    public void before(JoinPoint joinPoint) {
        log.info(" -------------> Before Aspect ");
        log.info(" -------------> before execution of " + joinPoint);
    }
}
