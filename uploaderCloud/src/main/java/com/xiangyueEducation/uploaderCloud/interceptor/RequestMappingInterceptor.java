package com.xiangyueEducation.uploaderCloud.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestMappingInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<String> requestMappingPath = new ThreadLocal<>();

    public static String getRequestMappingPath() {
        return requestMappingPath.get();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof org.springframework.web.method.HandlerMethod) {
            org.springframework.web.method.HandlerMethod handlerMethod = (org.springframework.web.method.HandlerMethod) handler;
            String mappingPath = extractMappingPath(handlerMethod);
            requestMappingPath.set(mappingPath);
        }
        return true;
    }

    private String extractMappingPath(org.springframework.web.method.HandlerMethod handlerMethod) {
        RequestMapping requestMapping = handlerMethod.getMethod().getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return requestMapping.value().length > 0 ? requestMapping.value()[0] : "";
        }

        GetMapping getMapping = handlerMethod.getMethod().getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return getMapping.value().length > 0 ? getMapping.value()[0] : "";
        }

        PostMapping postMapping = handlerMethod.getMethod().getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return postMapping.value().length > 0 ? postMapping.value()[0] : "";
        }

        return "";
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        requestMappingPath.remove();
    }
}
