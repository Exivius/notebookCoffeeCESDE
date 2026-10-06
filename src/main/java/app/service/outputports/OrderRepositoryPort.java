package app.service.outputports;
import app.domain.Order;
import java.util.List;

public interface OrderRepositoryPort {

    Order save(Order order);

    Order selectByOrderId(Integer orderId);
    List<Order> selectAllOrders();
    Order updateOrder(Order order);
    void deleteById(Integer Id);

}
