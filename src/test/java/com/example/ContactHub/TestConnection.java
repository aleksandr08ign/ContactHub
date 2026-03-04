package com.example.ContactHub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/contacthub_db";
        String user = "contact";
        String password = "123654";

        System.out.println("Пытаюсь подключиться к: " + url);
        System.out.println("Пользователь: " + user);

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ ПОДКЛЮЧЕНИЕ УСПЕШНО!");
        } catch (SQLException e) {
            System.out.println("❌ ОШИБКА: " + e.getMessage());
        }
    }
}
