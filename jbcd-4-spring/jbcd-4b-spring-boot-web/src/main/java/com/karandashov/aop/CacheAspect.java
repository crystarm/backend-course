package com.karandashov.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class CacheAspect {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Around("@annotation(com.karandashov.aop.UseCache)")
    public Object useCacheAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        
        if (cache.containsKey(methodName)) {
            log.info("Returning value from cache for method: {}", methodName);
            return cache.get(methodName);
        }

        log.info("Cache miss for method: {}. Calling original method.", methodName);
        Object result = joinPoint.proceed();
        cache.put(methodName, result);
        return result;
    }

    @AfterReturning("@annotation(com.karandashov.aop.ClearCache)")
    public void clearCacheAdvice() {
        log.info("Clearing cache due to @ClearCache");
        cache.clear();
    }
}
