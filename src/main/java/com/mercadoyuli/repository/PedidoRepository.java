package com.mercadoyuli.repository;

import com.mercadoyuli.model.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
    List<PedidoEntity> findAllByOrderByFechaPedidoDesc();
    List<PedidoEntity> findByEmailClienteOrderByFechaPedidoDesc(String emailCliente);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM PedidoEntity p")
    double sumTotalRevenue();
}
