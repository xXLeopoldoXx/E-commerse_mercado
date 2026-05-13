package com.mercadoyuli.controller;

import com.mercadoyuli.service.CarritoService;
import com.mercadoyuli.service.ProductoService;
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
    public String inicio(Model model) {
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());

        // Productos destacados: 2 de cada categoria (10 total)
        model.addAttribute("productosDestacados", productoService.obtenerProductosDestacados());

        // Productos en oferta: seleccion aleatoria de 8
        model.addAttribute("productosOferta", productoService.obtenerProductosOferta());

        return "index";
    }

    @GetMapping("/terminos")
    public String terminos(Model model) {
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        return "terminos";
    }
}
