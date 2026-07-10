package com.mercadoyuli.controller;

import com.mercadoyuli.service.CarritoService;
import com.mercadoyuli.service.ProductoService;
import com.mercadoyuli.service.UsuarioService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    public GlobalModelAdvice(CarritoService carritoService, ProductoService productoService,
                             UsuarioService usuarioService) {
        this.carritoService = carritoService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        // El usuario autenticado se obtiene del SecurityContext (JWT), no de la sesion
        model.addAttribute("usuarioLogueado", usuarioService.usuarioActual().orElse(null));
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        model.addAttribute("categorias", productoService.obtenerCategorias());
    }
}
