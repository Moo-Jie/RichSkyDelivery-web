package com.sky.handler;

import com.aliyun.oss.OSSException;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     * 异常被抛出时，程序会按照方法调用栈的顺序查找能够处理该异常的异常处理器（Exception Handler）。
     * 如果在当前方法中找到了匹配的异常处理器，那么就会执行该处理器中的代码。
     * 如果没有找到，那么异常会继续向上抛出，直到找到合适的异常处理器或者程序崩溃。
     */
    @ExceptionHandler//异常
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获用户重复添加的异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //处理：java.sql.SQLIntegrityConstraintViolationException:
        //错误信息：Duplicate entry 'dadaddadad' for key 'employee.idx_username'
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return Result.error(msg);
        }
        return Result.error("未知错误");
    }

    @ExceptionHandler
    public Result exceptionHandler(OSSException ex){
        log.error("阿里云文件上传失败：{}", ex.getMessage());
        //异常处理
        System.out.println("Caught an OSSException, which means your request made it to OSS, "
                + "but was rejected with an error response for some reason.");
        System.out.println("Error Message:" + ex.getErrorMessage());
        System.out.println("Error Code:" + ex.getErrorCode());
        System.out.println("Request ID:" + ex.getRequestId());
        System.out.println("Host ID:" + ex.getHostId());

        return Result.error("阿里云文件上传失败");
    }
}
