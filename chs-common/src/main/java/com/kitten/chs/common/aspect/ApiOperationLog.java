package com.kitten.chs.common.aspect;

import java.lang.annotation.*;

/**
 * @author kitten
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface ApiOperationLog {

    /**
     * API 功能描述
     *
     * @return
     */
    String description() default "";

}
