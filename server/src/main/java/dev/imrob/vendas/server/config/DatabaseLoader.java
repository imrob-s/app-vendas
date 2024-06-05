package dev.imrob.vendas.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
public class DatabaseLoader implements CommandLineRunner {
    private final DataSource dataSource;
    private final JdbcClient jdbcClient;

    public DatabaseLoader(DataSource dataSource, JdbcClient jdbcClient) {
        this.dataSource = dataSource;
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Verificando se o banco de dados está vazio...");
        Long count = jdbcClient
                .sql("SELECT COUNT(*) FROM tb_cliente")
                .query(Long.class)
                .single();
        if (count == 0) {
            log.info("Banco de dados vazio. Populando o banco de dados...");
            executarScriptSql();
            log.info("Banco de dados populado com sucesso.");
        } else {
            log.info("Banco de dados possui registros. Não será necessário popular o banco de dados.");
        }
    }

    private void executarScriptSql() {
        Resource resource = new ClassPathResource("data.sql");
        try {
            EncodedResource encodedResource = new EncodedResource(resource, "UTF-8");
            ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
