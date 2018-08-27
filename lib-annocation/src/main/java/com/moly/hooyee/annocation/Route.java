package com.moly.hooyee.annocation;

import com.moly.hooyee.model.RouteIntercept;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Route {
    String module() default "";
    String path();
    Class<? extends RouteIntercept>[] intercept() default {};
}
