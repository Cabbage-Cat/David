package com.cosmos.david.aspect;

import java.lang.annotation.*;

import static com.cosmos.david.contant.BasicConstant.WEIGHT_LIMIT_TYPE_BY_API_IP;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WeightLimit {

    long value() default 1L;

    int type() default WEIGHT_LIMIT_TYPE_BY_API_IP;
}
