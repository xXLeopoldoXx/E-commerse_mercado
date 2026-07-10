package com.mercadoyuli.controller;

import com.mercadoyuli.service.CarritoService;
import com.mercadoyuli.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CarritoService carritoService;
    private final ProductoService productoService;

    public GlobalModelAdvice(CarritoService carritoService, ProductoService productoService) {
        this.carritoService = carritoService;
        this.productoService = productoService;
    }

    @ModelAttribute
    public void addCommonAttributes(HttpSession session, Model model) {
        model.addAttribute("usuarioLogueado", session.getAttribute("usuarioLogueado"));
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        model.addAttribute("categorias", productoService.obtenerCategorias());
    }
}
