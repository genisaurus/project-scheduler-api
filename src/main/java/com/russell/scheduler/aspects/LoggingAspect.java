package com.russell.scheduler.aspects;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LogManager.getLogger();

    @Before(value = "within(com.russell.scheduler..*)")
    public void logMethodStart(JoinPoint jp) {
        String methodSignature = extractMethodSignature(jp);
        String methodArgs = Arrays.toString(jp.getArgs());
        logger.debug("Method Invocation: {}{}", methodSignature, methodArgs);
    }

    private String extractMethodSignature(JoinPoint jp) {
        return jp.getTarget().getClass().toString() + "#" + jp.getSignature().getName();
    }
}
