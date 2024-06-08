package data;

import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.lang.String;

public class SQLHelper {
    private final PreparedStatement statusPaymentStmt;
    private final PreparedStatement statusCreditStmt;
    private final PreparedStatement cleanOrdersStmt;
    private final PreparedStatement cleanPaymentsStmt;
    private final PreparedStatement cleanCreditsStmt;

    public SQLHelper(String url) throws SQLException {
        Connection conn = DriverManager.getConnection(
                url, "app", "pass"
        );

        val statusPayment = "SELECT status FROM payment_entity JOIN order_entity ON payment_entity.transaction_id = order_entity.payment_id  ORDER BY order_entity.created DESC LIMIT 1";
        statusPaymentStmt = conn.prepareStatement(statusPayment);

        val statusCredit = "SELECT status FROM credit_request_entity JOIN order_entity ON credit_request_entity.bank_id = order_entity.payment_id  ORDER BY order_entity.created DESC LIMIT 1";
        statusCreditStmt = conn.prepareStatement(statusCredit);

        val cleanOrdersSQL = "DELETE FROM order_entity";
        cleanOrdersStmt = conn.prepareStatement(cleanOrdersSQL);

        val cleanPaymentsSQL = "DELETE FROM payment_entity";
        cleanPaymentsStmt = conn.prepareStatement(cleanPaymentsSQL);

        val cleanCreditsSQL = "DELETE FROM credit_request_entity";
        cleanCreditsStmt = conn.prepareStatement(cleanCreditsSQL);
    }

    public String getLastPaymentStatus() {
        try (val rs = statusPaymentStmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLastCreditStatus() {
        try (val rs = statusCreditStmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cleanAll() {
        try {
            cleanOrdersStmt.executeUpdate();
            cleanCreditsStmt.executeUpdate();
            cleanPaymentsStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
