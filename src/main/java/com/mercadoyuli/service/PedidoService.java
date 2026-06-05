package com.mercadoyuli.service;

import com.mercadoyuli.model.CarritoItem;
import com.mercadoyuli.model.Pedido;
import com.mercadoyuli.model.PedidoEntity;
import com.mercadoyuli.model.PedidoItem;
import com.mercadoyuli.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public PedidoEntity guardarPedido(Pedido pedido, List<CarritoItem> items,
                                      double subtotal, double descuento, double total,
                                      String codigoAplicado) {
        PedidoEntity entity = new PedidoEntity();
        entity.setNumeroPedido(pedido.getNumeroPedido());
        entity.setNombreCliente(pedido.getNombreCliente());
        entity.setDniCliente(pedido.getDni());
        entity.setEmailCliente(pedido.getEmailCliente());
        entity.setTelefonoCliente(pedido.getTelefonoCliente());
        entity.setTipoEntrega(pedido.getTipoEntrega());
        entity.setDireccion(pedido.getDireccion());
        entity.setDistrito(pedido.getDistrito());
        entity.setMetodoPago(pedido.getMetodoPago());
        entity.setSubtotal(subtotal);
        entity.setDescuento(descuento);
        entity.setTotal(total);
        entity.setCodigoAplicado(codigoAplicado);

        // Convertir CarritoItems a PedidoItems
        List<PedidoItem> pedidoItems = new ArrayList<>();
        for (CarritoItem item : items) {
            PedidoItem pi = new PedidoItem();
            pi.setPedido(entity);
            pi.setProductoId(item.getProductoId());
            pi.setNombreProducto(item.getNombre());
            pi.setPrecioUnitario(item.getPrecioUnitario());
            pi.setCantidad(item.getCantidad());
            pi.setSubtotal(item.getSubtotal());
            pedidoItems.add(pi);
        }
        entity.setItems(pedidoItems);

        return pedidoRepository.save(entity);
    }

    public List<PedidoEntity> obtenerTodos() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc();
    }

    public List<PedidoEntity> obtenerPorEmail(String email) {
        return pedidoRepository.findByEmailClienteOrderByFechaPedidoDesc(email);
    }
}
