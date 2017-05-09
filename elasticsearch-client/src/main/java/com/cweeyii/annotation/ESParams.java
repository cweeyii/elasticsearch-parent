package com.cweeyii.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD,FIELD,TYPE})
@Retention(RUNTIME)
public @interface ESParams {
    /**
     * 索引名称
     * @return
     */
    String indexName() default "";

    /**
     * 类型名称
     * @return
     */
    String typeName() default "";

    String writeIndexName() default "";
}