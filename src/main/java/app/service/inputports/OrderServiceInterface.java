package app.service.inputports;

import app.domain.Order;
import app.domain.Place;
import app.domain.User;
import app.domain.Products;

import java.util.List;

public interface OrderServiceInterface {

    // Crea una orden con la sede, el usuario y el producto recibidos.
    Order createOrder(Integer orderId, Place place, User user, Products products);
    Order createOrder(Integer orderId, Place place, User user, List<Products> products,
                      List<Integer> quantities);

    // Busca una orden por su ID.
    Order selectByOrderId(Integer orderId);

    // Consulta todas las órdenes guardadas.
    List<Order> selectAllOrders();

    // Actualiza la sede, el usuario y el producto de una orden existente.
    Order updateOrder(Integer orderId, Place place, User user, Products products);

    // Elimina una orden usando su ID.
    void deleteById(Integer id);
}
