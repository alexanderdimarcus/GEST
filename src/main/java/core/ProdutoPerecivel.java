package core;
import java.util.Date;

public class ProdutoPerecivel extends Produto {
    private Date dataValidade;

    public ProdutoPerecivel(int codigo, String descricao, String categoria, int qntdDisp, double valorUnitVenda, double percentualLucro, Fornecedor fornecedor, String fabricante, Date dataValidade) {
        super(codigo, descricao, categoria, qntdDisp, valorUnitVenda, percentualLucro, fornecedor, fabricante);
        this.dataValidade = dataValidade;
    }

    public ProdutoPerecivel(int codigo, String descricao, String categoria, int qntdDisp, double valorUnitVenda, double percentualLucro, Fornecedor fornecedor, Date dataValidade) {
        this(codigo, descricao, categoria, qntdDisp, valorUnitVenda, percentualLucro, fornecedor, "Não informada", dataValidade);
    }

    @Override
    public void exibirDetalhes() { System.out.println("Validade: " + this.dataValidade); }
    public Date getDataValidade() { return dataValidade; }
}