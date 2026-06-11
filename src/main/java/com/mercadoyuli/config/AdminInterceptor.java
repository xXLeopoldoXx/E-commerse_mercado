package com.mercadoyuli.config;

import com.mercadoyuli.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (uri.equals("/admin/login") || uri.startsWith("/admin/login?")) return true;

        Usuario admin = (Usuario) request.getSession().getAttribute("adminLogueado");
        if (admin == null || !"ADMIN".equals(admin.getRol())) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }
        return true;
    }
}
