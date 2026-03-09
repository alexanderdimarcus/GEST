package dao;

import core.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();

        String sql = "SELECT p.*, f.nome as f_nome, f.cnpj_cpf as f_cnpj, f.contato as f_contato " +
                "FROM produtos p " +
                "INNER JOIN fornecedores f ON p.fornecedor_id = f.id " +
                "ORDER BY p.codigo";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // 1. Recuperamos o fornecedor
                Fornecedor f = new Fornecedor(
                        rs.getInt("fornecedor_id"),
                        rs.getString("f_nome"),
                        rs.getString("f_cnpj"),
                        rs.getString("f_contato")
                );

                // 2. Recuperamos os dados comuns de Produto
                int codigo = rs.getInt("codigo");
                String descricao = rs.getString("descricao");
                String categoria = rs.getString("categoria");
                int qtd = rs.getInt("quantidade");
                double valor = rs.getDouble("valor_venda");
                double lucro = rs.getDouble("percentual_lucro");
                String tipo = rs.getString("tipo_produto");

                // 3. Decidimos qual filho instanciar baseado no TIPO
                Produto p = null;

                // A marca agora serve para todos!
                String fabricante = rs.getString("fabricante");

                p = switch (tipo) {
                    case "COSMETICO" -> {
                        Date validadeCos = rs.getDate("data_validade");
                        yield new Cosmetico(codigo, descricao, categoria, qtd, valor, lucro, f, validadeCos, fabricante);
                    }
                    case "ELETRONICO" -> {
                        int garantia = rs.getInt("meses_garantia");
                        yield new Eletronico(codigo, descricao, categoria, qtd, valor, lucro, f, fabricante, garantia);
                    }
                    case "PERECIVEL" -> {
                        Date validadePer = rs.getDate("data_validade");
                        yield new ProdutoPerecivel(codigo, descricao, categoria, qtd, valor, lucro, f, fabricante, validadePer);
                    }
                    default -> p;
                };

                if (p != null) {
                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public Produto buscarPorCodigo(int cod) {
        return listarTodos().stream()
                .filter(p -> p.getCodigo() == cod)
                .findFirst()
                .orElse(null);
    }

    // Metodo para SALVAR um novo produto (Insert)
    public void salvar(Produto p) throws SQLException {
        String sql = "INSERT INTO produtos (codigo, descricao, categoria, quantidade, valor_venda, percentual_lucro, fornecedor_id, tipo_produto, data_validade, fabricante, meses_garantia) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. Dados Comuns
            stmt.setInt(1, p.getCodigo());
            stmt.setString(2, p.getDescricao());
            stmt.setString(3, p.getCategoria());
            stmt.setInt(4, p.getQntdDisp());
            stmt.setDouble(5, p.getValorUnitVenda());
            stmt.setDouble(6, p.getPercentualLucro());
            stmt.setInt(7, p.getFornecedor().getId());
            stmt.setString(10, p.getFabricante());

            // 2. Dados Específicos
            switch (p) {
                case Cosmetico c -> {
                    stmt.setString(8, "COSMETICO");
                    stmt.setDate(9, new java.sql.Date(c.getDataValidade().getTime()));
                    stmt.setNull(11, java.sql.Types.INTEGER);
                }
                case Eletronico e -> {
                    stmt.setString(8, "ELETRONICO");
                    stmt.setNull(9, java.sql.Types.DATE);
                    stmt.setInt(11, e.getDataGarantia() != null ? 12 : 0);
                }
                case ProdutoPerecivel pp -> {
                    stmt.setString(8, "PERECIVEL");
                    stmt.setDate(9, new java.sql.Date(pp.getDataValidade().getTime()));
                    stmt.setNull(11, java.sql.Types.INTEGER);
                }
                default -> {}
            }

            stmt.executeUpdate();
            System.out.println("Produto salvo com sucesso!");
        }
    }

    // Metodo para ATUALIZAR um produto existente (Update)
    public void atualizar(Produto p) throws SQLException {
        String sql = "UPDATE produtos SET descricao = ?, categoria = ?, quantidade = ?, valor_venda = ?, percentual_lucro = ?, fornecedor_id = ?, data_validade = ?, fabricante = ?, meses_garantia = ? WHERE codigo = ?";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getDescricao());
            stmt.setString(2, p.getCategoria());
            stmt.setInt(3, p.getQntdDisp());
            stmt.setDouble(4, p.getValorUnitVenda());
            stmt.setDouble(5, p.getPercentualLucro());
            stmt.setInt(6, p.getFornecedor().getId());
            stmt.setString(8, p.getFabricante());
            stmt.setInt(10, p.getCodigo());

            switch (p) {
                case Cosmetico c -> {
                    stmt.setDate(7, new java.sql.Date(c.getDataValidade().getTime()));
                    stmt.setNull(9, java.sql.Types.INTEGER);
                }
                case Eletronico e -> {
                    stmt.setNull(7, java.sql.Types.DATE);
                    stmt.setInt(9, e.getDataGarantia() != null ? 12 : 0);
                }
                case ProdutoPerecivel pp -> {
                    stmt.setDate(7, new java.sql.Date(pp.getDataValidade().getTime()));
                    stmt.setNull(9, java.sql.Types.INTEGER);
                }
                default -> {
                    stmt.setNull(7, java.sql.Types.DATE);
                    stmt.setNull(9, java.sql.Types.INTEGER);
                }
            }

            // O Código é a nossa chave de busca!
            stmt.setInt(10, p.getCodigo());

            stmt.executeUpdate();
            System.out.println("Produto atualizado com sucesso!");
        }
    }

    // Metodo para EXCLUIR um produto do banco
    public void excluir(int codigo) throws SQLException {
        String sql = "DELETE FROM produtos WHERE codigo = ?";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            stmt.executeUpdate();
            System.out.println("Produto excluído com sucesso!");
        }
    }
}