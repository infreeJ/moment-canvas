package com.infreej.moment_canvas.global.annotation;

import com.infreej.moment_canvas.global.code.SuccessCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SetSuccess {
    SuccessCode value();
}
