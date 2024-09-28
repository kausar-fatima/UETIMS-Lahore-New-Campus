package DatabaseClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String CONNECTION_URL = "jdbc:sqlserver://DESKTOP-8BL3MIG:1433;database=UETIMS;integratedSecurity=true;trustServerCertificate=true;";
    private Connection connection;

    public DatabaseConnector() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            // Ensure that the connection is established before preparing the statement
            connect();
            PreparedStatement statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(CONNECTION_URL);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
