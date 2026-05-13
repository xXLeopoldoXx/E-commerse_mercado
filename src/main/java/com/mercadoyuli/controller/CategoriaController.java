package com.mercadoyuli.controller;

import com.mercadoyuli.model.Categoria;
import com.mercadoyuli.model.Producto;
import com.mercadoyuli.service.CarritoService;
import com.mercadoyuli.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CategoriaController {

    private final ProductoService productoService;
    private final CarritoService carritoService;

    public CategoriaController(ProductoService productoService, CarritoService carritoService) {
        this.productoService = productoService;
        this.carritoService = carritoService;
    }

    @GetMapping("/categoria/{slug}")
    public String verCategoria(@PathVariable String slug, Model model) {
        Categoria categoria = productoService.obtenerCategoriaPorSlug(slug);
        if (categoria == null) {
            return "redirect:/";
        }

        List<Producto> productos = productoService.obtenerProductosPorCategoria(slug);

        model.addAttribute("categoria", categoria);
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        return "categorias/detalle";
    }

    @GetMapping("/producto/{id}")
    public String verProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto == null) {
            return "redirect:/";
        }

        // Productos relacionados de la misma categoria
        List<Producto> relacionados = productoService.obtenerProductosPorCategoria(
                        producto.getCategoria().getSlug())
                .stream()
                .filter(p -> !p.getId().equals(id))
                .limit(4)
                .toList();

        model.addAttribute("producto", producto);
        model.addAttribute("relacionados", relacionados);
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        return "producto/detalle";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam String q, Model model) {
        List<Producto> resultados = productoService.buscarProductos(q);
        model.addAttribute("resultados", resultados);
        model.addAttribute("query", q);
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        return "buscar";
    }
}
