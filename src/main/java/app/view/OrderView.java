package app.view;

import app.domain.Order;
import app.domain.Place;
import app.domain.User;
import app.domain.Products;
import app.service.inputports.OrderServiceInterface;
import app.service.validators.DataTypeValidator;

import java.util.List;

public class OrderView {

    // La vista usa el servicio para gestionar las órdenes.
    private final OrderServiceInterface orderService;

    // Recibe el servicio por el constructor.
    public OrderView(OrderServiceInterface orderService) {
        this.orderService = orderService;
    }

    public void createOrder(Place place, User user, Products products) {
        // Comprueba que se hayan seleccionado los datos de la orden.
        if (place == null || user == null || products == null) {
            System.out.println("Debe seleccionar una sede, un usuario y un producto.");
            return;
        }

        // Solicita el identificador de la nueva orden.
        int id = DataTypeValidator.validateInt("Ingrese el ID de la orden:");

        // Envía al servicio el ID y los objetos seleccionados.
        orderService.createOrder(id, place, user, products);
        System.out.println("Orden registrada correctamente.");
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