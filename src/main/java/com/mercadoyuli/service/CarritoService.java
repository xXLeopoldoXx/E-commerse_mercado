package com.mercadoyuli.service;

import com.mercadoyuli.model.CarritoItem;
import com.mercadoyuli.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
public class CarritoService {

    private static final String CODIGO_VALIDO = "YULI10";
    private static final double PORCENTAJE_DESCUENTO = 0.10;

    private final List<CarritoItem> items = new ArrayList<>();
    private String codigoAplicado = null;
    private boolean codigoUsado = false;

    public void agregarProducto(Producto producto, int cantidad) {
        Optional<CarritoItem> existente = items.stream()
                .filter(i -> i.getProductoId().equals(producto.getId()))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().setCantidad(existente.get().getCantidad() + cantidad);
        } else {
            items.add(new CarritoItem(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getProveedor(),
                    producto.getImagenUrl(),
                    producto.getUnidad(),
                    producto.getPrecio(),
                    cantidad
            ));
        }
    }

    public void actualizarCantidad(Long productoId, int cantidad) {
        items.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .ifPresent(item -> {
                    if (cantidad <= 0) items.remove(item);
                    else item.setCantidad(cantidad);
                });
    }

    public void eliminarProducto(Long productoId) {
        items.removeIf(i -> i.getProductoId().equals(productoId));
    }

    public List<CarritoItem> obtenerItems() { return items; }

    public int obtenerCantidadTotal() {
        return items.stream().mapToInt(CarritoItem::getCantidad).sum();
    }

    public double obtenerSubtotal() {
        return items.stream().mapToDouble(CarritoItem::getSubtotal).sum();
    }

    public double obtenerDescuento() {
        if (codigoAplicado != null) {
            return Math.round(obtenerSubtotal() * PORCENTAJE_DESCUENTO * 100.0) / 100.0;
        }
        return 0.0;
    }

    public double obtenerTotal() {
        return Math.round((obtenerSubtotal() - obtenerDescuento()) * 100.0) / 100.0;
    }

    // Retorna: "OK", "INVALIDO", "YA_USADO"
    public String aplicarCodigo(String codigo) {
        if (codigoUsado) return "YA_USADO";
        if (codigo != null && codigo.trim().equalsIgnoreCase(CODIGO_VALIDO)) {
            codigoAplicado = CODIGO_VALIDO;
            return "OK";
        }
        return "INVALIDO";
    }

    public void quitarCodigo() {
        codigoAplicado = null;
    }

    public String getCodigoAplicado() { return codigoAplicado; }
    public boolean tieneDescuento() { return codigoAplicado != null; }

    public void vaciarCarrito() {
        items.clear();
        codigoUsado = codigoAplicado != null; // marcar como usado si se aplicó
        codigoAplicado = null;
    }

    public boolean estaVacio() { return items.isEmpty(); }
}
