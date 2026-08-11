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

    // ── POINTCUTS ──────────────────────────────────────────────────────

    @Pointcut("execution(* com.scalecart.product.service.*.*(..))")
    public void serviceLayerPointcut() {}

    @Pointcut("@annotation(com.scalecart.product.annotation.TrackExecutionTime)")
    public void trackExecutionTimePointcut() {}

    @Pointcut("execution(* com.scalecart.product.repository.*.*(..))")
    public void repositoryLayerPointcut() {}

    // ── SERVICE LAYER ADVICE ───────────────────────────────────────────

    /**
     * @Around — wraps entire method execution.
     * ProceedingJoinPoint lets you control WHEN the real method runs
     * via proceed(). Regular JoinPoint cannot do this.
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

            // Always re-throw — swallowing breaks @Transactional rollback
            throw e;
        }
    }

    /**
     * @Before — fires before method starts.
     * Cannot stop the method from running (use @Around for that).
     */
    @Before("serviceLayerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        log.debug("@Before: {}.{}",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    /**
     * @AfterThrowing — fires ONLY when the method throws an exception.
     * Exception still propagates — this doesn't catch it, just observes.
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
     * returnValue = what the method actually returned.
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

    // ── @TrackExecutionTime ANNOTATION ADVICE ─────────────────────────

    /**
     * Fires for any method annotated with @TrackExecutionTime.
     * More selective than the service-layer pointcut —
     * you choose exactly which methods to track.
     */
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

    // ── REPOSITORY LAYER ADVICE ────────────────────────────────────────

    /**
     * Logs every repository (DB) call and its duration.
     *
     * Why this matters:
     * If a service method takes 800ms but the repository
     * call inside it takes 750ms, the problem is a slow DB query —
     * not the service logic. This pointcut makes that visible.
     *
     * Also helps spot N+1 query problems during development:
     * if you see the same repository method called 50 times
     * in one request, you have an N+1 issue.
     */
    @Around("repositoryLayerPointcut()")
    public Object logRepositoryCall(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String className  = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args     = joinPoint.getArgs();

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;

        // Use debug level — too verbose for INFO in production
        // Switch to WARN if duration exceeds threshold
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