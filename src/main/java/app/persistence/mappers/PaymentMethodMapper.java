package app.persistence.mappers;

import app.domain.PaymentMethod;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMethodMapper implements RowMapper<PaymentMethod> {
    @Override
    public PaymentMethod mapRow(ResultSet rs) throws SQLException {
        return new PaymentMethod(rs.getInt("method_id"), rs.getString("method_name"));
    }
}
