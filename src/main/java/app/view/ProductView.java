package app.view;

import app.domain.Products;
import app.service.inputports.ProductServiceInterface;
import app.service.validators.DataTypeValidator;
import java.util.List;

public class ProductView {
    // La vista utiliza el servicio para gestionar los productos.
    private final ProductServiceInterface productServiceInterface;

    // Recibe el servicio que se construye en Config.
    public ProductView(ProductServiceInterface productService) {
        this.productServiceInterface = productService;
    }

    public void createProduct() {
        // Pide los datos usando los validadores que ya tiene el proyecto.
        String name = DataTypeValidator.validateString("Ingrese el nombre del producto:");
        Float price = DataTypeValidator.validateFloat("Ingrese el precio del producto:");
        String type = DataTypeValidator.validateString("Ingrese el tipo de producto:");
        Products product = productServiceInterface.createProduct(null, name, price, type);
        //System.out.println("Producto registrado correctamente. ID: " + product.getProductId());
    }

    public void selectById() {
        // Busca el producto antes de mostrar sus datos.
        int id = DataTypeValidator.validateInt("Ingrese el ID del producto a consultar:");
        Products product = productServiceInterface.selectById(id).orElse(null);
        if (product == null) {
            System.out.println("No existe un producto con ese ID.");
            return;
        }
        System.out.println();
        System.out.println("=============================== DATOS DEL PRODUCTO ==============================");
        System.out.printf(
                "%-5s | %-20s | %-10s | %-15s%n",
                "ID", "NOMBRE", "PRECIO", "TIPO"
        );
        System.out.println("-------------------------------------------------------------------------------");
        System.out.printf(
                "%-5d | %-20s | %-10.2f | %-15s%n",
                product.getProductId(),
                product.getProductName(),
                product.getProductPrice(),
                product.getProductType());
        System.out.println("-------------------------------------------------------------------------------");
    }

    public void selectAllProducts() {
        // Obtiene la lista y comprueba si tiene productos.
        List<Products> products = productServiceInterface.selectAllProducts();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        System.out.println();
        System.out.println("=============================== LISTA DE PRODUCTOS ==============================");
        System.out.printf(
                "%-5s | %-20s | %-10s | %-15s%n",
                "ID", "NOMBRE", "PRECIO", "TIPO"
        );
        System.out.println("-------------------------------------------------------------------------------");
        // Muestra cada producto de la lista.
        for (Products product : products) {
            System.out.printf(
                    "%-5d | %-20s | %-10.2f | %-15s%n",
                    product.getProductId(),
                    product.getProductName(),
                    product.getProductPrice(),
                    product.getProductType()
            );
        }
    }

    public void updateProduct() {
        int id = DataTypeValidator.validateInt("Ingrese el ID del producto a actualizar:");
        Products currentProduct = productServiceInterface.selectById(id).orElse(null);
        // Verifica que exista antes de pedir los nuevos datos.
        if (currentProduct == null) {
            System.out.println("No existe un producto con ese ID.");
            return;
        }
        String name = DataTypeValidator.validateString("Ingrese el nuevo nombre:");
        Float price = DataTypeValidator.validateFloat("Ingrese el nuevo precio:");
        String type = DataTypeValidator.validateString("Ingrese el nuevo tipo:");
        Products updateProducts = new Products(id, name, price, type);
        if (productServiceInterface.updateProduct(updateProducts) == null) {
            System.out.println("Producto actualizado correctamente.");
        }
    }

    public void deleteById() {
        int id = DataTypeValidator.validateInt("Ingrese el ID del producto a eliminar:");
        // Evita anunciar una eliminación cuando el producto no existe.
        if (productServiceInterface.selectById(id) == null) {
            System.out.println("No existe un producto con ese ID.");
            return;
        }
        productServiceInterface.deleteById(id);
        //System.out.println("Producto eliminado correctamente.");
    }
}
