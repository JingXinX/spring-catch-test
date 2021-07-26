package com.example.springcatchtest.bean;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Spring切面类
 */
@Aspect
@Component
public class MySpringAspect {
    @Before("execution(public int com.example.springcatchtest.service.DivServiceImpl.div(..))")
    public void beforeNotify() {
        System.out.println("******** @Before我是前置通知MyAspect");
    }

    @After("execution(public int com.example.springcatchtest.service.DivServiceImpl.div(..))")
    public void afterNotify() {
        System.out.println("******** @After我是后置通知");
    }

    @AfterReturning("execution(public int com.example.springcatchtest.service.DivServiceImpl.div(..))")
    public void afterReturningNotify() {
        System.out.println("********@AfterReturning我是返回后通知");
    }

    @AfterThrowing("execution(public int com.example.springcatchtest.service.DivServiceImpl.div(..))")
    public void afterThrowingNotify() {
        System.out.println("********@AfterThrowing我是异常通知");
    }

    @Around("execution(public int com.example.springcatchtest.service.DivServiceImpl.div(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object retValue = null;
        System.out.println("我是@Around环绕通知之前A");
        retValue = proceedingJoinPoint.proceed();
        System.out.println("我是@Around环绕通知之后B");
        return retValue;
    }
}
