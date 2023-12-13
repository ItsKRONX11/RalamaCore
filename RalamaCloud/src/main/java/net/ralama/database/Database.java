package net.ralama.database;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String HOST = "localhost";
    private static final int PORT = 3306;
    private static final String DATABASE = "ralama";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    @Getter
    private Connection connection;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false", USER, PASSWORD);
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}