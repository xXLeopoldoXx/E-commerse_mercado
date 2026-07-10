package com.mercadoyuli.repository;

import com.mercadoyuli.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaSlug(String slug);
    List<Producto> findByNombreContainingIgnoreCaseOrProveedorContainingIgnoreCase(String nombre, String proveedor);
}
