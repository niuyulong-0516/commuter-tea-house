package com.commutertea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;

@SpringBootApplication
public class CommuterTeaApplication {

    public static void main(String[] args) {
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = System.getProperty("DATABASE_URL");
        }
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            // Render 下发的连接串形如 postgres://user:pass@host:port/db。
            // JDBC 不支持 URL 中带 user:pass@，需要拆出 host/port/db 拼成干净的 jdbc:postgresql://host:port/db。
            // 用户名密码由 DATABASE_USER / DATABASE_PASSWORD 单独提供（render.yaml 已从数据库注入）。
            String jdbcUrl = toJdbcUrl(databaseUrl);
            System.setProperty("JDBC_DATABASE_URL", jdbcUrl);
            System.setProperty("JDBC_DATABASE_DRIVER", "org.postgresql.Driver");
        }
        SpringApplication.run(CommuterTeaApplication.class, args);
    }

    private static String toJdbcUrl(String url) {
        if (url.startsWith("jdbc:")) {
            return url;
        }
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            int port = uri.getPort();
            String path = uri.getPath();
            if (host != null && path != null && !path.isBlank()) {
                // Render 的 PostgreSQL 要求 SSL
                return "jdbc:postgresql://" + host + (port > 0 ? ":" + port : "") + path + "?sslmode=require";
            }
        } catch (Exception ignored) {
        }
        return url.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
    }
}
