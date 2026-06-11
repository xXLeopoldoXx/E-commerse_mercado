package com.mercadoyuli.service;

import com.mercadoyuli.model.Categoria;
import com.mercadoyuli.model.Producto;
import com.mercadoyuli.repository.CategoriaRepository;
import com.mercadoyuli.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public ProductoService(CategoriaRepository categoriaRepository,
                           ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @PostConstruct
    public void init() {
        if (categoriaRepository.count() > 0) {
            actualizarStockInicial();
            return;
        }

        Categoria carnes = guardarCat("Carnes", "carnes",
                "Cortes frescos de res, pollo, cerdo y mas.", "bi-egg-fried", "/images/cat-carnes.jpg");
        Categoria verduras = guardarCat("Verduras", "verduras",
                "Verduras frescas del campo peruano.", "bi-tree", "/images/cat-verduras.jpg");
        Categoria frutas = guardarCat("Frutas", "frutas",
                "Frutas tropicales y de temporada.", "bi-apple", "/images/cat-frutas.jpg");
        Categoria abarrotes = guardarCat("Abarrotes", "abarrotes",
                "Productos de despensa esenciales.", "bi-basket2", "/images/cat-abarrotes.jpg");
        Categoria juguerias = guardarCat("Juguerias", "juguerias",
                "Jugos naturales y batidos al momento.", "bi-cup-straw", "/images/cat-juguerias.jpg");

        // Carnes
        guardarProd("Pollo Entero","Pollo fresco de granja.",14.50,"Avicola Don Pepe","kg","/images/polloEntero.png",carnes);
        guardarProd("Carne de Res - Bistec","Corte fino de res.",28.00,"Carniceria El Toro","kg","/images/carneRes.png",carnes);
        guardarProd("Chuleta de Cerdo","Chuleta fresca con hueso.",22.00,"Carniceria Yuli","kg","/images/chuletaCerdo.png",carnes);
        guardarProd("Higado de Res","Higado fresco rico en hierro.",15.00,"Carniceria El Toro","kg","/images/higadoRes.png",carnes);
        guardarProd("Mollejas de Pollo","Mollejas frescas limpias.",10.00,"Avicola Don Pepe","kg","/images/mollejaPollo.png",carnes);
        guardarProd("Carne Molida","Carne molida de res seleccionada.",24.00,"Carniceria Yuli","kg","/images/carneMolida.png",carnes);

        // Verduras
        guardarProd("Tomate","Tomate rojo maduro.",4.50,"Verduras Frescas SAC","kg","/images/tomate.png",verduras);
        guardarProd("Papa Amarilla","Papa amarilla de Huancavelica.",5.00,"Distribuidora Andina","kg","/images/papaAmarilla.png",verduras);
        guardarProd("Cebolla Roja","Cebolla roja fresca.",3.50,"Verduras Frescas SAC","kg","/images/cebollaRoja.png",verduras);
        guardarProd("Zanahoria","Zanahoria fresca y crujiente.",3.00,"Distribuidora Andina","kg","/images/zanahoria.png",verduras);
        guardarProd("Lechuga","Lechuga crespa fresca.",2.50,"Verduras Frescas SAC","unidad","/images/lechuga.png",verduras);
        guardarProd("Aji Amarillo","Aji amarillo fresco.",8.00,"Distribuidora Andina","kg","/images/ajiAmarillo.png",verduras);

        // Frutas
        guardarProd("Platano de Isla","Platano dulce y maduro.",3.50,"Fruteria Tropical","kg","/images/platanoIsla.png",frutas);
        guardarProd("Mango Kent","Mango jugoso de Piura.",6.00,"Frutas del Norte","kg","/images/mangoKent.png",frutas);
        guardarProd("Fresa","Fresas frescas de Canta.",8.00,"Fruteria Tropical","kg","/images/fresa.png",frutas);
        guardarProd("Mandarina","Mandarina dulce de temporada.",4.00,"Frutas del Norte","kg","/images/mandarina.png",frutas);
        guardarProd("Papaya","Papaya madura y dulce.",5.50,"Fruteria Tropical","kg","/images/papaya.png",frutas);
        guardarProd("Sandia","Sandia roja y refrescante.",2.50,"Frutas del Norte","kg","/images/sandia.png",frutas);

        // Abarrotes
        guardarProd("Arroz Extra","Arroz extra graneadito 5kg.",22.00,"Abarrotes Yuli","unidad","/images/arroz.png",abarrotes);
        guardarProd("Aceite Vegetal","Aceite vegetal 1 litro.",9.50,"Distribuidora Central","unidad","/images/aceiteVegetal.png",abarrotes);
        guardarProd("Fideos Spaghetti","Fideos spaghetti 500g.",3.80,"Abarrotes Yuli","unidad","/images/fideos.png",abarrotes);
        guardarProd("Azucar Rubia","Azucar rubia 1kg.",4.50,"Distribuidora Central","unidad","/images/azucar.png",abarrotes);
        guardarProd("Leche Evaporada","Leche evaporada entera 400g.",4.20,"Abarrotes Yuli","unidad","/images/leche.png",abarrotes);
        guardarProd("Atun en Conserva","Atun en conserva en aceite.",5.50,"Distribuidora Central","unidad","/images/atun.png",abarrotes);

        // Juguerias
        guardarProd("Jugo de Naranja","Jugo natural recien exprimido 500ml.",5.00,"Jugueria La Vida","vaso","/images/jugoNaranja.png",juguerias);
        guardarProd("Extracto de Zanahoria","Extracto puro de zanahoria 350ml.",4.50,"Jugueria Salud Total","vaso","/images/extractoZanahoria.png",juguerias);
        guardarProd("Batido de Fresa","Batido cremoso de fresa 500ml.",7.00,"Jugueria La Vida","vaso","/images/batidoFresa.png",juguerias);
        guardarProd("Jugo de Papaya","Jugo de papaya con miel 500ml.",5.50,"Jugueria Salud Total","vaso","/images/jugoPapaya.png",juguerias);
        guardarProd("Jugo Surtido","Mezcla de frutas de temporada 500ml.",6.50,"Jugueria La Vida","vaso","/images/jugoSurtido.png",juguerias);
        guardarProd("Emoliente","Emoliente caliente tradicional.",3.00,"Jugueria Salud Total","vaso","/images/emoliente.png",juguerias);

        actualizarStockInicial();
    }

    private Categoria guardarCat(String nombre, String slug, String desc, String icono, String img) {
        Categoria c = new Categoria(nombre, slug, desc, icono, img);
        return categoriaRepository.save(c);
    }

    private void guardarProd(String nombre, String desc, double precio, String proveedor,
                              String unidad, String img, Categoria cat) {
        Producto p = new Producto(nombre, desc, precio, proveedor, unidad, img, true, cat);
        productoRepository.save(p);
    }

    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria obtenerCategoriaPorSlug(String slug) {
        return categoriaRepository.findBySlug(slug).orElse(null);
    }

    public List<Producto> obtenerProductosPorCategoria(String slug) {
        return productoRepository.findByCategoriaSlug(slug);
    }

    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public List<Producto> obtenerProductosDestacados() {
        List<Producto> destacados = new ArrayList<>();
        for (Categoria cat : obtenerCategorias()) {
            obtenerProductosPorCategoria(cat.getSlug())
                    .stream().limit(2).forEach(destacados::add);
        }
        return destacados;
    }

    public List<Producto> obtenerProductosOferta() {
        List<Producto> todos = productoRepository.findAll();
        List<Producto> oferta = new ArrayList<>();
        for (int i = 0; i < todos.size(); i++) {
            if (i % 3 == 0) oferta.add(todos.get(i));
        }
        return oferta;
    }

    public List<Producto> buscarProductos(String query) {
        return productoRepository
                .findByNombreContainingIgnoreCaseOrProveedorContainingIgnoreCase(query, query);
    }

    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public com.mercadoyuli.model.Categoria obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    private void actualizarStockInicial() {
        List<Producto> todos = productoRepository.findAll();
        if (todos.isEmpty() || todos.stream().anyMatch(p -> p.getStock() > 0)) return;
        int[] stocks = {45, 30, 20, 8, 40, 25,
                        60, 80, 70, 55, 35, 40,
                        50, 25, 30, 60, 40, 35,
                        100, 80, 150, 200, 90, 60,
                        40, 50, 5, 40, 45, 55, 60};
        for (int i = 0; i < todos.size(); i++) {
            todos.get(i).setStock(stocks[Math.min(i, stocks.length - 1)]);
            productoRepository.save(todos.get(i));
        }
    }

    public void reducirStock(Long productoId, int cantidad) {
        Producto p = obtenerProductoPorId(productoId);
        if (p != null) {
            p.setStock(Math.max(0, p.getStock() - cantidad));
            productoRepository.save(p);
        }
    }

    public List<Producto> obtenerProductosBajoStock(int umbral) {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStock() <= umbral)
                .sorted(Comparator.comparingInt(Producto::getStock))
                .collect(Collectors.toList());
    }
}
