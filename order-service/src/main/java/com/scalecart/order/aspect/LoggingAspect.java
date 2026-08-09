package com.scalecart.order.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect        // tells Spring: this class contains AOP advice
@Component     // makes it a Spring bean so Spring can proxy it
public class LoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut definition — reusable expression.
     *
     * Matches ALL methods in ALL classes inside any package
     * named "service" under com.scalecart.product.
     *
     * Breakdown of expression:
     * execution(          — match method executions
     *   *                 — any return type
     *   com.scalecart.product.service.  — in this package
     *   *                 — any class name
     *   .                 — dot separator
     *   *                 — any method name
     *   (..)              — any number of parameters of any type
     * )
     */
    @Pointcut("execution(* com.scalecart.order.service.*.*(..))")
    public void serviceLayerPointcut() {
        // Empty — this method is just a named pointcut reference
        // Advice annotations reference this method name
    }

    /**
     * @Around advice — wraps the entire method execution.
     *
     * ProceedingJoinPoint gives you control over WHEN the real
     * method runs (via proceed()). Regular JoinPoint cannot do this.
     */
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
            // THIS is where the actual service method runs
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

            // Re-throw — don't swallow exceptions in aspects
            throw e;
        }
    }

    /**
     * @Before advice — fires before method starts.
     * Useful for argument validation logging.
     * Cannot stop the method from running (use @Around for that).
     */
    @Before("serviceLayerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        // Intentionally minimal — @Around already logs entry
        // Keeping this to demonstrate @Before exists and when to use it
        log.debug("@Before: {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    /**
     * @AfterThrowing — fires ONLY when the method throws an exception.
     * Good for audit logging: "method X threw exception Y with args Z"
     * Note: exception still propagates — this doesn't catch it
     */
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

    /**
     * @AfterReturning — fires ONLY on successful return (no exception).
     * returnValue gives you access to what the method returned.
     */
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

    // Pointcut that matches any method annotated with @TrackExecutionTime
    @Pointcut("@annotation(com.scalecart.product.annotation.TrackExecutionTime)")
    public void trackExecutionTimePointcut() {}

    // Advice that fires for @TrackExecutionTime annotated methods
    @Around("trackExecutionTimePointcut()")
    public Object trackAnnotatedMethod(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        log.info("@TrackExecutionTime — {} executed in {}ms", methodName, duration);

        return result;
    }
}