package app.view;

import app.domain.Products;
import app.service.inputports.ProductServiceInterface;
import app.service.validators.DataTypeValidator;
import java.util.List;

public class ProductView {
    // La vista utiliza el servicio para gestionar los productos.
    private final ProductServiceInterface productService;

    // Recibe el servicio que se construye en Config.
    public ProductView(ProductServiceInterface productService) {
        this.productService = productService;
    }

    public void createProduct() {
        // Pide los datos usando los validadores que ya tiene el proyecto.
        int id = DataTypeValidator.validateInt("Ingrese el ID del producto:");
        String name = DataTypeValidator.validateString("Ingrese el nombre del producto:");
        Float price = DataTypeValidator.validateFloat("Ingrese el precio del producto:");
        String type = DataTypeValidator.validateString("Ingrese el tipo de producto:");
        productService.createProduct(id, name, price, type);
        System.out.println("Producto registrado correctamente.");
    }

    public void selectById() {
        // Busca el producto antes de mostrar sus datos.
        int id = DataTypeValidator.validateInt("Ingrese el ID del producto a consultar:");
        Products product = productService.selectById(id);
        if (product == null) {
            System.out.println("No existe un producto con ese ID.");
            return;
        }
        System.out.println(product.getProductId() + " | " + product.getProductName()
                + " | " + product.getProductPrice() + " | " + product.getProductType());
    }

    public void selectAllProducts() {
        // Obtiene la lista y comprueba si tiene productos.
        List<Products> products = productService.selectAllProducts();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        // Muestra cada producto de la lista.
        for (Products product : products) {
            System.out.println(product.getProductId() + " | " + product.getProductName()
                    + " | " + product.getProductPrice() + " | " + product.getProductType());
        }
    }

    public void updateProduct() {
        int id = DataTypeValidator.validateInt("Ingrese el ID del producto a actualizar:");
        // Verifica que exista antes de pedir los nuevos datos.
        if (productService.selectById(id) == null) {
            System.out.println("No existe un producto con ese ID.");
            return;
        }
        String name = DataTypeValidator.validateString("Ingrese el nuevo nombre:");
        Float price = DataTypeValidator.validateFloat("Ingrese el nuevo precio:");
        String type = DataTypeValidator.validateString("Ingrese el nuevo tipo:");
        Products product = productService.updateProduct(id, name, price, type);
        if (product != null) {
            System.out.println("Producto actualizado correctamente.");
        } else {
            System.out.println("No existe un producto con ese ID.");
        }
    }

    public void deleteById() {
        int id = DataTypeValidator.validateInt("Ingrese el ID del producto a eliminar:");
        // Evita anunciar una eliminación cuando el producto no existe.
        if (productService.selectById(id) == null) {
            System.out.println("No existe un producto con ese ID.");
            return;
        }
        productService.deleteById(id);
        System.out.println("Producto eliminado correctamente.");
    }
}
