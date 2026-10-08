package app.service;

import app.domain.Order;
import app.domain.Place;
import app.domain.User;
import app.domain.Products;
import app.service.inputports.OrderServiceInterface;
import app.service.outputports.OrderRepositoryPort;

import java.util.List;


public class OrderServiceAdapter implements OrderServiceInterface {

    // Referencia al repositorio que almacena las órdenes.
    private final OrderRepositoryPort orderRepositoryPort;

    // Recibe el repositorio por el constructor.
    public OrderServiceAdapter(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public Order createOrder(Integer orderId, Place place, User user, Products products) {
        // Construye una orden con los datos recibidos y solicita guardarla.
        Order order = new Order(orderId, place, user, products);
        return orderRepositoryPort.save(order);
    }

    @Override
    public Order createOrder(Integer orderId, Place place, User user, List<Products> products,
                             List<Integer> quantities) {
        Order order = new Order(orderId, place, user, products, quantities);
        return orderRepositoryPort.save(order);
    }

    @Override
    public Order updateOrder(Integer orderId, Place place, User user, Products products) {
        // Conserva el ID y construye una orden con los nuevos datos.
        Order order = new Order(orderId, place, user, products);
        return orderRepositoryPort.updateOrder(order);
    }

    @Override
    public Order selectByOrderId(Integer orderId) {
        // Solicita buscar la orden; devuelve null si no existe.
        return orderRepositoryPort.selectByOrderId(orderId);
    }

    @Override
    public List<Order> selectAllOrders() {
        // Devuelve la lista de órdenes que entrega el repositorio.
        return orderRepositoryPort.selectAllOrders();
    }

    @Override
    public void deleteById(Integer id) {
        // Solicita eliminar la orden con el ID indicado.
        orderRepositoryPort.deleteById(id);
    }
}
