package com.mercadoyuli.controller;

import com.mercadoyuli.model.Pedido;
import com.mercadoyuli.model.PedidoEntity;
import com.mercadoyuli.service.PedidoService;
import com.mercadoyuli.model.Producto;
import com.mercadoyuli.service.CarritoService;
import com.mercadoyuli.service.ProductoService;
import com.mercadoyuli.service.UsuarioService;
import com.mercadoyuli.validator.PedidoValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final PedidoValidator pedidoValidator;
    private final UsuarioService usuarioService;

    public CarritoController(CarritoService carritoService, ProductoService productoService,
                             PedidoService pedidoService, PedidoValidator pedidoValidator,
                             UsuarioService usuarioService) {
        this.carritoService = carritoService;
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.pedidoValidator = pedidoValidator;
        this.usuarioService = usuarioService;
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

    // ===================== API JSON (carrito flotante, sin recargar) =====================

    @PostMapping("/carrito/api/agregar")
    @ResponseBody
    public Map<String, Object> apiAgregar(@RequestParam Long productoId,
                                          @RequestParam(defaultValue = "1") int cantidad) {
        Producto producto = productoService.obtenerProductoPorId(productoId);
        String nombre = "";
        if (producto != null) {
            carritoService.agregarProducto(producto, cantidad);
            nombre = producto.getNombre();
        }
        Map<String, Object> r = new HashMap<>();
        r.put("count", carritoService.obtenerCantidadTotal());
        r.put("nombre", nombre);
        return r;
    }

    @GetMapping("/carrito/api/items")
    @ResponseBody
    public Map<String, Object> apiItems() {
        Map<String, Object> r = new HashMap<>();
        r.put("items", carritoService.obtenerItems());
        r.put("subtotal", carritoService.obtenerSubtotal());
        r.put("total", carritoService.obtenerTotal());
        r.put("count", carritoService.obtenerCantidadTotal());
        return r;
    }

    @PostMapping("/carrito/api/eliminar")
    @ResponseBody
    public Map<String, Object> apiEliminar(@RequestParam Long productoId) {
        carritoService.eliminarProducto(productoId);
        return apiItems();
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

        // Pre-cargar los datos del usuario autenticado (si lo hay)
        Pedido pedido = new Pedido();
        usuarioService.usuarioActual().ifPresent(usuario -> {
            pedido.setNombreCliente(usuario.getNombre());
            pedido.setDni(usuario.getDni());
            pedido.setEmailCliente(usuario.getEmail());
            pedido.setTelefonoCliente(usuario.getTelefono());
            pedido.setDireccion(usuario.getDireccion());
        });
        model.addAttribute("pedido", pedido);
        return "checkout/formulario";
    }

    @PostMapping("/checkout/confirmar")
    public String confirmarPedido(@ModelAttribute Pedido pedido, Model model) {
        // Validacion del lado del servidor con el Spring Validator
        BindingResult binding = new BeanPropertyBindingResult(pedido, "pedido");
        pedidoValidator.validate(pedido, binding);

        if (binding.hasErrors()) {
            // Traducir los errores del Spring Validator a las claves que usa la plantilla
            Map<String, String> errores = new HashMap<>();
            binding.getFieldErrors().forEach(fe -> errores.putIfAbsent(
                    mapearCampo(fe.getField()), fe.getDefaultMessage()));

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

        // Guardar pedido en base de datos PostgreSQL
        pedidoService.guardarPedido(
            pedido,
            carritoService.obtenerItems(),
            carritoService.obtenerSubtotal(),
            carritoService.obtenerDescuento(),
            carritoService.obtenerTotal(),
            carritoService.getCodigoAplicado()
        );

        // Descontar stock de cada producto comprado (se refleja en el panel admin)
        carritoService.obtenerItems().forEach(item ->
                productoService.reducirStock(item.getProductoId(), item.getCantidad()));

        model.addAttribute("pedido", pedido);
        model.addAttribute("carritoCount", 0);
        model.addAttribute("categorias", productoService.obtenerCategorias());

        // Vaciar carrito despues de confirmar
        carritoService.vaciarCarrito();

        return "checkout/confirmacion";
    }

    // Traduce el nombre del campo del modelo Pedido a la clave usada en la plantilla
    private String mapearCampo(String campo) {
        return switch (campo) {
            case "nombreCliente"   -> "nombre";
            case "emailCliente"    -> "email";
            case "telefonoCliente" -> "telefono";
            default                -> campo; // dni, direccion
        };
    }
}
