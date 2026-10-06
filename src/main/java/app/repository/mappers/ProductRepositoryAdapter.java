package app.repository.mappers;

import app.domain.Products;
import app.service.outputports.ProductRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductRepositoryAdapter implements ProductRepositoryPort {

    // Almacena los productos en memoria mientras la aplicación está abierta.
    private final List<Products> products = new ArrayList<>();


    @Override
    public Products save(Products product) {
        // products es la lista; product es el objeto recibido.
        products.add(product);
        // Devuelve el producto guardado.
        return product;
    }

    @Override
    public Products selectById(Integer id) {
        // Recorre los productos y busca el ID solicitado.
        for (Products product : products) {
            // Compara los valores de los IDs.
            if (Objects.equals(product.getProductId(), id)) {
                return product;
            }
        }
        // null indica que no encontró el producto.
        return null;
    }

    @Override
    public List<Products> selectAllProducts() {
        // Devuelve una copia de la lista con los productos guardados.
        return new ArrayList<>(products);
    }

    @Override
    public Products updateProduct(Products product) {
        // Recorre las posiciones de la lista desde cero.
        for (int i = 0; i < products.size(); i++) {
            Products savedProduct = products.get(i);
            // Busca el producto que tenga el mismo ID.
            if (Objects.equals(savedProduct.getProductId(), product.getProductId())) {
                // Reemplaza el producto por el objeto con los nuevos datos.
                products.set(i, product);
                return product;
            }
        }
        // Si el ID no existe, no modifica la lista.
        return null;
    }

    @Override
    public void deleteById(Integer id) {
        // Busca la posición del producto que se quiere eliminar.
        for (int i = 0; i < products.size(); i++) {
            Products product = products.get(i);
            if (Objects.equals(product.getProductId(), id)) {
                // Elimina el producto y termina el método.
                products.remove(i);
                return;
            }
        }
    }
}
