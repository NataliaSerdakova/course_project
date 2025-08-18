package ru.netology.helper;


import java.sql.*;

import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConn() throws SQLException {
        return DriverManager.getConnection(System.getProperty("db.url"), "user", "qwerty123");
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


    @Value
    public static class PaymentEntity {
        Long id;
        Double amount;
        String created;
        String status;
        Long transaction_id;
    }

    @Value
    public static class OrderEntity {
        Long id;
        String created;
        Long credit_id;
        Long payment_id;
    }

    @Value
    public static class CreditRequestEntity {
        Long id;
        Long bank_id;
        String created;
        String status;
    }
}
