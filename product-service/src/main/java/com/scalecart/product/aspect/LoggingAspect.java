package com.scalecart.product.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.scalecart.product.service.*.*(..))")
    public void serviceLayerPointcut() {}

    @Pointcut("@annotation(com.scalecart.product.annotation.TrackExecutionTime)")
    public void trackExecutionTimePointcut() {}

    @Pointcut("execution(* com.scalecart.product.repository.*.*(..))")
    public void repositoryLayerPointcut() {}

    @Around("serviceLayerPointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String className  = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args     = joinPoint.getArgs();

        log.info("→ Entering {}.{}() with args: {}",
                className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("← Exiting {}.{}() — completed in {}ms",
                    className, methodName, executionTime);

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.error("✗ Exception in {}.{}() after {}ms — {}: {}",
                    className, methodName, executionTime,
                    e.getClass().getSimpleName(), e.getMessage());

            throw e;
        }
    }

    @Before("serviceLayerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        log.debug("@Before: {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    @AfterThrowing(
            pointcut = "serviceLayerPointcut()",
            throwing  = "exception"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("AUDIT — Exception thrown in {}.{}() — Exception: {} — Message: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                exception.getClass().getName(),
                exception.getMessage());
    }

    @AfterReturning(
            pointcut  = "serviceLayerPointcut()",
            returning = "returnValue"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object returnValue) {
        log.debug("@AfterReturning: {}.{}() returned: {}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                returnValue != null ? returnValue.getClass().getSimpleName() : "null");
    }

    @Around("trackExecutionTimePointcut()")
    public Object trackAnnotatedMethod(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        log.info("@TrackExecutionTime — {} executed in {}ms",
                methodName, duration);

        return result;
    }

    @Around("repositoryLayerPointcut()")
    public Object logRepositoryCall(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String className  = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args     = joinPoint.getArgs();

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;

        if (duration > 200) {
            log.warn("SLOW DB QUERY — {}.{}() with args {} took {}ms",
                    className, methodName,
                    Arrays.toString(args), duration);
        } else {
            log.debug("DB QUERY — {}.{}() completed in {}ms",
                    className, methodName, duration);
        }

        return result;
    }
}
