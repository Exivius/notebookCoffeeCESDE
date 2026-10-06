package app.domain.enums;

public enum SelectProductosEnum {
    DESAYUNO("Desayuno"),
    ALMUERZO("Almuerzo"),
    CENA("Cena");

    private final String producto;

    SelectProductosEnum(String producto) {
        this.producto = producto;
    }

    public String getProducto() {
        return this.producto;
    }
}
