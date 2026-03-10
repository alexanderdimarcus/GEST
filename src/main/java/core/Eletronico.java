package core;
import java.util.Date;
import java.util.Calendar;

public class Eletronico extends Produto {
    private Date dataGarantia;

    public Eletronico(int codigo, String descricao, String categoria, int qntdDisp, double valorUnitVenda, double percentualLucro, Fornecedor fornecedor, String fabricante, int mesesGarantia) {
        super(codigo, descricao, categoria, qntdDisp, valorUnitVenda, percentualLucro, fornecedor, fabricante);
        if (mesesGarantia > 0) {
            Calendar cal = Calendar.getInstance(); cal.add(Calendar.MONTH, mesesGarantia);
            this.dataGarantia = cal.getTime();
        } else throw new IllegalArgumentException("Prazo inválido.");
    }

    public Eletronico(int codigo, String descricao, String categoria, int qntdDisp, double valorUnitVenda, double percentualLucro, Fornecedor fornecedor, int mesesGarantia) {
        this(codigo, descricao, categoria, qntdDisp, valorUnitVenda, percentualLucro, fornecedor, "Não informada", mesesGarantia);
    }

    @Override
    public void exibirDetalhes() { System.out.println("Garantia: " + this.dataGarantia); }
    public Date getDataGarantia() { return dataGarantia; }
}