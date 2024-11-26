package com.example.taskManagement.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TaskAspect {
    private static final Logger logger = LoggerFactory.getLogger(TaskAspect.class);

    @Before("@annotation(LogBefore)") //выполняется перед точеой входа
    public void beforeAdvice(JoinPoint joinPoint) {
        logger.info("Before executing method: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "@annotation(LogAfterThrowing)", throwing = "exception") //выполняется в случфе исключения в точке входа
    public void afterThrowingAdvice(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method: {} with message: {}", joinPoint.getSignature().getName(), exception.getMessage());
    }

    @AfterReturning(pointcut = "@annotation(LogAfterReturning)", returning = "result") //После того как точка  завершилась
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        logger.info("Method: {} with result: {}", joinPoint.getSignature().getName(), result);
    }

    @Around("@annotation(LogAround)") //До и после
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before executing method: {}", joinPoint.getSignature().getName());
        try {
            Object result = joinPoint.proceed();
            logger.info("After executing method: {}", joinPoint.getSignature().getName());
            return result;
        } catch (Throwable e) {
            logger.error("Exception in method: {} with message: {}", joinPoint.getSignature().getName(), e.getMessage());
            throw e;
        }
    }


}