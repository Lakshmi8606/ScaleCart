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

    private static final long SLOW_METHOD_THRESHOLD_MS = 500;

    // Controller, service, repository only — exclude security filters/config
    @Pointcut("within(com.scalecart.product.controller..*) || " +
            "within(com.scalecart.product.service..*) || " +
            "within(com.scalecart.product.repository..*)")
    public void applicationLayerPointcut() {}

    @Around("applicationLayerPointcut()")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint)
            throws Throwable {

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

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
