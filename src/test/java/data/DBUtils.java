package data;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBUtils {
    private static String url = System.getProperty("db.url");
    private static String userDB = System.getProperty("app.userDB");
    private static String password = System.getProperty("app.password");

    @SneakyThrows
    public static void clearAllData() {
        var runner = new QueryRunner();
        var conn = DriverManager.getConnection(url, userDB, password);
        runner.update(conn, "DELETE FROM credit_request_entity;");
        runner.update(conn, "DELETE FROM payment_entity;");
        runner.update(conn, "DELETE FROM order_entity;");
    }

    public static void checkPaymentStatus(Status status) throws SQLException {
        val runner = new QueryRunner();
        val conn = DriverManager.getConnection(url, userDB, password);
        val paymentDataSQL = "SELECT status FROM payment_entity;";
        val payment = runner.query(conn, paymentDataSQL, new BeanHandler<>(Payment.class));
        assertEquals(status, payment.status);
    }

    public static void checkCreditStatus(Status status) throws SQLException {
        val runner = new QueryRunner();
        val conn = DriverManager.getConnection(url, userDB, password);
        val creditDataSQL = "SELECT status FROM credit_request_entity;";
        val credit = runner.query(conn, creditDataSQL, new BeanHandler<>(Credit.class));
        assertEquals(status, credit.status);
    }
}
