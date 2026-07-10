package com.mercadoyuli.controller;

import com.mercadoyuli.model.Categoria;
import com.mercadoyuli.model.PedidoEntity;
import com.mercadoyuli.model.Producto;
import com.mercadoyuli.service.PedidoService;
import com.mercadoyuli.service.ProductoService;
import com.mercadoyuli.service.UsuarioService;
import com.mercadoyuli.validator.ProductoValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final ProductoValidator productoValidator;

    public AdminController(ProductoService productoService,
                           PedidoService pedidoService,
                           UsuarioService usuarioService,
                           ProductoValidator productoValidator) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.productoValidator = productoValidator;
    }

    // ===================== LOGIN / LOGOUT =====================

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session.getAttribute("adminLogueado") != null)
            return "redirect:/admin/dashboard";
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes ra) {
        var resultado = usuarioService.loginAdmin(email, password);
        if (resultado.isPresent()) {
            session.setAttribute("adminLogueado", resultado.get());
            return "redirect:/admin/dashboard";
        }
        ra.addFlashAttribute("error", "Credenciales incorrectas o acceso no autorizado.");
        return "redirect:/admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminLogueado");
        return "redirect:/admin/login";
    }

    // ===================== DASHBOARD =====================

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        List<PedidoEntity> pedidos = pedidoService.obtenerTodos();

        model.addAttribute("totalProductos", productoService.obtenerTodosLosProductos().size());
        model.addAttribute("totalCategorias", productoService.obtenerCategorias().size());
        model.addAttribute("totalPedidos", pedidos.size());
        model.addAttribute("ingresoTotal", pedidoService.calcularIngresoTotal());
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());

        Map<String, Long> porMetodoPago = pedidos.stream().collect(
                Collectors.groupingBy(p -> p.getMetodoPago() != null ? p.getMetodoPago() : "otro",
                        Collectors.counting()));
        model.addAttribute("porMetodoPago", porMetodoPago);

        Map<String, Long> porTipoEntrega = pedidos.stream().collect(
                Collectors.groupingBy(p -> p.getTipoEntrega() != null ? p.getTipoEntrega() : "otro",
                        Collectors.counting()));
        model.addAttribute("porTipoEntrega", porTipoEntrega);

        model.addAttribute("pedidosRecientes", pedidos.stream().limit(6).toList());
        model.addAttribute("productosBajoStock", productoService.obtenerProductosBajoStock(10));
        model.addAttribute("adminPage", "dashboard");
        return "admin/dashboard";
    }

    // ===================== PEDIDOS =====================

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.obtenerTodos());
        model.addAttribute("adminPage", "pedidos");
        return "admin/pedidos";
    }

    // ===================== PRODUCTOS CRUD =====================

    @GetMapping("/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        model.addAttribute("adminPage", "productos");
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("adminPage", "productos");
        return "admin/formulario-producto";
    }

    @PostMapping("/productos/nuevo")
    public String guardarProducto(@ModelAttribute Producto producto,
                                  BindingResult bindingResult,
                                  @RequestParam(required = false) Long categoriaId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        productoValidator.validate(producto, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoriaSeleccionada", categoriaId);
            model.addAttribute("adminPage", "productos");
            return "admin/formulario-producto";
        }
        if (categoriaId != null) {
            Categoria cat = productoService.obtenerCategoriaPorId(categoriaId);
            producto.setCategoria(cat);
        }
        producto.setDisponible(true);
        productoService.guardarProducto(producto);
        redirectAttributes.addFlashAttribute("mensajeAdmin", "OK:Producto creado exitosamente.");
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProductoForm(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto == null) return "redirect:/admin/productos";
        model.addAttribute("producto", producto);
        model.addAttribute("categoriaSeleccionada",
                producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        model.addAttribute("editando", true);
        model.addAttribute("adminPage", "productos");
        return "admin/formulario-producto";
    }

    @PostMapping("/productos/editar/{id}")
    public String actualizarProducto(@PathVariable Long id,
                                     @ModelAttribute Producto producto,
                                     BindingResult bindingResult,
                                     @RequestParam(required = false) Long categoriaId,
                                     @RequestParam(defaultValue = "false") boolean disponible,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        productoValidator.validate(producto, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoriaSeleccionada", categoriaId);
            model.addAttribute("editando", true);
            model.addAttribute("adminPage", "productos");
            return "admin/formulario-producto";
        }
        producto.setId(id);
        producto.setDisponible(disponible);
        if (categoriaId != null) {
            Categoria cat = productoService.obtenerCategoriaPorId(categoriaId);
            producto.setCategoria(cat);
        }
        productoService.guardarProducto(producto);
        redirectAttributes.addFlashAttribute("mensajeAdmin", "OK:Producto actualizado exitosamente.");
        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.eliminarProducto(id);
        redirectAttributes.addFlashAttribute("mensajeAdmin", "OK:Producto eliminado.");
        return "redirect:/admin/productos";
    }
}
