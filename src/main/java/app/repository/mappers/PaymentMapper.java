package app.repository.mappers;

import app.domain.Order;
import app.domain.Payment;
import app.domain.PaymentMethod;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMapper implements RowMapper<Payment> {
    @Override
    public Payment mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        Payment payment = new Payment(rs.getInt("payment_id"),
                new PaymentMethod(rs.getInt("method_id"), rs.getString("method_name")), order);
        payment.setPaymentType(rs.getString("payment_type"));
        return payment;
    }
}
