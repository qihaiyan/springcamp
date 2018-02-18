package cn.springcamp.springaop.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPointConfig {
    @Pointcut("execution(* cn.springcamp.springaop.service.*.*(..))")
    public void serviceLayerExecution() {}
}
