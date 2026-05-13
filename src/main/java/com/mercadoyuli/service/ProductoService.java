package com.mercadoyuli.service;

import com.mercadoyuli.model.Categoria;
import com.mercadoyuli.model.Producto;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();

    @PostConstruct
    public void init() {
        // --- CATEGORIAS ---
        Categoria carnes = new Categoria("Carnes", "carnes",
                "Cortes frescos de res, pollo, cerdo y mas. Directo de nuestros proveedores de confianza.",
                "bi-egg-fried", "https://images.unsplash.com/photo-1529694157872-4e0c0f3b238b?w=600&q=80");
        carnes.setId(1L);

        Categoria verduras = new Categoria("Verduras", "verduras",
                "Verduras frescas del campo peruano. Tomates, lechugas, zanahorias y mucho mas.",
                "bi-tree", "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=600&q=80");
        verduras.setId(2L);

        Categoria frutas = new Categoria("Frutas", "frutas",
                "Frutas tropicales y de temporada. Mangos, fresas, platanos y mas delicias naturales.",
                "bi-apple", "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=600&q=80");
        frutas.setId(3L);

        Categoria abarrotes = new Categoria("Abarrotes", "abarrotes",
                "Productos de despensa: arroz, aceite, fideos, conservas y todo lo esencial para tu cocina.",
                "bi-basket2", "https://images.unsplash.com/photo-1534723452862-4c874018d66d?w=600&q=80");
        abarrotes.setId(4L);

        Categoria juguerias = new Categoria("Juguerias", "juguerias",
                "Jugos naturales, extractos y batidos preparados al momento con frutas frescas del mercado.",
                "bi-cup-straw", "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=600&q=80");
        juguerias.setId(5L);

        categorias.addAll(List.of(carnes, verduras, frutas, abarrotes, juguerias));

        // --- PRODUCTOS ---
        long idCounter = 1L;

        // Carnes
        productos.add(crearProducto(idCounter++, "Pollo Entero", "Pollo fresco de granja, ideal para guisos, estofados y al horno. Peso aproximado de 2 kg.", 14.50, "Avicola Don Pepe", "kg", "/images/polloEntero.png", carnes));
        productos.add(crearProducto(idCounter++, "Carne de Res - Bistec", "Corte fino de res para parrilla o saltado. Carne tierna seleccionada.", 28.00, "Carniceria El Toro", "kg", "/images/carneRes.png", carnes));
        productos.add(crearProducto(idCounter++, "Chuleta de Cerdo", "Chuleta de cerdo fresca con hueso, perfecta para frituras y guisos.", 22.00, "Carniceria Yuli", "kg", "/images/chuletaCerdo.png", carnes));
        productos.add(crearProducto(idCounter++, "Higado de Res", "Higado fresco de res, rico en hierro. Ideal para saltados y guisos nutritivos.", 15.00, "Carniceria El Toro", "kg", "/images/higadoRes.png", carnes));
        productos.add(crearProducto(idCounter++, "Mollejas de Pollo", "Mollejas frescas limpias, listas para preparar en guiso o a la plancha.", 10.00, "Avicola Don Pepe", "kg", "/images/mollejaPollo.png", carnes));
        productos.add(crearProducto(idCounter++, "Carne Molida", "Carne molida de res seleccionada, ideal para albondigas, pasta y rellenos.", 24.00, "Carniceria Yuli", "kg", "/images/carneMolida.png", carnes));

        // Verduras
        productos.add(crearProducto(idCounter++, "Tomate", "Tomate rojo maduro de campo peruano. Firme y jugoso, perfecto para ensaladas y guisos.", 4.50, "Verduras Frescas SAC", "kg", "/images/tomate.png", verduras));
        productos.add(crearProducto(idCounter++, "Papa Amarilla", "Papa amarilla seleccionada de Huancavelica. Textura suave ideal para causa y pure.", 5.00, "Distribuidora Andina", "kg", "/images/papaAmarilla.png", verduras));
        productos.add(crearProducto(idCounter++, "Cebolla Roja", "Cebolla roja fresca de Arequipa. Sabor intenso para ensaladas y aderezos.", 3.50, "Verduras Frescas SAC", "kg", "/images/cebollaRoja.png", verduras));
        productos.add(crearProducto(idCounter++, "Zanahoria", "Zanahoria fresca y crujiente. Rica en vitamina A, ideal para jugos y ensaladas.", 3.00, "Distribuidora Andina", "kg", "/images/zanahoria.png", verduras));
        productos.add(crearProducto(idCounter++, "Lechuga", "Lechuga crespa fresca y crujiente. Perfecta para ensaladas y hamburguesas.", 2.50, "Verduras Frescas SAC", "unidad", "/images/lechuga.png", verduras));
        productos.add(crearProducto(idCounter++, "Aji Amarillo", "Aji amarillo fresco, indispensable para la cocina peruana. Picor moderado.", 8.00, "Distribuidora Andina", "kg", "/images/ajiAmarillo.png", verduras));

        // Frutas
        productos.add(crearProducto(idCounter++, "Platano de Isla", "Platano dulce y maduro, fuente natural de potasio y energia.", 3.50, "Fruteria Tropical", "kg", "/images/platanoIsla.png", frutas));
        productos.add(crearProducto(idCounter++, "Mango Kent", "Mango Kent jugoso y dulce de Piura. Ideal para jugos, postres y consumo directo.", 6.00, "Frutas del Norte", "kg", "/images/mangoKent.png", frutas));
        productos.add(crearProducto(idCounter++, "Fresa", "Fresas frescas de Canta. Rojas, dulces y aromaticas para postres y batidos.", 8.00, "Fruteria Tropical", "kg", "/images/fresa.png", frutas));
        productos.add(crearProducto(idCounter++, "Mandarina", "Mandarina dulce de temporada. Facil de pelar, rica en vitamina C.", 4.00, "Frutas del Norte", "kg", "/images/mandarina.png", frutas));
        productos.add(crearProducto(idCounter++, "Papaya", "Papaya madura y dulce, excelente para jugos y desayunos saludables.", 5.50, "Fruteria Tropical", "kg", "/images/papaya.png", frutas));
        productos.add(crearProducto(idCounter++, "Sandia", "Sandia roja y refrescante, perfecta para los dias calurosos de Lima.", 2.50, "Frutas del Norte", "kg", "/images/sandia.png", frutas));

        // Abarrotes
        productos.add(crearProducto(idCounter++, "Arroz Extra", "Arroz extra graneadito de 5 kg. Rinde para toda la familia.", 22.00, "Abarrotes Yuli", "unidad", "/images/arroz.png", abarrotes));
        productos.add(crearProducto(idCounter++, "Aceite Vegetal", "Aceite vegetal de 1 litro para freir y cocinar. Calidad garantizada.", 9.50, "Distribuidora Central", "unidad", "/images/aceiteVegetal.png", abarrotes));
        productos.add(crearProducto(idCounter++, "Fideos Spaghetti", "Fideos spaghetti de 500g. Perfectos para tallarines y pastas al estilo peruano.", 3.80, "Abarrotes Yuli", "unidad", "/images/fideos.png", abarrotes));
        productos.add(crearProducto(idCounter++, "Azucar Rubia", "Azucar rubia de 1 kg para postres, jugos y la cocina diaria.", 4.50, "Distribuidora Central", "unidad", "/images/azucar.png", abarrotes));
        productos.add(crearProducto(idCounter++, "Leche Evaporada", "Leche evaporada entera de 400g. Ideal para desayunos y reposteria.", 4.20, "Abarrotes Yuli", "unidad", "/images/leche.png", abarrotes));
        productos.add(crearProducto(idCounter++, "Atun en Conserva", "Atun en conserva en aceite vegetal, practico y nutritivo.", 5.50, "Distribuidora Central", "unidad", "/images/atun.png", abarrotes));

        // Juguerias
        productos.add(crearProducto(idCounter++, "Jugo de Naranja", "Jugo de naranja natural recien exprimido. Vaso de 500ml lleno de vitamina C.", 5.00, "Jugueria La Vida", "vaso", "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400&q=80", juguerias));
        productos.add(crearProducto(idCounter++, "Extracto de Zanahoria", "Extracto puro de zanahoria con un toque de limon. Vaso de 350ml.", 4.50, "Jugueria Salud Total", "vaso", "https://images.unsplash.com/photo-1615478503562-ec2d8aa0e24e?w=400&q=80", juguerias));
        productos.add(crearProducto(idCounter++, "Batido de Fresa", "Batido cremoso de fresa con leche. Vaso de 500ml refrescante y dulce.", 7.00, "Jugueria La Vida", "vaso", "https://images.unsplash.com/photo-1553361371-9b22f78e8b1d?w=400&q=80", juguerias));
        productos.add(crearProducto(idCounter++, "Jugo de Papaya", "Jugo natural de papaya con un toque de miel. Vaso de 500ml.", 5.50, "Jugueria Salud Total", "vaso", "https://images.unsplash.com/photo-1622597467836-f3285f2131b8?w=400&q=80", juguerias));
        productos.add(crearProducto(idCounter++, "Jugo Surtido", "Mezcla de frutas de temporada: mango, papaya y platano. Vaso de 500ml.", 6.50, "Jugueria La Vida", "vaso", "https://images.unsplash.com/photo-1502741224143-90386d7f8c82?w=400&q=80", juguerias));
        productos.add(crearProducto(idCounter++, "Emoliente", "Emoliente caliente con linaza, boldo y limon. La bebida tradicional peruana.", 3.00, "Jugueria Salud Total", "vaso", "https://images.unsplash.com/photo-1544145945-f90425340c7e?w=400&q=80", juguerias));
    }

    private Producto crearProducto(Long id, String nombre, String desc, double precio,
                                   String proveedor, String unidad, String img, Categoria cat) {
        Producto p = new Producto(nombre, desc, precio, proveedor, unidad, img, true, cat);
        p.setId(id);
        return p;
    }

    // --- Metodos de consulta ---

    public List<Categoria> obtenerCategorias() {
        return categorias;
    }

    public Categoria obtenerCategoriaPorSlug(String slug) {
        return categorias.stream()
                .filter(c -> c.getSlug().equalsIgnoreCase(slug))
                .findFirst()
                .orElse(null);
    }

    public List<Producto> obtenerProductosPorCategoria(String slug) {
        return productos.stream()
                .filter(p -> p.getCategoria().getSlug().equalsIgnoreCase(slug))
                .collect(Collectors.toList());
    }

    public Producto obtenerProductoPorId(Long id) {
        return productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Producto> obtenerProductosDestacados() {
        // 2 productos de cada categoria
        List<Producto> destacados = new ArrayList<>();
        for (Categoria cat : categorias) {
            productos.stream()
                .filter(p -> p.getCategoria().getSlug().equals(cat.getSlug()))
                .limit(2)
                .forEach(destacados::add);
        }
        return destacados;
    }

    public List<Producto> obtenerProductosOferta() {
        // Productos de indices pares (simulacion de oferta)
        List<Producto> oferta = new ArrayList<>();
        for (int i = 0; i < productos.size(); i++) {
            if (i % 3 == 0) oferta.add(productos.get(i));
        }
        return oferta;
    }

    public List<Producto> buscarProductos(String query) {
        String q = query.toLowerCase();
        return productos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(q)
                        || p.getProveedor().toLowerCase().contains(q)
                        || p.getCategoria().getNombre().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }
}
