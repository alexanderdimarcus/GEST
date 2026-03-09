package core;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Cosmetico extends Produto {
    private Date dataValidade;

    public Cosmetico(int codigo, String descricao, String categoria, int qntdDisp, double valorUnitVenda, double percentualLucro, Fornecedor fornecedor, Date dataValidade, String fabricante) {
        super(codigo, descricao, categoria, qntdDisp, valorUnitVenda, percentualLucro, fornecedor, fabricante);
        this.dataValidade = dataValidade;
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("Data de Validade: " + new SimpleDateFormat("dd/MM/yyyy").format(this.dataValidade));
    }
    public Date getDataValidade() { return dataValidade; }
}