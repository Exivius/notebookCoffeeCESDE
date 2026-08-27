package App.domain;

public class Products {
    private Integer productId;
    private String productName;
    private Float productPrice;
    private String productType;

    public Products(){}

    public Products(Integer productId, String productName, Float productPrice, String productType) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productType = productType;
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
