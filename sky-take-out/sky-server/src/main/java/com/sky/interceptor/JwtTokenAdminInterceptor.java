package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户操作校验
     * 
 * @param request
 * @param response
 * @param handler
     * @return boolean
     * @author DuRuiChi
     * @create 2024/10/25
     **/
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());//先拿到请求头中存放JWT的属性名，从而token

        //2、校验令牌
        try {
            log.info("jwt校验成功，允许操作");
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);//使用配置文件中的密钥解析token
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            log.info("当前操作者ID为：{}", empId);

        //3.把empId存入ThreadLocal中
            BaseContext.setCurrentId(empId);
        // 4、通过，放行
            return true;
        } catch (Exception ex) {
        //5、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
