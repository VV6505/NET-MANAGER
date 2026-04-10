package com.netgamer.qlquannet.db;

import javax.servlet.ServletContext;

public final class DbConfig {
    private DbConfig() {
    }

    public static String getDbUrl(ServletContext ctx) {
        return get("DB_URL", ctx, "jdbc:sqlserver://localhost:1433;databaseName=dbNET;encrypt=true;trustServerCertificate=true;integratedSecurity=false");
    }

    public static String getDbUsername(ServletContext ctx) {
        return get("DB_USERNAME", ctx, "sa");
    }

    public static String getDbPassword(ServletContext ctx) {
        return get("DB_PASSWORD", ctx, "12345");
    }

    private static String get(String key, ServletContext ctx, String fallback) {
        String env = System.getenv(key);
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }
        if (ctx != null) {
            String init = ctx.getInitParameter(key);
            if (init != null && !init.trim().isEmpty()) {
                return init.trim();
            }
        }
        return fallback;
    }
}

