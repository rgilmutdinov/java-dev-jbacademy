package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL_TEMPLATE = "jdbc:h2:file:./src/carsharing/db/%s";

    private final String dbName;

    public Database(String dbName) {
        this.dbName = dbName;
    }

    public PreparedStatement prepareStatement(String sql) {
        try {
            Connection connection = getConnection();
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeUpdate(String sql) {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> getList(String selectSql, SQLCheckedFunction<ResultSet, T> factory) {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                List<T> list = new ArrayList<>();

                ResultSet rs = statement.executeQuery(selectSql);
                while (rs.next()) {
                    list.add(factory.apply(rs));
                }

                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getSingle(String selectSql, SQLCheckedFunction<ResultSet, T> factory) {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery(selectSql);
                if (rs.next()) {
                    return factory.apply(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return getConnection(dbName);
    }

    private static String getDbUrl(String dbName) {
        return String.format(DB_URL_TEMPLATE, dbName);
    }

    private static Connection getConnection(String dbName) throws SQLException {
        Connection connection = DriverManager.getConnection(getDbUrl(dbName));
        connection.setAutoCommit(true);
        return connection;
    }
}
