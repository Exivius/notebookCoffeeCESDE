package app.service;

import app.domain.Item;
import app.domain.Products;
import app.service.inputports.ItemServiceInterface;
import app.service.outputports.ItemRepositoryPort;

import java.util.List;

public class ItemServiceAdapter implements ItemServiceInterface {

    private final ItemRepositoryPort itemRepositoryPort;

    public ItemServiceAdapter(ItemRepositoryPort itemRepositoryPort) {
        this.itemRepositoryPort = itemRepositoryPort;
    }

    @Override
    public Item createItem(Integer cartId, Products product, Integer quantity) {
        validateId(cartId, "carrito");
        Item item = new Item(product, quantity);
        item.setCartId(cartId);
        return itemRepositoryPort.save(item);
    }

    @Override
    public Item selectById(Integer itemId) {
        validateId(itemId, "item");
        return itemRepositoryPort.selectById(itemId);
    }

    @Override
    public List<Item> selectByCartId(Integer cartId) {
        validateId(cartId, "carrito");
        return itemRepositoryPort.selectByCartId(cartId);
    }

    @Override
    public Item updateItem(Item item) {
        return null;
    }

    @Override
    public void deleteById(Integer itemId) {

    }

    //Metodo auxiliar para validar que el ID sea mayor a cero
    private void validateId(Integer id, String name) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("El ID del " + name + " debe ser mayor que cero.");
        }
    }
}
