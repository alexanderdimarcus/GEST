package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MovimentacaoDAO {

    // Metodo para registrar uma baixa/perda de estoque com segurança
    public void registrarBaixa(int codigoProduto, int quantidadeRemover, String motivo, String observacao, String nomeUsuario) throws SQLException {

        // Query 1: Tira do estoque
        String sqlAtualizarEstoque = "UPDATE produtos SET quantidade = quantidade - ? WHERE codigo = ? AND quantidade >= ?";

        // Query 2: Salva o log (AGORA ENVIANDO A DATA_HORA LOCAL DO SISTEMA)
        String sqlInserirHistorico = "INSERT INTO historico_movimentacoes (produto_codigo, tipo_movimentacao, quantidade, motivo, observacao, usuario, data_hora) VALUES (?, 'BAIXA', ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEstoque = conn.prepareStatement(sqlAtualizarEstoque);
                 PreparedStatement stmtHistorico = conn.prepareStatement(sqlInserirHistorico)) {

                // 1. Tenta atualizar o estoque
                stmtEstoque.setInt(1, quantidadeRemover);
                stmtEstoque.setInt(2, codigoProduto);
                stmtEstoque.setInt(3, quantidadeRemover);

                int linhasAfetadas = stmtEstoque.executeUpdate();

                if (linhasAfetadas == 0) {
                    conn.rollback();
                    throw new SQLException("Estoque insuficiente para realizar esta baixa ou produto não encontrado.");
                }

                // 2. Se deu certo, salva no histórico com o horário correto
                stmtHistorico.setInt(1, codigoProduto);
                stmtHistorico.setInt(2, quantidadeRemover);
                stmtHistorico.setString(3, motivo);
                stmtHistorico.setString(4, observacao);
                stmtHistorico.setString(5, nomeUsuario);

                // Pega a hora exata da sua máquina e converte para o formato do banco
                stmtHistorico.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

                stmtHistorico.executeUpdate();

                // 3. EFETIVA A TRANSAÇÃO
                conn.commit();
                System.out.println("Baixa e Histórico registrados com sucesso com horário local!");

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    // Metodo para registrar uma VENDA (Múltiplos itens de uma vez com segurança transacional)
    public void registrarVenda(java.util.List<core.ItemCarrinho> itens, String nomeUsuario) throws SQLException {

        String sqlAtualizarEstoque = "UPDATE produtos SET quantidade = quantidade - ? WHERE codigo = ? AND quantidade >= ?";

        String sqlInserirHistorico = "INSERT INTO historico_movimentacoes (produto_codigo, tipo_movimentacao, quantidade, motivo, observacao, usuario, data_hora) VALUES (?, 'VENDA', ?, 'Venda PDV', 'Venda finalizada no caixa', ?, ?)";

        try (Connection conn = ConexaoBD.conectar()) {
            // TRAVA DE SEGURANÇA: Inicia a transação. Ou salva tudo, ou não salva nada.
            conn.setAutoCommit(false);

            try (PreparedStatement stmtEstoque = conn.prepareStatement(sqlAtualizarEstoque);
                 PreparedStatement stmtHistorico = conn.prepareStatement(sqlInserirHistorico)) {

                // Percorre cada item do carrinho de compras
                for (core.ItemCarrinho item : itens) {

                    // 1. Tenta tirar do estoque
                    stmtEstoque.setInt(1, item.getQuantidade());
                    stmtEstoque.setInt(2, item.getCodigo());
                    stmtEstoque.setInt(3, item.getQuantidade());

                    int linhasAfetadas = stmtEstoque.executeUpdate();

                    if (linhasAfetadas == 0) {
                        // Se falhar em UM item (estoque acabou no meio da compra), cancela TUDO!
                        conn.rollback();
                        throw new SQLException("Estoque insuficiente para o produto: " + item.getDescricao() + " (Cód: " + item.getCodigo() + ")");
                    }

                    // 2. Registra no histórico de movimentações como VENDA
                    stmtHistorico.setInt(1, item.getCodigo());
                    stmtHistorico.setInt(2, item.getQuantidade());
                    stmtHistorico.setString(3, nomeUsuario);
                    stmtHistorico.setTimestamp(4, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

                    stmtHistorico.executeUpdate();
                }

                // Se o laço (for) terminar sem erros, CONFIRMA A VENDA DE TODOS OS ITENS!
                conn.commit();
                System.out.println("Venda finalizada! " + itens.size() + " itens processados com sucesso.");

            } catch (SQLException e) {
                conn.rollback(); // Desfaz tudo em caso de erro
                throw e;
            } finally {
                conn.setAutoCommit(true); // Devolve o banco ao estado normal
            }
        }
    }
}