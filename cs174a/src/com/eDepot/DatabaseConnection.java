package com.eDepot;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:oracle:thin:@cs174a_low?TNS_ADMIN=C:/Users/keigo/Downloads/Final_CS174A_Wallet";
    private static final String DB_USER = "ADMIN";
    private static final String DB_PASSWORD = "CS174Adatabase";

    public static Connection getConnection() throws SQLException {
        Properties info = new Properties();
        info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER);
        info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD);
        info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");

        OracleDataSource ods = new OracleDataSource();
        ods.setURL(DB_URL);
        ods.setConnectionProperties(info);

        return ods.getConnection();
    }
}
