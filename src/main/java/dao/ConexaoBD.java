package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    private static final String url = "jdbc:postgresql://ep-mute-union-acero5qf-pooler.sa-east-1.aws.neon.tech/gest-db?user=admin-gest&password=npg_r5OXJiCLI2jp&sslmode=require&channelBinding=require";

    public static Connection conectar() throws SQLException {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Erro Crítico: Não foi possível conectar ao Banco de dados!");
            System.out.println("Mensagem: " + e.getMessage());
            throw new RuntimeException("Erro de conexão com o banco de dados na nuvem", e);
        }
    }
}