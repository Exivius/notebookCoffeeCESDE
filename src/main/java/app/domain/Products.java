package app.domain;

public class Products {
    private Integer productId;
    private String productName;
    private Float productPrice;
    private String productType;
    private String description = "";
    private Integer stock;

    public Products(){}

    public Products(Integer productId, String productName, Float productPrice, String productType) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productType = productType;
    }

    public Products(Integer productId, String productName, Float productPrice, String productType,
                    String description, Integer stock) {
        this(productId, productName, productPrice, productType);
        this.description = description == null ? "" : description;
        this.stock = stock;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Float productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description == null ? "" : description; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    //metodos

    public void createProduct(){

    }

    public void selectAllProducts(){

    }

    public void selectProductById(int id){

    }

    public void updateProduct(){

    }

    public void deleteProductById(int id){

    }

}
