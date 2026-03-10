package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MovimentacaoDAO {

    public void registrarBaixa(int codigoProduto, int quantidadeRemover, String motivo, String observacao, String nomeUsuario) throws SQLException {

        String sqlAtualizarEstoque = "UPDATE produtos SET quantidade = quantidade - ? WHERE codigo = ? AND quantidade >= ?";

        String sqlInserirHistorico = "INSERT INTO historico_movimentacoes (produto_codigo, tipo_movimentacao, quantidade, motivo, observacao, usuario, data_hora) VALUES (?, 'BAIXA', ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEstoque = conn.prepareStatement(sqlAtualizarEstoque);
                 PreparedStatement stmtHistorico = conn.prepareStatement(sqlInserirHistorico)) {

                stmtEstoque.setInt(1, quantidadeRemover);
                stmtEstoque.setInt(2, codigoProduto);
                stmtEstoque.setInt(3, quantidadeRemover);

                int linhasAfetadas = stmtEstoque.executeUpdate();

                if (linhasAfetadas == 0) {
                    conn.rollback();
                    throw new SQLException("Estoque insuficiente para realizar esta baixa ou produto não encontrado.");
                }

                stmtHistorico.setInt(1, codigoProduto);
                stmtHistorico.setInt(2, quantidadeRemover);
                stmtHistorico.setString(3, motivo);
                stmtHistorico.setString(4, observacao);
                stmtHistorico.setString(5, nomeUsuario);
                stmtHistorico.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                stmtHistorico.executeUpdate();

                conn.commit();
                System.out.println("Baixa e Histórico registrados com sucesso!");

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void registrarVenda(java.util.List<core.ItemCarrinho> itens, String nomeUsuario) throws SQLException {

        String sqlAtualizarEstoque = "UPDATE produtos SET quantidade = quantidade - ? WHERE codigo = ? AND quantidade >= ?";

        String sqlInserirHistorico = "INSERT INTO historico_movimentacoes (produto_codigo, tipo_movimentacao, quantidade, motivo, observacao, usuario, data_hora) VALUES (?, 'VENDA', ?, 'Venda PDV', 'Venda finalizada no caixa', ?, ?)";

        try (Connection conn = ConexaoBD.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEstoque = conn.prepareStatement(sqlAtualizarEstoque);
                 PreparedStatement stmtHistorico = conn.prepareStatement(sqlInserirHistorico)) {

                for (core.ItemCarrinho item : itens) {

                    stmtEstoque.setInt(1, item.getQuantidade());
                    stmtEstoque.setInt(2, item.getCodigo());
                    stmtEstoque.setInt(3, item.getQuantidade());

                    int linhasAfetadas = stmtEstoque.executeUpdate();

                    if (linhasAfetadas == 0) {
                        conn.rollback();
                        throw new SQLException("Estoque insuficiente para o produto: " + item.getDescricao() + " (Cód: " + item.getCodigo() + ")");
                    }

                    stmtHistorico.setInt(1, item.getCodigo());
                    stmtHistorico.setInt(2, item.getQuantidade());
                    stmtHistorico.setString(3, nomeUsuario);
                    stmtHistorico.setTimestamp(4, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                    stmtHistorico.executeUpdate();
                }

                conn.commit();
                System.out.println("Venda finalizada! " + itens.size() + " itens processados com sucesso.");

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void registrarEntradaLote(java.util.List<core.ItemCarrinho> itens, String nomeUsuario) throws SQLException {

        String sqlAtualizarEstoque = "UPDATE produtos SET quantidade = quantidade + ? WHERE codigo = ?";

        String sqlInserirHistorico = "INSERT INTO historico_movimentacoes (produto_codigo, tipo_movimentacao, quantidade, motivo, observacao, usuario, data_hora) VALUES (?, 'ENTRADA', ?, 'Entrada em Lote', 'Reposição de estoque / Recebimento', ?, ?)";

        try (Connection conn = ConexaoBD.conectar()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmtEstoque = conn.prepareStatement(sqlAtualizarEstoque);
                 PreparedStatement stmtHistorico = conn.prepareStatement(sqlInserirHistorico)) {

                for (core.ItemCarrinho item : itens) {

                    stmtEstoque.setInt(1, item.getQuantidade());
                    stmtEstoque.setInt(2, item.getCodigo());
                    stmtEstoque.executeUpdate();

                    stmtHistorico.setInt(1, item.getCodigo());
                    stmtHistorico.setInt(2, item.getQuantidade());
                    stmtHistorico.setString(3, nomeUsuario);
                    stmtHistorico.setTimestamp(4, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
                    stmtHistorico.executeUpdate();
                }

                conn.commit();
                System.out.println("Entrada finalizada! " + itens.size() + " itens adicionados.");

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public java.util.List<core.LogMovimentacao> listarHistorico() {
        java.util.List<core.LogMovimentacao> lista = new java.util.ArrayList<>();

        String sql = "SELECT * FROM historico_movimentacoes ORDER BY id DESC";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                core.LogMovimentacao log = new core.LogMovimentacao(
                        rs.getInt("id"),
                        rs.getInt("produto_codigo"),
                        rs.getString("tipo_movimentacao"),
                        rs.getInt("quantidade"),
                        rs.getString("motivo"),
                        rs.getString("observacao"),
                        rs.getString("usuario"),
                        rs.getTimestamp("data_hora")
                );
                lista.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar histórico: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}