package dao;

import core.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, core.Seguranca.hashearSenha(senha));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setLogin(rs.getString("login"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setNomeCompleto(rs.getString("nome_completo"));
                usuario.setCargo(rs.getString("cargo"));

                return usuario;
            }

        } catch (SQLException e) {
            System.err.println("Erro na autenticação: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // ==========================================================
    // MÉTODOS DE GESTÃO DE EQUIPE
    // ==========================================================

    public java.util.List<Usuario> listarTodos() {
        java.util.List<Usuario> lista = new java.util.ArrayList<>();
        String sql = "SELECT id, login, nome_completo, cargo FROM usuarios ORDER BY id";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setLogin(rs.getString("login"));
                u.setNomeCompleto(rs.getString("nome_completo"));
                u.setCargo(rs.getString("cargo"));
                lista.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public void salvar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (login, senha, nome_completo, cargo) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getLogin());
            stmt.setString(2, core.Seguranca.hashearSenha(u.getSenha()));
            stmt.setString(3, u.getNomeCompleto());
            stmt.setString(4, u.getCargo());
            stmt.executeUpdate();
        }
    }

    public void atualizarSenha(int id, String novaSenhaLimpa) throws SQLException {
        String sql = "UPDATE usuarios SET senha = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, core.Seguranca.hashearSenha(novaSenhaLimpa));
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET login = ?, nome_completo = ?, cargo = ? WHERE id = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getLogin());
            stmt.setString(2, u.getNomeCompleto());
            stmt.setString(3, u.getCargo());
            stmt.setInt(4, u.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

}