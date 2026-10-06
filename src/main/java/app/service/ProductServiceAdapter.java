package app.service;

import app.domain.Products;
import app.service.inputports.ProductServiceInterface;
import app.service.outputports.ProductRepositoryPort;
import java.util.List;

public class ProductServiceAdapter implements ProductServiceInterface {
    // Referencia al repositorio que almacena los productos.
    private final ProductRepositoryPort productRepositoryPort;

    // Recibe el repositorio por el constructor.
    public ProductServiceAdapter(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public Products createProduct(Integer id, String name, Float price, String type) {
        // Construye el producto y solicita guardarlo.
        Products product = new Products(id, name, price, type);
        return productRepositoryPort.save(product);
    }

    @Override
    public Products selectById(Integer id) {
        // Devuelve el producto encontrado o null si no existe.
        return productRepositoryPort.selectById(id);
    }

    @Override
    public List<Products> selectAllProducts() {
        // Solicita al repositorio todos los productos.
        return productRepositoryPort.selectAllProducts();
    }

    @Override
    public Products updateProduct(Integer id, String name, Float price, String type) {
        // Construye un objeto con el mismo ID y los nuevos datos.
        Products product = new Products(id, name, price, type);
        return productRepositoryPort.updateProduct(product);
    }

    @Override
    public void deleteById(Integer id) {
        // Solicita eliminar el producto indicado.
        productRepositoryPort.deleteById(id);
    }
}
