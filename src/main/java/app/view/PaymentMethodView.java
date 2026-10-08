package app.view;

import app.service.PaymentMethodService;

public class PaymentMethodView {
    private PaymentMethodService paymentMethodService;

    public PaymentMethodView(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }
}
