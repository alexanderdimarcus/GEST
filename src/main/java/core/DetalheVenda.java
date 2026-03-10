package core;

import app.MainFX;

public class DetalheVenda {
    private final String dataHora;
    private final String produto;
    private final int qtd;
    private final double subtotal;
    private final double lucro;

    public DetalheVenda(String dataHora, String produto, int qtd, double subtotal, double lucro) {
        this.dataHora = dataHora;
        this.produto = produto;
        this.qtd = qtd;
        this.subtotal = subtotal;
        this.lucro = lucro;
    }

    public String getDataHora() { return dataHora; }
    public String getProduto() { return produto; }
    public int getQtd() { return qtd; }
    public String getSubtotalFormatado() { return MainFX.formatarValorAbreviado(subtotal); }
    public String getLucroFormatado() { return MainFX.formatarValorAbreviado(lucro); }
}