package app.view;

import app.service.PaymentService;

public class PaymentView {
    private PaymentService paymentService;

    public PaymentView(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
