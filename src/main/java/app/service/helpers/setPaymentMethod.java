package app.service.helpers;

import app.domain.enums.SelectPaymentMethodEnum;

import java.util.Scanner;

public class SetPaymentMethod {
    static Scanner sc = new Scanner(System.in);
    public static String getPaymentMethod(){
        System.out.println("Seleccione: 1. Efectivo\n2. Tarjeta de credito\n3. Tarjeta de debito");
        int option = sc.nextInt();
        String paymentMethod = null;
        switch (option){
            case 1:
                paymentMethod = SelectPaymentMethodEnum.CASH.getPaymentMethod();
                break;
            case 2:
                paymentMethod = SelectPaymentMethodEnum.CREDIT_CARD.getPaymentMethod();
                break;
            case 3:
                paymentMethod = SelectPaymentMethodEnum.DEBIT_CARD.getPaymentMethod();
                break;
            default:
                System.out.println("Opción no valida");
        }
        return paymentMethod;
    }
}
