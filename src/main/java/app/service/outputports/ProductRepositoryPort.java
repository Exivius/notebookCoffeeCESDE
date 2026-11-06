package app.service.outputports;

import app.domain.Products;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {

    Products save(Products products);
    Optional<Products> selectById(Integer id);
    List<Products> selectAllProducts();
    Products updateProduct(Products product);
    void deleteById(Integer id);

}


