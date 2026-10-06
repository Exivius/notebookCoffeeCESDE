package app.domain.enums;

public enum SelectSedeEnum {
    CENTRO("Centro"),
    BELLO("Bello"),
    RIONEGRO("Rionegro"),
    BOGOTA("Bogotá"),
    ;

    private final String sede;

    SelectSedeEnum(String sede) {
        this.sede = sede;
    }

    public String getSede() {
        return this.sede;
    }
}
