package com.xiangyueEducation.uploaderCloud.aspect;

import com.xiangyueEducation.uploaderCloud.interceptor.RequestMappingInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;


@Aspect
@Component
@Slf4j
public class IpAddressAspect {

    @Autowired
    private HttpServletRequest request;

    @Before("execution(* com.xiangyueEducation.uploaderCloud.Controller..*(..)) && !execution(* com.xiangyueEducation.uploaderCloud.Controller.FileController.*(..))") // 匹配 MyTest 类中的所有方法
    public void logClientIp(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        String requestMappingPath = RequestMappingInterceptor.getRequestMappingPath();

        // 获取客户端的 IP 地址
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        log.info("请求IP: {}, java方法: {}, 方法映射路径: {}:+++++++++++start++++++++++++", clientIp, name, requestMappingPath);
    }


    @After("execution(* com.xiangyueEducation.uploaderCloud.Controller..*(..)) && !execution(* com.xiangyueEducation.uploaderCloud.Controller.FileController.*(..))") // 匹配 MyTest 类中的所有方法
    public void logClientIpEnd(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        String requestMappingPath = RequestMappingInterceptor.getRequestMappingPath();

        // 获取客户端的 IP 地址
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        log.info("请求IP: {}, java方法: {}, 方法路径: {}:-----------end------------", clientIp, name, requestMappingPath);
    }
}
