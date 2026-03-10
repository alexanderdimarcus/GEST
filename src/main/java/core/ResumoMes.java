package core;

import app.MainFX;

public class ResumoMes {
    private final String mesAno;
    private int qtdVendas = 0;
    private double totalVendido = 0.0;
    private double lucroTotal = 0.0;
    private double prejuizoBaixas = 0.0;

    public ResumoMes(String mesAno) {
        this.mesAno = mesAno;
    }

    public void addVenda(int qtd, double valor, double lucro) {
        this.qtdVendas += qtd;
        this.totalVendido += valor;
        this.lucroTotal += lucro;
    }

    public void addBaixa(double prejuizo) {
        this.prejuizoBaixas += prejuizo;
    }

    public String getMesAno() { return mesAno; }
    public int getQtdVendas() { return qtdVendas; }
    public String getTotalVendidoFormatado() { return MainFX.formatarValorAbreviado(totalVendido); }
    public String getLucroTotalFormatado() { return MainFX.formatarValorAbreviado(lucroTotal); }
    public String getPrejuizoFormatado() { return MainFX.formatarValorAbreviado(prejuizoBaixas); }
}