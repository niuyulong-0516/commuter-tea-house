package com.commutertea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommuterTeaApplication {

    public static void main(String[] args) {
        // Render/Supabase 等平台下发 postgres:// 格式的连接串，JDBC 需要 jdbc:postgresql:// 前缀。
        // 转换后写入系统属性 JDBC_DATABASE_URL（application.yml 读取它，环境变量 DEST 不影响）。
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl == null) {
            databaseUrl = System.getProperty("DATABASE_URL");
        }
        if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
            databaseUrl = databaseUrl.replaceFirst("^postgres://", "jdbc:postgresql://");
        }
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            System.setProperty("JDBC_DATABASE_URL", databaseUrl);
            if (databaseUrl.startsWith("jdbc:postgresql:")) {
                System.setProperty("JDBC_DATABASE_DRIVER", "org.postgresql.Driver");
            }
        }
        SpringApplication.run(CommuterTeaApplication.class, args);
    }
}
