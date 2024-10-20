package com.xiangyueEducation.uploaderCloud.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiangyueEducation.uploaderCloud.POJO.Admin;
import com.xiangyueEducation.uploaderCloud.Utils.JwtHelper;
import com.xiangyueEducation.uploaderCloud.Utils.Result;
import com.xiangyueEducation.uploaderCloud.Utils.ResultCodeEnum;
import com.xiangyueEducation.uploaderCloud.mapper.AdminMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@CrossOrigin
public class AdminMotionInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private AdminMapper adminMapper;

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader("token");
//        String adminId = jwtHelper.getUserId(token);
//        Admin admin = adminMapper.selectById(adminId);
//        try {
//            if (admin.getRoleId().equals(1)){
//                return true;
//            }
//        }catch (Exception e){
//            System.out.println("tokenIsNullException="+e.getMessage());
//        }
//        Result fail = Result.build(null, ResultCodeEnum.NOTLOGIN);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = objectMapper.writeValueAsString(fail);
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().print(json);
//        return false;
//    }
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    System.out.println("*************拦截一次*************");
    // 处理CORS预检请求
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    response.setHeader("Access-Control-Allow-Credentials", "true");

    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
        response.setStatus(HttpServletResponse.SC_OK);
        return false;
    }

    // 处理实际请求
    String token = request.getHeader("token");
    if (token == null || token.isEmpty()) {
        sendUnauthorizedResponse(response, "Token is missing");
        return false;
    }

    String adminId;
    try {
        adminId = jwtHelper.getUserId(token);
    } catch (Exception e) {
        sendUnauthorizedResponse(response, "Invalid token");
        return false;
    }

    Admin admin;
    try {
        admin = adminMapper.selectById(adminId);
        if (admin != null && admin.getRoleId().equals(1)) {
            return true;
        }
    } catch (Exception e) {
        sendUnauthorizedResponse(response, "User not found");
        return false;
    }

    sendUnauthorizedResponse(response, "Not authorized");
    return false;
}

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        Result fail = Result.build(null, ResultCodeEnum.NOTLOGIN);
        fail.setMessage(message);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(fail);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(json);
    }
}