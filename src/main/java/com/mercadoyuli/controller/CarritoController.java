package com.mercadoyuli.controller;

import com.mercadoyuli.model.Pedido;
import com.mercadoyuli.model.Producto;
import com.mercadoyuli.service.CarritoService;
import com.mercadoyuli.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@Controller
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoService productoService;

    public CarritoController(CarritoService carritoService, ProductoService productoService) {
        this.carritoService = carritoService;
        this.productoService = productoService;
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam Long productoId,
                                   @RequestParam(defaultValue = "1") int cantidad,
                                   RedirectAttributes redirectAttributes) {
        Producto producto = productoService.obtenerProductoPorId(productoId);
        if (producto != null) {
            carritoService.agregarProducto(producto, cantidad);
            redirectAttributes.addFlashAttribute("mensaje",
                    producto.getNombre() + " agregado al carrito");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/agregar-rapido")
    public String agregarRapido(@RequestParam Long productoId,
                                @RequestParam(defaultValue = "1") int cantidad,
                                @RequestParam String returnUrl,
                                RedirectAttributes redirectAttributes) {
        Producto producto = productoService.obtenerProductoPorId(productoId);
        if (producto != null) {
            carritoService.agregarProducto(producto, cantidad);
            redirectAttributes.addFlashAttribute("mensaje",
                    producto.getNombre() + " agregado al carrito");
        }
        return "redirect:" + returnUrl;
    }

    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoService.obtenerItems());
        model.addAttribute("subtotal", carritoService.obtenerSubtotal());
        model.addAttribute("descuento", carritoService.obtenerDescuento());
        model.addAttribute("total", carritoService.obtenerTotal());
        model.addAttribute("codigoAplicado", carritoService.getCodigoAplicado());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        return "carrito/ver";
    }

    @PostMapping("/carrito/aplicar-codigo")
    public String aplicarCodigo(@RequestParam String codigo, RedirectAttributes redirectAttributes) {
        String resultado = carritoService.aplicarCodigo(codigo);
        switch (resultado) {
            case "OK"       -> redirectAttributes.addFlashAttribute("codigoMensaje", "OK:Codigo YULI10 aplicado. Tienes 10% de descuento!");
            case "INVALIDO" -> redirectAttributes.addFlashAttribute("codigoMensaje", "ERROR:Codigo invalido. Verifica e intenta de nuevo.");
            case "YA_USADO" -> redirectAttributes.addFlashAttribute("codigoMensaje", "ERROR:Este codigo ya fue utilizado en un pedido anterior.");
        }
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/quitar-codigo")
    public String quitarCodigo() {
        carritoService.quitarCodigo();
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/actualizar")
    public String actualizarCantidad(@RequestParam Long productoId,
                                     @RequestParam int cantidad) {
        carritoService.actualizarCantidad(productoId, cantidad);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam Long productoId) {
        carritoService.eliminarProducto(productoId);
        return "redirect:/carrito";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        if (carritoService.estaVacio()) {
            return "redirect:/carrito";
        }
        model.addAttribute("items", carritoService.obtenerItems());
        model.addAttribute("subtotal", carritoService.obtenerSubtotal());
        model.addAttribute("descuento", carritoService.obtenerDescuento());
        model.addAttribute("total", carritoService.obtenerTotal());
        model.addAttribute("codigoAplicado", carritoService.getCodigoAplicado());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("pedido", new Pedido());
        return "checkout/formulario";
    }

    @PostMapping("/checkout/confirmar")
    public String confirmarPedido(@ModelAttribute Pedido pedido, Model model) {
        // Validar campos obligatorios del lado del servidor
        java.util.Map<String, String> errores = new java.util.HashMap<>();

        if (pedido.getNombreCliente() == null || pedido.getNombreCliente().trim().length() < 3)
            errores.put("nombre", "Ingresa tu nombre completo.");
        if (pedido.getDni() == null || !pedido.getDni().matches("[0-9]{8}"))
            errores.put("dni", "El DNI debe tener exactamente 8 digitos.");
        if (pedido.getEmailCliente() == null || !pedido.getEmailCliente().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            errores.put("email", "Ingresa un correo electronico valido.");
        if (pedido.getTelefonoCliente() == null || !pedido.getTelefonoCliente().matches("[0-9]{9}"))
            errores.put("telefono", "El telefono debe tener 9 digitos.");
        if ("envio".equals(pedido.getTipoEntrega()) &&
            (pedido.getDireccion() == null || pedido.getDireccion().trim().isEmpty()))
            errores.put("direccion", "Ingresa tu direccion de envio.");

        if (!errores.isEmpty()) {
            model.addAttribute("items", carritoService.obtenerItems());
            model.addAttribute("subtotal", carritoService.obtenerSubtotal());
            model.addAttribute("descuento", carritoService.obtenerDescuento());
            model.addAttribute("total", carritoService.obtenerTotal());
            model.addAttribute("codigoAplicado", carritoService.getCodigoAplicado());
            model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
            model.addAttribute("categorias", productoService.obtenerCategorias());
            model.addAttribute("pedido", pedido);
            model.addAttribute("errores", errores);
            return "checkout/formulario";
        }

        // Generar numero de pedido
        String numPedido = "MY-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        pedido.setNumeroPedido(numPedido);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setItems(new ArrayList<>(carritoService.obtenerItems()));
        pedido.setTotal(carritoService.obtenerTotal());

        model.addAttribute("pedido", pedido);
        model.addAttribute("carritoCount", 0);
        model.addAttribute("categorias", productoService.obtenerCategorias());

        // Vaciar carrito despues de confirmar
        carritoService.vaciarCarrito();

        return "checkout/confirmacion";
    }
}
