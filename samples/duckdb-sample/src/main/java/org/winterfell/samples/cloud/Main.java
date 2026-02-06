package org.winterfell.samples.cloud;

import java.sql.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/3/31
 */
public class Main {
    public static void main(String[] args) {
        try {
            // 1. 加载 DuckDB JDBC 驱动
            Class.forName("org.duckdb.DuckDBDriver");
            String url = "jdbc:duckdb:~/data/swap/test.db";
            // 2. 建立数据库连接
            Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement;
            // 创建表
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (id INTEGER, name VARCHAR, age INTEGER)";
            preparedStatement = connection.prepareStatement(createTableSQL);
            preparedStatement.execute();
            System.out.println("表创建成功！");

            // 插入数据
            String insertSQL = "INSERT INTO users (id, name, age) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, "Alice");
            preparedStatement.setInt(3, 30);
            preparedStatement.executeUpdate();

            preparedStatement.setInt(1, 2);
            preparedStatement.setString(2, "Bob");
            preparedStatement.setInt(3, 25);
            preparedStatement.executeUpdate();
            System.out.println("数据插入成功！");

            Statement statement = connection.createStatement();
            // 4. 执行 SQL 语句
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            // 5. 处理查询结果
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name")
                        + ", Age: " + resultSet.getInt("age"));
            }
            // 6. 关闭资源
            resultSet.close();
            preparedStatement.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException |
                 SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Hello, World!");
    }
}