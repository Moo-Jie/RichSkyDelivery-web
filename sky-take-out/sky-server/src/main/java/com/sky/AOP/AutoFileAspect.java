package com.sky.AOP;

import com.sky.Annotation.AutoFileAssign;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Slf4j
@Component
public class AutoFileAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.Annotation.AutoFileAssign)")
    public void pointCut() {}

    @Before("pointCut()")
    public void AutoPublicFieldAssign(JoinPoint joinPoint) throws Throwable {
        //1.获取注解属性
        //获取代理的方法签名,获取方法本身,获取方法上的注解,获取注解上的属性
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();//转换为具有特定方法的签名对象
        AutoFileAssign assign = signature.getMethod().getAnnotation(AutoFileAssign.class);//获取方法上的注解
        OperationType value = assign.value();//获取注解上的属性值

        //2.获取方法的参数对象
        //通过反射拿到方法的参数对象
        Object[] args = joinPoint.getArgs();
        //判断参数是否为空
        if(args == null || args.length == 0)
            return;
        //(一般位于参数列表的第一个位置)
        Object arg = args[0];

        //3.获取声明方法并在参数对象上运行
        //判断注解属性值进行不同的业务
        if(value == OperationType.INSERT)
        {
            Object object01 = arg.getClass()
                    //通过指定的方法名称和参数类型获取方法对象
                    .getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class)
                    //通过指定运行方法的对象和传入的参数值来调用方法
                    .invoke(arg, LocalDateTime.now());
            Object object02 = arg.getClass()
                    .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class)
                    .invoke(arg, LocalDateTime.now());
            Object object03 = arg.getClass()
                    .getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class)
                    .invoke(arg, BaseContext.getCurrentId());
            Object object04 = arg.getClass()
                    .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class)
                    .invoke(arg, BaseContext.getCurrentId());
        }
        else if(value == OperationType.UPDATE)
        {
            Object object01 = arg.getClass()
                 .getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class)
                 .invoke(arg, LocalDateTime.now());
            Object object02 = arg.getClass()
                 .getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class)
                 .invoke(arg, BaseContext.getCurrentId());
        }
        log.info("进行了公共字段赋值，赋值后为：{}",arg);
    }
}
