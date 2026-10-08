package app.repository.mappers;

import app.domain.Order;
import app.service.outputports.OrderRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderRepositoryAdapter implements OrderRepositoryPort {
    // Guarda las órdenes en memoria mientras la aplicación está abierta.
    private final List<Order> orders = new ArrayList<>();

    @Override
    public Order save(Order order) {
        order.setOrderId(nextId());
        // Agrega la orden a la lista y devuelve el objeto guardado.
        orders.add(order);
        return order;
    }

    private int nextId() {
        int id = 1;
        while (selectByOrderId(id) != null) {
            id++;
        }
        return id;
    }

    @Override
    public Order selectByOrderId(Integer orderId) {
        // Recorre las órdenes guardadas para buscar el ID solicitado.
        for (Order order : orders) {
            // Objects.equals compara los valores de los IDs.
            if (Objects.equals(order.getOrderId(), orderId)) {
                // Devuelve la orden encontrada y termina la búsqueda.
                return order;
            }
        }
        // null indica que no existe una orden con ese ID.
        return null;
    }

    @Override
    public List<Order> selectAllOrders() {
        // Devuelve una copia de la lista con las órdenes guardadas.
        return new ArrayList<>(orders);
    }

    @Override
    public Order updateOrder(Order order) {
        // i representa la posición; empieza en cero y avanza con i++.
        for (int i = 0; i < orders.size(); i++) {
            // Obtiene la orden guardada en la posición actual.
            Order savedOrder = orders.get(i);
            if (Objects.equals(savedOrder.getOrderId(), order.getOrderId())) {
                // Reemplaza la orden de esa posición por los nuevos datos.
                orders.set(i, order);
                return order;
            }
        }
        // Si el ID no existe, no modifica la lista.
        return null;
    }

    @Override
    public void deleteById(Integer id) {
        // Busca la posición de la orden que se quiere eliminar.
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (Objects.equals(order.getOrderId(), id)) {
                // Elimina la orden y termina el método sin devolver un valor.
                orders.remove(i);
                return;
            }
        }
    }
}
