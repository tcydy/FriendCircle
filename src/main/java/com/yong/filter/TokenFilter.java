package com.yong.filter;

import java.io.IOException;

import com.yong.utils.JwtUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;//请求数据
        HttpServletResponse response = (HttpServletResponse) servletResponse;//响应数据

        //1. 获取到请求路径
        String requestURI = request.getRequestURI();

        //2. 判断是否是登录请求，如果路径中包含 /login，说明是登录操作，放行
        if (requestURI.contains("/login") || requestURI.contains("/register")||requestURI.contains("/web/upload")) {
            log.info("登录请求，放行。");
            chain.doFilter(request, response);
            return;
        }

        //3. 获取请求头中的token
        String token = request.getHeader("token");

        //4. 判断token是否存在，如果不存在，说明用户没有登录，返回错误信息(响应401状态码)
        if (token == null || token.isEmpty()) {
            log.info("令牌为空，响应401");
            response.setStatus(401);
            write401(response, "令牌非法或已过期，请重新登录");
            return;
        }

        //5. 如果token存在，校验令牌，如果校验失败 -> 返回错误信息(响应401状态码)
        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.info("令牌非法，响应401");
            log.info("{}",token);
            response.setStatus(401);
            write401(response, "令牌非法或已过期，请重新登录");
            return;
        }

        //6. 校验通过，放行
        chain.doFilter(request, response);

    }
    
    
    private void write401(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"msg\":\"" + msg + "\"}");
        response.getWriter().flush();
    }

    
}
