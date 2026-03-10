package core;

public abstract class Produto implements IRelatorio {
    protected String descricao;
    protected String categoria;
    protected int codigo;
    protected int qntdDisp;
    protected double valorUnitVenda;
    protected double percentualLucro;
    protected Fornecedor fornecedor;
    protected String fabricante;

    public Produto(int codigo, String descricao, String categoria, int qntdDisp, double valorUnitVenda, double percentualLucro, Fornecedor fornecedor, String fabricante) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.qntdDisp = qntdDisp;
        this.valorUnitVenda = valorUnitVenda;
        this.percentualLucro = percentualLucro;
        this.fornecedor = fornecedor;
        this.fabricante = fabricante;
    }

    public Produto(int codigo, String descricao, String categoria, int qntdDisp, double valorUnitVenda, double percentualLucro, Fornecedor fornecedor) {
        this(codigo, descricao, categoria, qntdDisp, valorUnitVenda, percentualLucro, fornecedor, "Não informada");
    }

    public abstract void exibirDetalhes();

    public void adicionarEstoque(int quantidade) {
        if (quantidade > 0) this.qntdDisp += quantidade;
    }

    public void removerEstoque(int quantidade) throws Exception {
        if (quantidade <= 0) throw new Exception("Quantidade deve ser positiva.");
        if (this.qntdDisp >= quantidade) this.qntdDisp -= quantidade;
        else throw new Exception("Erro: Estoque insuficiente.");
    }

    public double getLucroUnitarioCalculado() { return this.valorUnitVenda * (this.percentualLucro / 100.0); }

    public int getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getQntdDisp() { return qntdDisp; }
    public double getValorUnitVenda() { return valorUnitVenda; }
    public void setValorUnitVenda(double valorUnitVenda) { this.valorUnitVenda = valorUnitVenda; }
    public double getPercentualLucro() { return percentualLucro; }
    public void setPercentualLucro(double percentualLucro) { this.percentualLucro = percentualLucro; }
    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }

    public String getFabricante() { return fabricante; } // Getter da marca!
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    @Override
    public void gerarCabecalho() { System.out.println("Relatório do Produto: " + this.descricao); }
    @Override
    public void gerarCorpo() { System.out.println("Marca: " + this.fabricante); }
    @Override
    public void imprimirRelatorio() { gerarCabecalho(); gerarCorpo(); exibirDetalhes(); }
}