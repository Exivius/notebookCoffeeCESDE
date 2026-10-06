package app.domain.enums;

public enum SelectPaymentMethodEnum {
    CREDIT_CARD("Tarjeta de crédito"),
    DEBIT_CARD("Tarjeta de débito"),
    BANK_TRANSFER("Transferencia bancaria"),
    CASH("Efectivo");

    private final String paymentMethod;

    SelectPaymentMethodEnum(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }
}
