package com.mercadoyuli.controller;

import com.mercadoyuli.model.Usuario;
import com.mercadoyuli.service.CarritoService;
import com.mercadoyuli.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductoService productoService;
    private final CarritoService carritoService;

    public HomeController(ProductoService productoService, CarritoService carritoService) {
        this.productoService = productoService;
        this.carritoService = carritoService;
    }

    @GetMapping("/")
    public String inicio(Model model, HttpSession session) {
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        model.addAttribute("productosDestacados", productoService.obtenerProductosDestacados());
        model.addAttribute("productosOferta", productoService.obtenerProductosOferta());

        // Usuario logueado
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuario);

        return "index";
    }

    @GetMapping("/terminos")
    public String terminos(Model model) {
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        return "terminos";
    }
}
