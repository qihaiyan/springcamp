package cn.springcamp.springaop.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AfterAspect {
    @AfterReturning(value = "CommonJoinPointConfig.serviceLayerExecution()",
            returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        System.out.println(" -------------> AfterReturning Aspect ");
        System.out.println(" -------------> " + joinPoint + " returned with value " + result);
    }

    @After("CommonJoinPointConfig.serviceLayerExecution()")
    public void after(JoinPoint joinPoint) {
        System.out.println(" -------------> After Aspect ");
        System.out.println(" -------------> after execution of " + joinPoint);
    }
}
