package com.mercadoyuli.controller;

import com.mercadoyuli.model.CarritoItem;
import com.mercadoyuli.model.Pedido;
import com.mercadoyuli.model.PedidoEntity;
import com.mercadoyuli.model.Usuario;
import com.mercadoyuli.service.PedidoService;
import com.mercadoyuli.model.Producto;
import com.mercadoyuli.service.CarritoService;
import com.mercadoyuli.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private final PedidoService pedidoService;

    public CarritoController(CarritoService carritoService, ProductoService productoService,
                             PedidoService pedidoService) {
        this.carritoService = carritoService;
        this.productoService = productoService;
        this.pedidoService = pedidoService;
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
    public String checkout(Model model, HttpSession session) {
        if (carritoService.estaVacio()) {
            return "redirect:/carrito";
        }
        Pedido pedido = new Pedido();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null) {
            pedido.setNombreCliente(usuario.getNombre());
            pedido.setEmailCliente(usuario.getEmail());
            pedido.setTelefonoCliente(usuario.getTelefono());
            pedido.setDni(usuario.getDni());
            pedido.setDireccion(usuario.getDireccion());
        }
        model.addAttribute("items", carritoService.obtenerItems());
        model.addAttribute("subtotal", carritoService.obtenerSubtotal());
        model.addAttribute("descuento", carritoService.obtenerDescuento());
        model.addAttribute("total", carritoService.obtenerTotal());
        model.addAttribute("codigoAplicado", carritoService.getCodigoAplicado());
        model.addAttribute("carritoCount", carritoService.obtenerCantidadTotal());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        model.addAttribute("pedido", pedido);
        return "checkout/formulario";
    }

    @PostMapping("/checkout/confirmar")
    public String confirmarPedido(@Valid @ModelAttribute Pedido pedido,
                                  BindingResult bindingResult,
                                  Model model) {
        java.util.Map<String, String> errores = new java.util.HashMap<>();

        // Convertir errores de Spring Validation al mapa que usa el template
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(fe -> {
                String key = switch (fe.getField()) {
                    case "nombreCliente" -> "nombre";
                    case "emailCliente"  -> "email";
                    case "telefonoCliente" -> "telefono";
                    default -> fe.getField();
                };
                errores.putIfAbsent(key, fe.getDefaultMessage());
            });
        }

        // Validacion condicional: direccion requerida solo si es envio
        if ("envio".equals(pedido.getTipoEntrega()) &&
            (pedido.getDireccion() == null || pedido.getDireccion().trim().isEmpty()))
            errores.put("direccion", "Ingresa tu direccion de envio.");

        // Validacion condicional: campos de tarjeta
        if ("tarjeta".equals(pedido.getMetodoPago())) {
            String numTarjeta = pedido.getNumeroTarjeta() != null
                    ? pedido.getNumeroTarjeta().replaceAll("\\s", "") : "";
            if (!numTarjeta.matches("[0-9]{16}"))
                errores.put("numeroTarjeta", "El numero de tarjeta debe tener 16 digitos.");
            if (pedido.getCvv() == null || !pedido.getCvv().matches("[0-9]{3,4}"))
                errores.put("cvv", "El CVV debe tener 3 o 4 digitos.");
            if (pedido.getVencimiento() == null || !pedido.getVencimiento().matches("(0[1-9]|1[0-2])/[0-9]{2}"))
                errores.put("vencimiento", "Formato invalido. Usa MM/AA (ej: 08/27).");
            if (pedido.getTitular() == null || pedido.getTitular().trim().length() < 3)
                errores.put("titular", "Ingresa el nombre del titular de la tarjeta.");
        }

        // Verificar stock disponible
        for (CarritoItem item : carritoService.obtenerItems()) {
            Producto p = productoService.obtenerProductoPorId(item.getProductoId());
            if (p != null && p.getStock() < item.getCantidad()) {
                errores.put("stock", "Stock insuficiente para \"" + p.getNombre() +
                        "\" (disponible: " + p.getStock() + ")");
                break;
            }
        }

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

        // Guardar pedido en base de datos PostgreSQL
        pedidoService.guardarPedido(
            pedido,
            carritoService.obtenerItems(),
            carritoService.obtenerSubtotal(),
            carritoService.obtenerDescuento(),
            carritoService.obtenerTotal(),
            carritoService.getCodigoAplicado()
        );

        // Reducir stock de cada producto
        for (CarritoItem item : pedido.getItems()) {
            productoService.reducirStock(item.getProductoId(), item.getCantidad());
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("carritoCount", 0);
        model.addAttribute("categorias", productoService.obtenerCategorias());

        // Vaciar carrito despues de confirmar
        carritoService.vaciarCarrito();

        return "checkout/confirmacion";
    }
}
