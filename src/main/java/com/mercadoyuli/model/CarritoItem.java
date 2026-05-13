package com.mercadoyuli.model;

public class CarritoItem {

    private Long productoId;
    private String nombre;
    private String proveedor;
    private String imagenUrl;
    private String unidad;
    private double precioUnitario;
    private int cantidad;

    public CarritoItem() {}

    public CarritoItem(Long productoId, String nombre, String proveedor,
                       String imagenUrl, String unidad, double precioUnitario, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.proveedor = proveedor;
        this.imagenUrl = imagenUrl;
        this.unidad = unidad;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return precioUnitario * cantidad;
    }

    // --- Getters y Setters ---
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
