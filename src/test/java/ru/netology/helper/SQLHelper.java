package ru.netology.helper;


import java.sql.*;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConn() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/db", "user", "qwerty123");
    }

    @SneakyThrows
    public static List<PaymentEntity> getPayments() {
        try (var conn = getConn()) {
            return QUERY_RUNNER.query(conn, "SELECT * FROM payment_entity", new BeanListHandler<>(PaymentEntity.class));
        }
    }

    @SneakyThrows
    public static List<OrderEntity> getOrders() {
        try (var conn = getConn()) {
            return QUERY_RUNNER.query(conn, "SELECT * FROM order_entity", new BeanListHandler<>(OrderEntity.class));
        }
    }

    @SneakyThrows
    public static List<CreditRequestEntity> getCredit() {
        try (var conn = getConn()) {
            return QUERY_RUNNER.query(conn, "SELECT * FROM credit_request_entity", new BeanListHandler<>(CreditRequestEntity.class));
        }
    }

    @SneakyThrows
    public static void cleanDatabase() {
        try (var conn = getConn()) {
            QUERY_RUNNER.execute(conn, "DELETE FROM payment_entity");
            QUERY_RUNNER.execute(conn, "DELETE FROM order_entity");
            QUERY_RUNNER.execute(conn, "DELETE FROM credit_request_entity");

        }
    }

    @SneakyThrows
    public static long countPayments() {
        try (var conn = getConn()) {
            return QUERY_RUNNER.query(conn, "SELECT COUNT(*) FROM payment_entity", new ScalarHandler<Long>());
        }
    }

    @SneakyThrows
    public static long countOrders() {
        try (var conn = getConn()) {
            return QUERY_RUNNER.query(conn, "SELECT COUNT(*) FROM order_entity", new ScalarHandler<Long>());
        }
    }

    @SneakyThrows
    public static long countCredit() {
        try (var conn = getConn()) {
            return QUERY_RUNNER.query(conn, "SELECT COUNT(*) FROM credit_request_entity", new ScalarHandler<Long>());
        }
    }

    @SneakyThrows
    public static Optional<CreditRequestEntity> findPaymentByStatus(String status) {
        String query = "SELECT * FROM credit_request_entity WHERE status = ?";
        try (var conn = getConn()) {
            CreditRequestEntity credit = QUERY_RUNNER.query(conn, query, new BeanHandler<>(CreditRequestEntity.class), status);
            return Optional.ofNullable(credit);
        }
    }

    public static boolean isCreditRecorded(String expectedStatus) {
        return findPaymentByStatus(expectedStatus).isPresent();
    }

    @SneakyThrows
    public static Optional<PaymentEntity> findPaymentByAmountAndStatus(double amount, String status) {
        String query = "SELECT * FROM payment_entity WHERE amount = ? AND status = ?";
        try (var conn = getConn()) {
            PaymentEntity payment = QUERY_RUNNER.query(conn, query, new BeanHandler<>(PaymentEntity.class), amount, status);
            return Optional.ofNullable(payment);
        }
    }

    public static boolean isPaymentRecorded(double expectedAmount, String expectedStatus) {
        return findPaymentByAmountAndStatus(expectedAmount, expectedStatus).isPresent();
    }


    @Data
    public static class PaymentEntity {
        String id;
        Double amount;
        String created;
        String status;
        String transaction_id;
    }

    @Data
    public static class OrderEntity {
        String id;
        String created;
        String credit_id;
        String payment_id;
    }

    @Data
    public static class CreditRequestEntity {
        String id;
        String bank_id;
        String created;
        String status;
    }
}

