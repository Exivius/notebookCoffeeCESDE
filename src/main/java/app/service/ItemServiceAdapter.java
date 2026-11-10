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
        return null;
    }

    @Override
    public Item selectById(Integer itemId) {
        return null;
    }

    @Override
    public List<Item> selectByCartId(Integer cartId) {
        return List.of();
    }

    @Override
    public Item updateItem(Item item) {
        return null;
    }

    @Override
    public void deleteById(Integer itemId) {

    }
}
