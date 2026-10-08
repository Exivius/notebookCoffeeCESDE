package app.service.outputports;

import app.domain.Products;

import java.util.List;

public interface ProductRepositoryPort {

    Products save(Products products);
    Products selectById(Integer id);
    List<Products> selectAllProducts();
    Products updateProduct(Products product);
    void deleteById(Integer id);

}


