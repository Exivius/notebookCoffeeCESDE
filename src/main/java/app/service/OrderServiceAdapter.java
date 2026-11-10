package app.service;

import app.domain.Order;
import app.domain.Item;
import app.domain.Place;
import app.domain.User;
import app.domain.Products;
import app.domain.ShoppingCart;
import app.service.inputports.OrderServiceInterface;
import app.service.inputports.ShoppingCartServiceInterface;
import app.service.outputports.OrderRepositoryPort;

import java.util.List;


public class OrderServiceAdapter implements OrderServiceInterface {

    // Referencia al repositorio que almacena las órdenes.
    private final OrderRepositoryPort orderRepositoryPort;
    private final ShoppingCartServiceInterface shoppingCartService;

    // Recibe el repositorio por el constructor.
    public OrderServiceAdapter(OrderRepositoryPort orderRepositoryPort) {
        this(orderRepositoryPort, null);
    }

    public OrderServiceAdapter(OrderRepositoryPort orderRepositoryPort,
                               ShoppingCartServiceInterface shoppingCartService) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    public Order createOrder(Integer orderId, Place place, User user, Products products) {
        // Construye una orden con los datos recibidos y solicita guardarla.
        Order order = new Order(orderId, place, user, products);
        prepareCart(order);
        return orderRepositoryPort.save(order);
    }

    @Override
    public Order createOrder(Integer orderId, Place place, User user, List<Products> products,
                             List<Integer> quantities) {
        ShoppingCart shoppingCart = new ShoppingCart();
        if (products == null || quantities == null || products.size() != quantities.size()) {
            throw new IllegalArgumentException("Productos y cantidades deben tener el mismo tamaño.");
        }
        for (int i = 0; i < products.size(); i++) {
            shoppingCart.addItem(new Item(products.get(i), quantities.get(i)));
        }
        Order order = new Order(orderId, place, user, shoppingCart);
        prepareCart(order);
        return orderRepositoryPort.save(order);
    }

    @Override
    public Order updateOrder(Integer orderId, Place place, User user, Products products) {
        // Conserva el ID y construye una orden con los nuevos datos.
        Order order = new Order(orderId, place, user, products);
        Order current = orderRepositoryPort.selectByOrderId(orderId);
        if (current != null) {
            order.getShoppingCart().setCartId(current.getShoppingCart().getCartId());
            prepareCart(order);
        }
        return orderRepositoryPort.updateOrder(order);
    }

    @Override
    public Order selectByOrderId(Integer orderId) {
        // Solicita buscar la orden; devuelve null si no existe.
        return hydrate(orderRepositoryPort.selectByOrderId(orderId));
    }

    @Override
    public List<Order> selectAllOrders() {
        // Devuelve la lista de órdenes que entrega el repositorio.
        List<Order> orders = orderRepositoryPort.selectAllOrders();
        for (Order order : orders) {
            hydrate(order);
        }
        return orders;
    }

    @Override
    public void deleteById(Integer id) {
        // Solicita eliminar la orden con el ID indicado.
        Order order = orderRepositoryPort.selectByOrderId(id);
        orderRepositoryPort.deleteById(id);
        if (order != null && order.getShoppingCart().getCartId() != null) {
            shoppingCartService.deleteById(order.getShoppingCart().getCartId());
        }
    }

    private void prepareCart(Order order) {
        if (shoppingCartService == null) {
            throw new IllegalStateException("El servicio de carrito es obligatorio para persistir órdenes.");
        }
        ShoppingCart cart = order.getShoppingCart();
        if (cart.getCartId() == null) {
            List<Item> items = cart.getItems();
            cart = shoppingCartService.createCart();
            order.setShoppingCart(cart);
            for (Item item : items) {
                shoppingCartService.addItem(cart.getCartId(), item);
            }
            order.setShoppingCart(shoppingCartService.selectById(cart.getCartId()));
        } else {
            shoppingCartService.updateCart(cart);
        }
    }

    private Order hydrate(Order order) {
        if (order != null && order.getShoppingCart().getCartId() != null) {
            order.setShoppingCart(shoppingCartService.selectById(
                    order.getShoppingCart().getCartId()));
        }
        return order;
    }
}
