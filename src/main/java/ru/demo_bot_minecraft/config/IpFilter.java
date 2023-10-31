package ru.demo_bot_minecraft.config;

import lombok.RequiredArgsConstructor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class IpFilter implements Filter {
    private final String allowedIp;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getRemoteAddr().equals(allowedIp)) {
            chain.doFilter(request, response);
        } else {
            response.getWriter().write("Access denied");
        }
    }
}
