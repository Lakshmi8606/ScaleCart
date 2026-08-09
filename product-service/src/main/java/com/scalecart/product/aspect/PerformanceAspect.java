package com.scalecart.product.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log =
            LoggerFactory.getLogger(PerformanceAspect.class);

    // Threshold above which we consider a method "slow"
    private static final long SLOW_METHOD_THRESHOLD_MS = 500;

    // Matches ALL layers — controller, service, repository
    @Pointcut("within(com.scalecart.product..*)")
    public void applicationLayerPointcut() {}

    @Around("applicationLayerPointcut()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint)
            throws Throwable {

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        // Only log if method is slow — avoids log spam in normal operation
        if (duration > SLOW_METHOD_THRESHOLD_MS) {
            log.warn("SLOW METHOD DETECTED: {}.{}() took {}ms " +
                            "(threshold: {}ms)",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    duration,
                    SLOW_METHOD_THRESHOLD_MS);
        }

        return result;
    }
}