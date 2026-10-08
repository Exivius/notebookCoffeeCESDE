package app.view;

import app.domain.Order;
import app.domain.Place;
import app.domain.User;
import app.domain.Products;
import app.service.inputports.OrderServiceInterface;
import app.service.inputports.PlaceServiceInterface;
import app.service.inputports.UserServiceInterface;
import app.service.inputports.ProductServiceInterface;
import app.service.validators.DataTypeValidator;
import app.service.helpers.SetSede;

import java.util.ArrayList;
import java.util.List;

public class OrderView {

    // La vista usa el servicio para gestionar las órdenes.
    private final OrderServiceInterface orderService;
    private final PlaceServiceInterface placeService;
    private final UserServiceInterface userService;
    private final ProductServiceInterface productService;

    // Recibe el servicio por el constructor.
    public OrderView(OrderServiceInterface orderService, PlaceServiceInterface placeService,
                     UserServiceInterface userService, ProductServiceInterface productService) {
        this.orderService = orderService;
        this.placeService = placeService;
        this.userService = userService;
        this.productService = productService;
    }

    public void createOrder() {
        // Busca objetos registrados usando los servicios compartidos.
        Place place = SetSede.getPlace(placeService);
        int userId = DataTypeValidator.validateInt("Ingrese el ID del usuario:");
        User user = userService.selectUserById(userId);
        createOrderWithProducts(place, user);
    }

    public void updateOrder() {
        // Busca las nuevas relaciones entre objetos registrados.
        int placeId = DataTypeValidator.validateInt("Ingrese el nuevo ID de sede:");
        int userId = DataTypeValidator.validateInt("Ingrese el nuevo ID de usuario:");
        int productId = DataTypeValidator.validateInt("Ingrese el nuevo ID de producto:");
        Place place = placeService.selectPlaceById(placeId);
        User user = userService.selectUserById(userId);
        Products product = productService.selectById(productId);
        updateOrder(place, user, product);
    }

    public void createOrder(Place place, User user, Products products) {
        // Comprueba que se hayan seleccionado los datos de la orden.
        if (place == null || user == null || products == null) {
            System.out.println("Debe seleccionar una sede, un usuario y un producto.");
            return;
        }

        // Solicita el identificador de la nueva orden.
        Order order = orderService.createOrder(null, place, user, products);
        System.out.println("Orden registrada correctamente. ID: " + order.getOrderId());
    }

    public void createOrderForUser(User user) {
        createOrderWithProducts(SetSede.getPlace(placeService), user);
    }

    private void createOrderWithProducts(Place place, User user) {
        if (place == null || user == null) {
            System.out.println("Debe seleccionar una sede y un usuario válido.");
            return;
        }
        List<Products> products = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        int addMore;
        do {
            System.out.println("Productos disponibles:");
            productService.selectAllProducts();
            int productId = DataTypeValidator.validateInt("Ingrese el ID del producto:");
            Products product = productService.selectById(productId);
            if (product == null) {
                System.out.println("No existe ese producto.");
                addMore = 1;
                continue;
            }
            int quantity = DataTypeValidator.validateInt("Ingrese la cantidad:");
            if (quantity < 1) {
                System.out.println("La cantidad debe ser mayor que cero.");
                addMore = 1;
                continue;
            }
            products.add(product);
            quantities.add(quantity);
            addMore = DataTypeValidator.validateInt("¿Desea agregar otro producto? 1. Sí 0. No");
        } while (addMore == 1);
        if (products.isEmpty()) {
            System.out.println("La orden debe tener al menos un producto.");
            return;
        }
        Order order = orderService.createOrder(null, place, user, products, quantities);
        System.out.println("Orden registrada correctamente. ID: " + order.getOrderId());
    }

    public void selectOrdersByUser(User user) {
        boolean found = false;
        for (Order order : orderService.selectAllOrders()) {
            if (order.getUser() != null && order.getUser().getId().equals(user.getId())) {
                System.out.println("Orden: " + order.getOrderId()
                        + " | Sede: " + order.getPlace().getPlaceName()
                        + " | Producto: " + order.getProducts().getProductName()
                        + " | Precio: " + order.getProducts().getProductPrice());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No hay órdenes registradas para este usuario.");
        }
    }

    public void selectByOrderId() {
        // Solicita el ID y busca la orden mediante el servicio.
        int id = DataTypeValidator.validateInt("Ingrese el ID de la orden:");
        Order order = orderService.selectByOrderId(id);

        // Evita acceder a los datos si la orden no existe.
        if (order == null) {
            System.out.println("No existe una orden con ese ID.");
            return;
        }

        // Muestra los datos de la orden encontrada.
        System.out.println(
                "Orden: " + order.getOrderId()
                        + " | Sede: " + order.getPlace().getPlaceName()
                        + " | Usuario: " + order.getUser().getName()
                        + " | Producto: " + order.getProducts().getProductName()
                        + " | Precio: " + order.getProducts().getProductPrice()
        );
    }

    public void selectAllOrders() {
        // Obtiene las órdenes guardadas.
        List<Order> orders = orderService.selectAllOrders();

        if (orders.isEmpty()) {
            System.out.println("No hay órdenes registradas.");
            return;
        }

        // Recorre la lista y muestra cada orden.
        for (Order order : orders) {
            System.out.println(
                    "Orden: " + order.getOrderId()
                            + " | Sede: " + order.getPlace().getPlaceName()
                            + " | Usuario: " + order.getUser().getName()
                            + " | Producto: " + order.getProducts().getProductName()
                            + " | Precio: " + order.getProducts().getProductPrice()
            );
        }
    }

    public void updateOrder(Place place, User user, Products products) {
        // Comprueba que los nuevos datos estén seleccionados.
        if (place == null || user == null || products == null) {
            System.out.println("Debe seleccionar una sede, un usuario y un producto.");
            return;
        }

        int id = DataTypeValidator.validateInt(
                "Ingrese el ID de la orden a actualizar:"
        );

        // Envía el ID y las nuevas relaciones al servicio.
        Order order = orderService.updateOrder(id, place, user, products);

        // El repositorio devuelve null si no encontró la orden.
        if (order == null) {
            System.out.println("No existe una orden con ese ID.");
        } else {
            System.out.println("Orden actualizada correctamente.");
        }
    }

    public void deleteById() {
        int id = DataTypeValidator.validateInt(
                "Ingrese el ID de la orden a eliminar:"
        );

        // Comprueba que exista antes de solicitar la eliminación.
        if (orderService.selectByOrderId(id) == null) {
            System.out.println("No existe una orden con ese ID.");
            return;
        }

        orderService.deleteById(id);
        System.out.println("Orden eliminada correctamente.");
    }
}
