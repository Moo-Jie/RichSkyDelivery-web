package com.sky.Annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

/**
 * 用于公共字段赋值的自定义注解
 * 参数OperationType指定操作类型
 * 更新操作UPDATE   插入操作INSERT
 * @author DuRuiChi
 * @create 2024/10/25
 **/
public @interface AutoFileAssign {
    OperationType value();

}
