package app.service.helpers;

import app.domain.enums.SelectPaymentMethodEnum;

import app.service.validators.DataTypeValidator;

public class SetPaymentMethod {
    public static String getPaymentMethod(){
        while (true) {
            System.out.println("Seleccione: 1. Efectivo\n2. Tarjeta de credito\n3. Tarjeta de debito");
            int option = DataTypeValidator.validateInt("Método:");
            switch (option){
                case 1: return SelectPaymentMethodEnum.CASH.getPaymentMethod();
                case 2: return SelectPaymentMethodEnum.CREDIT_CARD.getPaymentMethod();
                case 3: return SelectPaymentMethodEnum.DEBIT_CARD.getPaymentMethod();
                default: System.out.println("Opción no valida");
            }
        }
    }
}
