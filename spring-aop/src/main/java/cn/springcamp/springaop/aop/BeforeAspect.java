package cn.springcamp.springaop.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BeforeAspect {
    @Before("CommonJoinPointConfig.serviceLayerExecution()")
    public void before(JoinPoint joinPoint) {
        System.out.println(" -------------> Before Aspect ");
        System.out.println(" -------------> before execution of " + joinPoint);
    }
}
