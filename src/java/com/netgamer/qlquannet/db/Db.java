package com.netgamer.qlquannet.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;

public final class Db {
    private static volatile boolean driverLoaded = false;

    private Db() {
    }

    public static Connection getConnection(ServletContext ctx) throws SQLException {
        loadDriverOnce();
        String url = DbConfig.getDbUrl(ctx);
        String username = DbConfig.getDbUsername(ctx);
        String password = DbConfig.getDbPassword(ctx);
        return DriverManager.getConnection(url, username, password);
    }

    private static void loadDriverOnce() {
        if (driverLoaded) {
            return;
        }
        synchronized (Db.class) {
            if (driverLoaded) {
                return;
            }
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Missing SQLServer JDBC driver. Put mssql-jdbc jar in WEB-INF/lib.", e);
            }
        }
    }
}

