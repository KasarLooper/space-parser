package org.kasar.parsers.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCConnector {
    private static Connection connection;

    public static Connection getConnection() throws SQLException, IOException {
        if (connection != null)
            return connection;
        DBInfo dbInfo = new DBInfo();
        String url = "jdbc:postgresql://" + dbInfo.getHost() + "/" + dbInfo.getDBName();
        String user = dbInfo.getUser();
        String password = dbInfo.getPassword();
        connection = DriverManager.getConnection(url, user, password);
        return connection;
    }
}
