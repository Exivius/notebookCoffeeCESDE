package app.service.inputports;

import app.domain.Products;
import java.util.List;
import java.util.Optional;

public interface ProductServiceInterface {
    // Crea un producto con los datos recibidos.
    Products createProduct(Integer id, String name, Float price, String type);

    // Busca un producto por su ID.
    Optional<Products> selectById(Integer id);

    // Consulta todos los productos guardados.
    List<Products> selectAllProducts();

    // Actualiza los datos del producto identificado por su ID.
    Products updateProduct(Integer id, String name, Float price, String type);

    // Elimina un producto por su ID.
    void deleteById(Integer id);
}
