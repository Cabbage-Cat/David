package com.cosmos.david.aspect;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.cosmos.david.contant.BasicConstant.*;

@Aspect
@Component
public class WeightLimitAspect {
    private final static RateLimiter API_IP_LIMITER =
            RateLimiter.create(Double.valueOf(API_WEIGHT_LIMIT_PER_MIN_BY_IP * 0.8) / 60);
    private final static RateLimiter SAPI_IP_LIMITER =
            RateLimiter.create(Double.valueOf(SAPI_WEIGHT_LIMIT_PER_MIN_BY_IP * 0.8) / 60);
    private final static RateLimiter SAPI_UID_LIMITER =
            RateLimiter.create(Double.valueOf(SAPI_WEIGHT_LIMIT_PER_MIN_BY_UID * 0.8) / 60);
    private final static long DEFAULT_TIMEOUT = 3L;

    @Pointcut("@annotation(com.cosmos.david.aspect.WeightLimit)")
    public void checkPointcut() {
    }

    @Around(value = "checkPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        //获取目标方法
        Method targetMethod = methodSignature.getMethod();
        if (targetMethod.isAnnotationPresent(WeightLimit.class)) {
            WeightLimit rate = targetMethod.getAnnotation(WeightLimit.class);
            RateLimiter limiter = null;
            switch (rate.type()) {
                case WEIGHT_LIMIT_TYPE_BY_SAPI_IP: {
                    limiter = SAPI_IP_LIMITER;
                    break;
                }
                case WEIGHT_LIMIT_TYPE_BY_SAPI_UID: {
                    limiter = SAPI_UID_LIMITER;
                    break;
                }
                default: {
                    limiter = API_IP_LIMITER;
                    break;
                }
            }
            if (limiter.tryAcquire(DEFAULT_TIMEOUT, TimeUnit.SECONDS)) {
                return pjp.proceed();
            }
        }
        return null;
    }
}
