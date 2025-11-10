package com.thederailingmafia.carwash.carservice.logger;



import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect // Marks the class as an aspect, which contains cross-cutting concerns
@Component // Registers this aspect as a Spring bean
@Slf4j
public class LoggingAspect {
    @Pointcut("execution(public * com.thederailingmafia.carwash.carservice.service.*.*(..))")
    // Defines a pointcut that matches the execution of any public method in classes under com.example.service package
    public void allServiceMethods() {}
//
//    @Before("allServiceMethods()")
////     Advice that runs before the execution of methods matched by the pointcut
//    public void logBefore(JoinPoint joinPoint) {
//        System.out.println("Before method: " + joinPoint.getSignature().getName());
//    }
//
//    @After("allServiceMethods()")
//    // Advice that runs after the execution of methods matched by the pointcut, regardless of their outcome
//    public void logAfter(JoinPoint joinPoint) {
//        System.out.println("After method: " + joinPoint.getSignature().getName());
//    }
//



    @Around("allServiceMethods()")
    public Object logServiceMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long duration = System.currentTimeMillis()-start;
        log.info("{} executed in {} ms",proceedingJoinPoint.getSignature().toShortString(),duration);
        return result;
    }

}

