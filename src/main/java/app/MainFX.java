package app;

import core.*;
import core.Sessao;
import dao.ProdutoDAO;
import dao.ConexaoBD;

import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import java.sql.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.beans.property.SimpleStringProperty;

import java.util.Comparator;

public class MainFX extends Application {

    private BorderPane rootLayout;
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @Override
    public void start(Stage stage) {
        // Inicializa o tema moderno do AtlantaFX
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        rootLayout = new BorderPane();
        rootLayout.setLeft(criarMenuLateral());
        rootLayout.setCenter(criarDashboard()); // Tela Inicial

        Scene scene = new Scene(rootLayout, 1200, 768);
        stage.setTitle("GEST - Sistema de Gestão de Estoque");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    // =================================================================================
    // MENU LATERAL (AtlantaFX Styled)
    // =================================================================================
    private ScrollPane criarMenuLateral() {
        VBox sidebar = new VBox();
        sidebar.setMinWidth(260); // <-- MÁGICA 1: Nunca amassa!
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #ffffff;"); // Fundo branco limpo

        // 1. LOGO REFINADA
        ImageView logoView = new ImageView();
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/logoG.png");
            if (is == null) is = getClass().getResourceAsStream("/images/logoG.png");
            if (is != null) {
                logoView.setImage(new Image(is));
                logoView.setFitWidth(160);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
                logoView.setCache(true);
            }
        } catch (Exception ignored) {}

        HBox logoContainer = new HBox(logoView);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        logoContainer.setPadding(new Insets(0, 0, 10, 0));

        // 2. INFORMAÇÕES DO USUÁRIO
        Label lblUser = new Label(Sessao.getUsuario().getNomeCompleto());
        lblUser.getStyleClass().addAll(Styles.TITLE_4);
        lblUser.setWrapText(true);

        String cargoTexto = Sessao.getUsuario().getCargo() != null ? Sessao.getUsuario().getCargo() : "Colaborador";
        Label lblCargo = new Label(cargoTexto.toUpperCase());
        lblCargo.getStyleClass().addAll(Styles.TEXT_SMALL, Styles.TEXT_MUTED);

        Label lblAvatar = new Label(Sessao.getUsuario().getLogin().substring(0, 1).toUpperCase());
        lblAvatar.setStyle("-fx-background-color: #0969da; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 35; -fx-min-height: 35; -fx-alignment: center;");

        VBox userDetails = new VBox(2, lblUser, lblCargo);
        HBox userBox = new HBox(10, lblAvatar, userDetails);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(10, 0, 10, 0));

        // 3. BOTÃO DE SAIR
        Button btnLogout = new Button("Sair");
        btnLogout.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL);
        btnLogout.setPrefWidth(100);
        btnLogout.setOnAction(e -> onLogout());

        VBox header = new VBox(15, logoContainer, userBox, btnLogout);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        // --- Navegação
        Button btnDash = criarBotaoMenu("Início");
        Button btnVendas = criarBotaoMenu("Vendas");
        Button btnEstoque = criarBotaoMenu("Estoque");
        Button btnFornecedores = criarBotaoMenu("Fornecedores");
        Button btnRelatorios = criarBotaoMenu("Relatórios & Logs");

        btnDash.setOnAction(e -> rootLayout.setCenter(criarDashboard()));
        btnVendas.setOnAction(e -> rootLayout.setCenter(criarTelaVendas()));
        btnEstoque.setOnAction(e -> rootLayout.setCenter(criarTelaEstoque()));
        btnFornecedores.setOnAction(e -> rootLayout.setCenter(criarTelaFornecedores()));
        btnRelatorios.setOnAction(e -> rootLayout.setCenter(criarTelaRelatorios()));

        VBox botoesLayout = new VBox(2, btnDash, btnVendas, btnEstoque, btnFornecedores, btnRelatorios);

        // --- STATUS DA CONEXÃO ---
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label lblStatusBD = new Label("● Conectado: PostgreSQL");
        lblStatusBD.getStyleClass().add(Styles.TEXT_SMALL);
        lblStatusBD.setPadding(new Insets(0, 0, 10, 20));

        try (Connection conn = ConexaoBD.conectar()) {
            if (conn != null && !conn.isClosed()) {
                lblStatusBD.setStyle("-fx-text-fill: green; -fx-font-size: 11px;");
            }
        } catch (Exception e) {
            lblStatusBD.setText("● Offline / Erro BD");
            lblStatusBD.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        }

        Label lblVersao = new Label("v1.0.0 (Sprint 3)");
        lblVersao.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL);
        lblVersao.setPadding(new Insets(0, 0, 20, 20));

        sidebar.getChildren().addAll(header, botoesLayout, spacer, lblStatusBD, lblVersao);

        // <-- MÁGICA 2: Envelopando no ScrollPane -->
        ScrollPane scrollSidebar = new ScrollPane(sidebar);
        scrollSidebar.setFitToWidth(true);
        scrollSidebar.setFitToHeight(true);
        scrollSidebar.getStyleClass().add("edge-to-edge");
        // A borda da direita fica no ScrollPane agora!
        scrollSidebar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d7de; -fx-border-width: 0 1 0 0;");

        return scrollSidebar;
    }

    private Button criarBotaoMenu(String texto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.BASELINE_LEFT);
        btn.setPadding(new Insets(12, 20, 12, 20));
        // Botões flat (sem borda/fundo até passar o mouse)
        btn.getStyleClass().addAll(Styles.FLAT, Styles.TITLE_4);
        return btn;
    }

    // =================================================================================
    // TELA 0: DASHBOARD (H13 - Turbinado com Finanças)
    // =================================================================================
    private ScrollPane criarDashboard() {
        Label lblTitulo = new Label("Visão Geral");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        // --- 1. BUSCA DE DADOS ---
        List<Produto> listaProdutos = produtoDAO.listarTodos();

        // Puxamos o histórico AQUI EM CIMA agora para fazer a matemática!
        dao.MovimentacaoDAO daoMov = new dao.MovimentacaoDAO();
        List<core.LogMovimentacao> historicoCompleto = daoMov.listarHistorico();

        // === CÁLCULOS DO ESTOQUE ATUAL ===
        int totalProdutos = listaProdutos.size();
        long qtdEstoqueBaixo = listaProdutos.stream().filter(this::isEstoqueBaixo).count();
        long qtdVencidos = listaProdutos.stream().filter(this::isVencido).count();
        long qtdAVencer = listaProdutos.stream().filter(this::isVencendo).count();
        double valorEmEstoque = listaProdutos.stream().mapToDouble(p -> p.getQntdDisp() * p.getValorUnitVenda()).sum();
        double lucroPotencial = listaProdutos.stream().mapToDouble(p -> p.getQntdDisp() * p.getLucroUnitarioCalculado()).sum();

        // === NOVOS CÁLCULOS: DESEMPENHO DO MÊS ATUAL ===
        java.time.YearMonth mesAtual = java.time.YearMonth.now();

        // 1. Filtra os logs para pegar APENAS os eventos deste mês
        List<core.LogMovimentacao> logsDoMes = historicoCompleto.stream()
                .filter(log -> {
                    if (log.getDataHora() == null) return false;
                    java.time.LocalDate dataLog = log.getDataHora().toLocalDateTime().toLocalDate();
                    return java.time.YearMonth.from(dataLog).equals(mesAtual);
                }).toList();

        // 2. Matemática das VENDAS (Qtd, Valor e Lucro)
        long qtdVendidaMes = logsDoMes.stream().filter(l -> "VENDA".equals(l.getTipoMovimentacao())).mapToInt(core.LogMovimentacao::getQuantidade).sum();

        double valorVendidoMes = logsDoMes.stream().filter(l -> "VENDA".equals(l.getTipoMovimentacao())).mapToDouble(l -> {
            Produto p = listaProdutos.stream().filter(prod -> prod.getCodigo() == l.getCodigoProduto()).findFirst().orElse(null);
            return p != null ? p.getValorUnitVenda() * l.getQuantidade() : 0.0;
        }).sum();

        double lucroVendidoMes = logsDoMes.stream().filter(l -> "VENDA".equals(l.getTipoMovimentacao())).mapToDouble(l -> {
            Produto p = listaProdutos.stream().filter(prod -> prod.getCodigo() == l.getCodigoProduto()).findFirst().orElse(null);
            return p != null ? p.getLucroUnitarioCalculado() * l.getQuantidade() : 0.0;
        }).sum();

        // 3. Matemática das PERDAS / BAIXAS (Descarte, Roubo, Vencimento)
        long qtdPerdidaMes = logsDoMes.stream().filter(l -> "BAIXA".equals(l.getTipoMovimentacao())).mapToInt(core.LogMovimentacao::getQuantidade).sum();

        double valorPerdidoMes = logsDoMes.stream().filter(l -> "BAIXA".equals(l.getTipoMovimentacao())).mapToDouble(l -> {
            Produto p = listaProdutos.stream().filter(prod -> prod.getCodigo() == l.getCodigoProduto()).findFirst().orElse(null);
            // O prejuízo da baixa é o Custo do produto (Preço de venda - Lucro)
            return p != null ? (p.getValorUnitVenda() - p.getLucroUnitarioCalculado()) * l.getQuantidade() : 0.0;
        }).sum();

        // --- 2. MONTAGEM DOS CARTÕES ---
        FlowPane cards = new FlowPane(20, 20);
        cards.getChildren().addAll(
                criarCard("Vendas no Mês", formatarValorAbreviado(valorVendidoMes), qtdVendidaMes + " itens vendidos"),
                criarCard("Lucro no Mês", formatarValorAbreviado(lucroVendidoMes), "Lucro estimado gerado"),
                criarCard("Prejuízo / Baixas", formatarValorAbreviado(valorPerdidoMes), qtdPerdidaMes + " itens perdidos/descartados"),
                criarCard("Catálogo Ativo", String.valueOf(totalProdutos), "Produtos cadastrados diferentes"),
                criarCard("Estoque Baixo", String.valueOf(qtdEstoqueBaixo), "Reposição (<= 5 un)"),
                criarCard("Vencidos / A Vencer", String.valueOf(qtdVencidos + qtdAVencer), "Descarte ou Promoção"),
                criarCard("Capital em Estoque", formatarValorAbreviado(valorEmEstoque), "Preço de venda total"),
                criarCard("Lucro Estimado Total", formatarValorAbreviado(lucroPotencial), "Se vender tudo")
        );

        // --- 3. TABELA DE ÚLTIMAS MOVIMENTAÇÕES ---
        Label lblRecentes = new Label("Últimas Movimentações (Top 15)");
        lblRecentes.getStyleClass().addAll(Styles.TITLE_3);
        VBox.setMargin(lblRecentes, new Insets(20, 0, 0, 0));

        TableView<core.LogMovimentacao> tabRecentes = new TableView<>();
        tabRecentes.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        VBox.setVgrow(tabRecentes, Priority.ALWAYS);
        tabRecentes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<core.LogMovimentacao, String> colAcao = new TableColumn<>("Ação");
        colAcao.setCellValueFactory(new PropertyValueFactory<>("tipoMovimentacao"));
        colAcao.setMinWidth(120);
        colAcao.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold;");
                    switch (item) {
                        case "ENTRADA" -> setTextFill(javafx.scene.paint.Color.web("#1a7f37"));
                        case "BAIXA" -> setTextFill(javafx.scene.paint.Color.web("#cf222e"));
                        case "VENDA" -> setTextFill(javafx.scene.paint.Color.web("#0969da"));
                    }
                }
            }
        });

        TableColumn<core.LogMovimentacao, Integer> colProd = new TableColumn<>("Cód. Prod");
        colProd.setCellValueFactory(new PropertyValueFactory<>("codigoProduto"));
        colProd.setMinWidth(100);

        TableColumn<core.LogMovimentacao, Integer> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colQtd.setMinWidth(80);

        TableColumn<core.LogMovimentacao, String> colUser = new TableColumn<>("Usuário");
        colUser.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colUser.setMinWidth(180);

        TableColumn<core.LogMovimentacao, String> colData = new TableColumn<>("Data/Hora");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataHoraFormatada"));
        colData.setMinWidth(160);

        tabRecentes.getColumns().addAll(colAcao, colProd, colQtd, colUser, colData);

        // --- 4. POPULANDO A TABELA COM DADOS DO BANCO ---
        List<core.LogMovimentacao> ultimas15 = historicoCompleto.size() > 15 ? historicoCompleto.subList(0, 15) : historicoCompleto;

        tabRecentes.setItems(FXCollections.observableArrayList(ultimas15));
        tabRecentes.setPrefHeight(350); // Garante que a tabela tenha altura mesmo se a tela diminuir

        VBox layout = new VBox(20, lblTitulo, cards, lblRecentes, tabRecentes);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: transparent;"); // Evita conflito de cor
        layout.setMinWidth(950);
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true); // Faz o conteúdo acompanhar a largura da janela
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #ffffff;");

        return scrollPane;
    }

    private VBox criarCard(String titulo, String valor, String subtitulo) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TITLE_4);

        Label lblValor = new Label(valor);
        lblValor.getStyleClass().addAll(Styles.TITLE_1);
        lblValor.setStyle("-fx-font-size: 36px;");

        Label lblSub = new Label(subtitulo);
        lblSub.getStyleClass().addAll(Styles.TEXT_SMALL);

        VBox card = new VBox(5, lblTitulo, lblValor, lblSub);
        card.setPadding(new Insets(20));

        // MÁGICA RESPONSIVA: Garante tamanho mínimo e preferencial
        card.setMinWidth(260); // Nunca fica menor que isso
        card.setPrefSize(280, 120); // Tamanho ideal
        card.setMaxWidth(320); // Impede que estique demais se a tela for gigante

        card.getStyleClass().addAll("card", Styles.ELEVATED_1);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 6px;");
        return card;
    }

    // =================================================================================
    // TELA 1: CONSULTA DE ESTOQUE (Busca Avançada)
    // =================================================================================

    private VBox criarAbaConsulta() {

        // 1. CARREGAMENTO DE DADOS (Master Data)
        // Carregamos tudo do banco uma vez para memória para filtrar rápido
        List<Produto> listaOriginal = produtoDAO.listarTodos();
        ObservableList<Produto> masterData = FXCollections.observableArrayList(listaOriginal);

        // 2. FILTROS E BUSCA (FilteredList)
        // Envolvemos a lista original em uma lista filtrável
        FilteredList<Produto> filteredData = new FilteredList<>(masterData, p -> true);

        // Componentes de Filtro
        TextField txtBusca = new TextField();
        txtBusca.setPromptText("🔎 Buscar por nome, código ou categoria...");
        txtBusca.setPrefWidth(320);

        ComboBox<String> cmbOrdenacao = new ComboBox<>();
        cmbOrdenacao.getItems().addAll(
                "Padrão (Código)",
                "Maior Quantidade",
                "Menor Quantidade",
                "Maior Valor",
                "Menor Valor",
                "Nome (A-Z)"
        );
        cmbOrdenacao.setValue("Padrão (Código)");
        cmbOrdenacao.setPrefWidth(180);


// Botão de "Refresh" caso novos dados entrem no banco
        Button btnAtualizar = new Button("Recarregar");
        btnAtualizar.getStyleClass().addAll(Styles.BUTTON_OUTLINED);
        btnAtualizar.setOnAction(e -> {
            masterData.setAll(produtoDAO.listarTodos());
            txtBusca.clear();
        });

        // 1. PRIMEIRO DECLARAMOS A TABELA AQUI EM CIMA!
        TableView<Produto> tabela = new TableView<>();
        tabela.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // 2. AGORA SIM, CRIAMOS OS BOTÕES DE AÇÃO
        Button btnNovo = new Button("Cadastrar Produto");
        btnNovo.getStyleClass().addAll(Styles.SUCCESS);
        btnNovo.setOnAction(e -> abrirModalCadastroProduto(null, masterData));
        Button btnEditar = new Button("Editar Selecionado");
        btnEditar.getStyleClass().addAll(Styles.WARNING);
        btnEditar.setOnAction(e -> {
            Produto selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                mostrarErro("Atenção", "Selecione um produto na tabela clicando nele primeiro.");
                return;
            }
            abrirModalCadastroProduto(selecionado, masterData); // Chama a janela enviando o produto!
        });

        Button btnExcluir = new Button("Excluir Selecionado");
        btnExcluir.getStyleClass().addAll(Styles.DANGER);
        btnExcluir.setOnAction(e -> {
            Produto selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                mostrarErro("Atenção", "Selecione um produto na tabela clicando nele primeiro.");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Tem certeza que deseja excluir o produto '" + selecionado.getDescricao() + "' (Cód: " + selecionado.getCodigo() + ")?",
                    ButtonType.YES, ButtonType.NO);
            confirmacao.setHeaderText("Confirmação de Exclusão");

            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        produtoDAO.excluir(selecionado.getCodigo());
                        masterData.remove(selecionado);
                        mostrarAlerta("Produto excluído com sucesso!");
                    } catch (Exception ex) {
                        mostrarErro("Erro", "Não foi possível excluir o produto: " + ex.getMessage());
                    }
                }
            });
        });

        // 3. MONTAMOS A BARRA DE FERRAMENTAS
        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);
        HBox barraFerramentas = new HBox(10, txtBusca, cmbOrdenacao, btnAtualizar, espacador, btnEditar, btnExcluir, btnNovo);
        barraFerramentas.setAlignment(Pos.CENTER_LEFT);

        // 3. TABELA (SortedList)
        tabela.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // --- Definição das Colunas ---

        TableColumn<Produto, Integer> colId = new TableColumn<>("Cód");
        colId.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colId.setMaxWidth(80);

        TableColumn<Produto, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Produto, String> colCat = new TableColumn<>("Categoria");
        colCat.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colCat.setMaxWidth(150);

        // Coluna Especial: Fabricante (se Cosmético) ou Fornecedor (Geral)
        TableColumn<Produto, String> colOrigem = new TableColumn<>("Fabricante / Fornecedor");
        colOrigem.setCellValueFactory(cellData -> {
            Produto p = cellData.getValue();
            if (p instanceof Cosmetico) {
                return new SimpleStringProperty(((Cosmetico) p).getFabricante());
            }
            return new SimpleStringProperty(p.getFornecedor().getNome());
        });

        TableColumn<Produto, String> colValidade = new TableColumn<>("Validade / Garantia");
        colValidade.setCellValueFactory(cellData -> {
            Produto p = cellData.getValue();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

            if (p instanceof core.ProdutoPerecivel perecivel && perecivel.getDataValidade() != null) {
                return new SimpleStringProperty("Vence: " + sdf.format(perecivel.getDataValidade()));
            } else if (p instanceof core.Cosmetico cosmetico && cosmetico.getDataValidade() != null) {
                return new SimpleStringProperty("Vence: " + sdf.format(cosmetico.getDataValidade()));
            } else if (p instanceof core.Eletronico eletronico && eletronico.getDataGarantia() != null) {
                return new SimpleStringProperty("Garantia: " + sdf.format(eletronico.getDataGarantia()));
            }
            return new SimpleStringProperty("-");
        });

        colValidade.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    Produto p = getTableRow() != null ? getTableRow().getItem() : null;

                    if (p != null && isVencido(p)) {
                        setStyle("-fx-text-fill: #cf222e; -fx-font-weight: bold;"); // Vermelho para VENCIDO
                    } else if (p != null && isVencendo(p)) {
                        setStyle("-fx-text-fill: #b35900; -fx-font-weight: bold;"); // Laranja/Mostarda para A VENCER
                    } else {
                        setStyle("-fx-text-fill: #1a7f37;"); // Verde para OK
                    }
                }
            }
        });

        TableColumn<Produto, Integer> colQtd = new TableColumn<>("Estoque");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("qntdDisp"));
        colQtd.setMaxWidth(100);
        colQtd.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item.toString());
                    Produto p = getTableRow() != null ? getTableRow().getItem() : null;
                    // Fica vermelho APENAS se o estoque estiver baixo
                    if (p != null && isEstoqueBaixo(p)) {
                        setStyle("-fx-text-fill: #cf222e; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #1a7f37;");
                    }
                }
            }
        });

        // --- RESTAURANDO A COLUNA DE VALOR QUE HAVIA SUMIDO ---
        TableColumn<Produto, String> colValor = new TableColumn<>("Valor (R$)");
        colValor.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("R$ %.2f", cellData.getValue().getValorUnitVenda()))
        );
        colValor.setMaxWidth(120);

        tabela.getColumns().addAll(colId, colDesc, colCat, colOrigem, colValidade, colQtd, colValor);


        // =========================================================
        // RESUMO INTELIGENTE E ALERTAS DA TELA DE CONSULTA
        // =========================================================

        // 1. Contagens separadas
        long qtdBaixo = listaOriginal.stream().filter(this::isEstoqueBaixo).count();
        long qtdVencidos = listaOriginal.stream().filter(this::isVencido).count();
        long qtdAVencer = listaOriginal.stream().filter(this::isVencendo).count();

        // 2. Alerta visual de resumo inteligente
        HBox alertaCritico = new HBox(10);
        alertaCritico.setAlignment(Pos.CENTER_LEFT);
        alertaCritico.setPadding(new Insets(10));

        if (qtdBaixo > 0 || qtdVencidos > 0 || qtdAVencer > 0) {
            alertaCritico.setStyle("-fx-background-color: #ffebe9; -fx-border-color: #ff8182; -fx-border-radius: 5;");

            String msg = "⚠ Atenção: ";
            if (qtdBaixo > 0) msg += qtdBaixo + " produto(s) c/ estoque baixo. | ";
            if (qtdVencidos > 0) msg += qtdVencidos + " produto(s) VENCIDO(S). | ";
            if (qtdAVencer > 0) msg += qtdAVencer + " produto(s) a vencer.";

            Label lblAlerta = new Label(msg);
            lblAlerta.setStyle("-fx-text-fill: #cf222e; -fx-font-weight: bold;");
            alertaCritico.getChildren().add(lblAlerta);
        }

        // 4. LÓGICA DE FILTRAGEM (Ao digitar)
        txtBusca.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(produto -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = newValue.toLowerCase();

            // Regras de busca:
            if (String.valueOf(produto.getCodigo()).contains(lowerCaseFilter)) return true;
            if (produto.getDescricao().toLowerCase().contains(lowerCaseFilter)) return true;
            if (produto.getCategoria().toLowerCase().contains(lowerCaseFilter)) return true;
            if (produto instanceof Cosmetico) {
                return ((Cosmetico) produto).getFabricante().toLowerCase().contains(lowerCaseFilter);
            }

            return false; // Não encontrou nada
        }));

        // 5. LÓGICA DE ORDENAÇÃO
        // Envolvemos a Lista Filtrada em uma Lista Ordenada
        SortedList<Produto> sortedData = new SortedList<>(filteredData);

        // Vincula o comparador da tabela (clique no cabeçalho) à lista ordenada
        sortedData.comparatorProperty().bind(tabela.comparatorProperty());

        // Adiciona lógica do ComboBox de Ordenação
        cmbOrdenacao.setOnAction(e -> {
            String selecionado = cmbOrdenacao.getValue();
            switch (selecionado) {
                case "Maior Quantidade":
                    tabela.getSortOrder().clear();
                    colQtd.setSortType(TableColumn.SortType.DESCENDING);
                    tabela.getSortOrder().add(colQtd);
                    break;
                case "Menor Quantidade":
                    tabela.getSortOrder().clear();
                    colQtd.setSortType(TableColumn.SortType.ASCENDING);
                    tabela.getSortOrder().add(colQtd);
                    break;
                case "Maior Valor":
                    // Precisamos ordenar pela propriedade original, não pela String formatada
                    // Pequeno hack: forçar comparador na lista ou usar a coluna se ela fosse numérica
                    // Como a colValor é String (R$), a ordenação padrão falha.
                    // Vamos ordenar a lista diretamente:
                    tabela.getSortOrder().clear(); // Remove ordenação visual de coluna
                    sortedData.setComparator(Comparator.comparingDouble(Produto::getValorUnitVenda).reversed());
                    break;
                case "Menor Valor":
                    tabela.getSortOrder().clear();
                    sortedData.setComparator(Comparator.comparingDouble(Produto::getValorUnitVenda));
                    break;
                case "Nome (A-Z)":
                    tabela.getSortOrder().clear();
                    colDesc.setSortType(TableColumn.SortType.ASCENDING);
                    tabela.getSortOrder().add(colDesc);
                    break;
                default: // Padrão
                    tabela.getSortOrder().clear();
                    colId.setSortType(TableColumn.SortType.ASCENDING);
                    tabela.getSortOrder().add(colId);
                    sortedData.setComparator(null); // Reseta comparadores manuais
                    break;
            }
        });

        tabela.setItems(sortedData); // Define a lista inteligente na tabela
        VBox.setVgrow(tabela, Priority.ALWAYS);

        // Rodapé com totais
        Label lblTotal = new Label();
        lblTotal.getStyleClass().add(Styles.TEXT_MUTED);

        // Criamos uma mini-função para atualizar o texto
        Runnable atualizarContador = () -> lblTotal.setText("Exibindo " + filteredData.size() + " registros");

        // 1. Atualiza quando o filtro de busca muda (ao digitar)
        filteredData.predicateProperty().addListener(o -> atualizarContador.run());

        // 2. A MÁGICA NOVA: Atualiza automaticamente quando um produto é salvo ou recarregado
        masterData.addListener((javafx.collections.ListChangeListener.Change<? extends Produto> c) -> atualizarContador.run());

        atualizarContador.run(); // Define o valor inicial assim que a tela abre

        VBox layout = new VBox(15, barraFerramentas, alertaCritico, tabela, lblTotal);
        layout.setPadding(new Insets(20, 0, 0, 0));

        return layout;
    }


    // =================================================================================
    // MODAL DE CADASTRO E EDIÇÃO DE PRODUTOS (Híbrido)
    // =================================================================================
    private void abrirModalCadastroProduto(Produto produtoEdicao, ObservableList<Produto> listaAtual) {
        boolean isEdicao = (produtoEdicao != null);

        Stage stageModal = new Stage();
        stageModal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stageModal.setTitle(isEdicao ? "Editar Produto: " + produtoEdicao.getDescricao() : "Novo Cadastro de Produto");

        // 1. Campos Básicos
        TextField txtCodigo = new TextField();
        TextField txtDescricao = new TextField();
        TextField txtCategoria = new TextField();
        TextField txtPreco = new TextField();
        aplicarMascaraMoeda(txtPreco);
        TextField txtLucro = new TextField();
        TextField txtQtd = new TextField();

        // 2. ComboBox de Fornecedores
        ComboBox<Fornecedor> cmbFornecedor = new ComboBox<>();
        cmbFornecedor.getItems().addAll(new dao.FornecedorDAO().listarTodos());
        cmbFornecedor.setPromptText("Selecione um fornecedor...");

        // 3. Campos Específicos
        TextField txtValidade = new TextField();
        txtValidade.setPromptText("dd/mm/aaaa");
        aplicarMascaraData(txtValidade);
        TextField txtFabricante = new TextField();
        TextField txtGarantia = new TextField();
        txtGarantia.setPromptText("Ex: 12");

        // 4. ComboBox de Tipo e Área Dinâmica
        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("Cosmético", "Eletrônico", "Perecível");
        cmbTipo.setPromptText("Selecione o tipo...");

        VBox areaDinamica = new VBox(10);
        areaDinamica.setPadding(new Insets(10));
        areaDinamica.setStyle("-fx-background-color: #f6f8fa; -fx-border-color: #d0d7de; -fx-border-radius: 4px;");

        // Listener: Mostra os campos corretos baseados no Tipo escolhido
        cmbTipo.setOnAction(e -> {
            areaDinamica.getChildren().clear();
            String selecionado = cmbTipo.getValue();

            if ("Cosmético".equals(selecionado)) {
                areaDinamica.getChildren().addAll(new Label("Data de Validade:"), txtValidade, new Label("Fabricante:"), txtFabricante);
            } else if ("Eletrônico".equals(selecionado)) {
                areaDinamica.getChildren().addAll(new Label("Meses de Garantia:"), txtGarantia);
            } else if ("Perecível".equals(selecionado)) {
                areaDinamica.getChildren().addAll(new Label("Data de Validade:"), txtValidade);
            }
        });

        // --- PREENCHIMENTO AUTOMÁTICO SE FOR EDIÇÃO ---
        if (isEdicao) {
            txtCodigo.setText(String.valueOf(produtoEdicao.getCodigo()));
            txtCodigo.setDisable(true); // Bloqueia o código (não pode mudar Chave Primária)

            txtDescricao.setText(produtoEdicao.getDescricao());
            txtCategoria.setText(produtoEdicao.getCategoria());
            txtQtd.setText(String.valueOf(produtoEdicao.getQntdDisp()));
            txtPreco.setText(String.format("%.2f", produtoEdicao.getValorUnitVenda()));
            txtLucro.setText(String.format("%.2f", produtoEdicao.getPercentualLucro()));

            // Acha o fornecedor na lista e seleciona
            for (Fornecedor f : cmbFornecedor.getItems()) {
                if (f.getId() == produtoEdicao.getFornecedor().getId()) {
                    cmbFornecedor.setValue(f);
                    break;
                }
            }

            // Preenche os dados específicos e o tipo
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            switch (produtoEdicao) {
                case Cosmetico c -> {
                    cmbTipo.setValue("Cosmético");
                    if (c.getDataValidade() != null) txtValidade.setText(sdf.format(c.getDataValidade()));
                    txtFabricante.setText(c.getFabricante());
                }
                case Eletronico eletronico -> {
                    cmbTipo.setValue("Eletrônico");
                    txtGarantia.setText("12"); // Exemplo base
                }
                case ProdutoPerecivel pp -> {
                    cmbTipo.setValue("Perecível");
                    if (pp.getDataValidade() != null) txtValidade.setText(sdf.format(pp.getDataValidade()));
                }
                default -> {
                }
            }

            cmbTipo.setDisable(true); // O tipo (perecível/eletrônico) não muda depois de criado
            cmbTipo.fireEvent(new javafx.event.ActionEvent()); // Força o painel dinâmico a aparecer
        }

        // 5. Montando o Formulário
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.addRow(0, new Label("Código:"), txtCodigo, new Label("Quantidade:"), txtQtd);
        form.addRow(1, new Label("Descrição:"), txtDescricao, new Label("Categoria:"), txtCategoria);
        form.addRow(2, new Label("Fornecedor:"), cmbFornecedor);
        GridPane.setColumnSpan(cmbFornecedor, 3);
        form.addRow(3, new Label("Preço Venda:"), txtPreco, new Label("Lucro (%):"), txtLucro);
        form.addRow(4, new Label("Tipo:"), cmbTipo);

        Button btnSalvar = new Button(isEdicao ? "Salvar Alterações" : "Salvar Novo Produto");
        btnSalvar.getStyleClass().addAll(Styles.SUCCESS, Styles.LARGE);
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

        // --- LÓGICA DE SALVAMENTO ---
        btnSalvar.setOnAction(event -> {
            try {
                if (txtCodigo.getText().isEmpty() || txtDescricao.getText().isEmpty() || txtPreco.getText().isEmpty() || txtQtd.getText().isEmpty() || cmbFornecedor.getValue() == null || cmbTipo.getValue() == null) {
                    throw new IllegalArgumentException("Preencha todos os campos obrigatórios.");
                }

                int cod = Integer.parseInt(txtCodigo.getText());
                String desc = txtDescricao.getText();
                String cat = txtCategoria.getText();
                int qtd = Integer.parseInt(txtQtd.getText());
                double preco = Double.parseDouble(txtPreco.getText().replaceAll("[^\\d,]", "").replace(",", "."));
                double lucro = txtLucro.getText().isEmpty() ? 0.0 : Double.parseDouble(txtLucro.getText().replace(",", "."));
                Fornecedor forn = cmbFornecedor.getValue();
                String tipo = cmbTipo.getValue();

                Produto novoProduto = null;
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);

                if ("Cosmético".equals(tipo)) {
                    java.util.Date validade = sdf.parse(txtValidade.getText());
                    novoProduto = new Cosmetico(cod, desc, cat, qtd, preco, lucro, forn, validade, txtFabricante.getText());
                } else if ("Eletrônico".equals(tipo)) {
                    int garantia = Integer.parseInt(txtGarantia.getText());
                    novoProduto = new Eletronico(cod, desc, cat, qtd, preco, lucro, forn, garantia);
                } else if ("Perecível".equals(tipo)) {
                    java.util.Date validade = sdf.parse(txtValidade.getText());
                    novoProduto = new ProdutoPerecivel(cod, desc, cat, qtd, preco, lucro, forn, validade);
                }

                dao.ProdutoDAO daoProd = new dao.ProdutoDAO();
                if (novoProduto != null) {
                    if (isEdicao) {
                        daoProd.atualizar(novoProduto);
                        mostrarAlerta("Produto atualizado com sucesso!");
                    } else {
                        daoProd.salvar(novoProduto);
                        mostrarAlerta("Produto salvo com sucesso!");
                    }
                }

                listaAtual.setAll(daoProd.listarTodos());
                stageModal.close();

            } catch (Exception ex) {
                mostrarErro("Erro ao Salvar", ex.getMessage());
            }
        });

        VBox layout = new VBox(20, form, new Label("Detalhes Específicos:"), areaDinamica, btnSalvar);
        layout.setPadding(new Insets(30));
        Scene scene = new Scene(layout, 700, 550);
        stageModal.setScene(scene);
        stageModal.showAndWait();
    }

    // =================================================================================
    // TELA EXTRA: FRENTE DE CAIXA / VENDAS (PDV)
    // =================================================================================
    private ScrollPane criarTelaVendas() {
        Label lblTitulo = new Label("Caixa");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        // Lista que vai guardar os itens do carrinho
        ObservableList<ItemCarrinho> carrinho = FXCollections.observableArrayList();
        dao.ProdutoDAO daoProduto = new dao.ProdutoDAO();

        // --- LADO ESQUERDO: Adicionar Produto ---
        VBox painelEsquerdo = new VBox(15);
        painelEsquerdo.setPrefWidth(350);
        painelEsquerdo.setPadding(new Insets(20));
        painelEsquerdo.setStyle("-fx-background-color: #f6f8fa; -fx-border-color: #d0d7de; -fx-border-radius: 6px;");

        Label lblAdd = new Label("Adicionar Item");
        lblAdd.getStyleClass().add(Styles.TITLE_3);

        TextField txtCod = new TextField();
        txtCod.setPromptText("Digite o Cód");
        TextField txtQtd = new TextField("1"); // Padrão é 1
        txtQtd.setPromptText("Qtd");

        // Info do produto "bipado"
        Label lblInfo = new Label("Aguardando produto...");
        lblInfo.getStyleClass().add(Styles.TEXT_MUTED);
        lblInfo.setWrapText(true);

        Button btnAdd = new Button("Adicionar ao Carrinho ➕");
        btnAdd.getStyleClass().addAll(Styles.ACCENT, Styles.LARGE);
        btnAdd.setMaxWidth(Double.MAX_VALUE);

        painelEsquerdo.getChildren().addAll(lblAdd, new Label("Código do Produto:"), txtCod, new Label("Quantidade:"), txtQtd, btnAdd, lblInfo);

        // --- LADO DIREITO: Tabela (Carrinho) e Total ---
        VBox painelDireito = new VBox(15);
        HBox.setHgrow(painelDireito, Priority.ALWAYS); // Cresce para ocupar a tela

        TableView<ItemCarrinho> tabelaCart = new TableView<>();
        tabelaCart.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        VBox.setVgrow(tabelaCart, Priority.ALWAYS);
        tabelaCart.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<ItemCarrinho, String> colCod = new TableColumn<>("Cód");
        colCod.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCod.setMinWidth(80);

        TableColumn<ItemCarrinho, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colDesc.setMinWidth(250);

        TableColumn<ItemCarrinho, String> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colQtd.setMinWidth(80);

        TableColumn<ItemCarrinho, String> colUn = new TableColumn<>("Vlr. Unitário");
        colUn.setCellValueFactory(new PropertyValueFactory<>("valorUnitarioFormatado"));
        colUn.setMinWidth(120);

        TableColumn<ItemCarrinho, String> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(new PropertyValueFactory<>("subtotalFormatado"));
        colSub.setMinWidth(120);

        tabelaCart.getColumns().addAll(colCod, colDesc, colQtd, colUn, colSub);
        tabelaCart.setItems(carrinho);

        // Rodapé com Total e Botão Finalizar
        Label lblTextoTotal = new Label("TOTAL:");
        lblTextoTotal.getStyleClass().addAll(Styles.TITLE_2, Styles.TEXT_MUTED);

        Label lblTotalValor = new Label("R$ 0,00");
        lblTotalValor.getStyleClass().add(Styles.TITLE_1);
        lblTotalValor.setStyle("-fx-font-size: 40px; -fx-text-fill: #1a7f37;"); // Verde grandão

        Button btnFinalizar = new Button("Finalizar Venda");
        btnFinalizar.getStyleClass().addAll(Styles.SUCCESS, Styles.LARGE);
        btnFinalizar.setPrefWidth(250);
        btnFinalizar.setPrefHeight(60);
        btnFinalizar.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        HBox rodapeTotal = new HBox(20, lblTextoTotal, lblTotalValor, new Region(), btnFinalizar);
        rodapeTotal.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(rodapeTotal.getChildren().get(2), Priority.ALWAYS);

        painelDireito.getChildren().addAll(tabelaCart, rodapeTotal);

        // --- LÓGICA DO CARRINHO ---

        txtCod.setOnAction(e -> {
            try {
                int cod = Integer.parseInt(txtCod.getText());
                // Busca rápida do produto
                Produto pBusca = daoProduto.listarTodos().stream().filter(p -> p.getCodigo() == cod).findFirst().orElse(null);

                if (pBusca != null) {
                    lblInfo.setText("Produto: " + pBusca.getDescricao() + " | Estoque: " + pBusca.getQntdDisp());
                    lblInfo.setStyle("-fx-text-fill: #0969da; -fx-font-weight: bold;"); // Azul para pré-visualização
                    txtQtd.requestFocus();
                } else {
                    lblInfo.setText("❌ Produto não encontrado!");
                    lblInfo.setStyle("-fx-text-fill: red;");
                    txtCod.selectAll();
                }
            } catch (NumberFormatException ex) {
                lblInfo.setText("❌ Digite um código válido.");
                lblInfo.setStyle("-fx-text-fill: red;");
            }
        });

        txtQtd.setOnAction(e -> btnAdd.fire());

        Runnable atualizarTotal = () -> {
            double soma = carrinho.stream().mapToDouble(ItemCarrinho::getSubtotal).sum();
            lblTotalValor.setText(String.format("R$ %.2f", soma));
        };

        btnAdd.setOnAction(e -> {
            try {
                int cod = Integer.parseInt(txtCod.getText());
                int qtd = Integer.parseInt(txtQtd.getText());

                if (qtd <= 0) throw new NumberFormatException();

                Produto pEncontrado = daoProduto.listarTodos().stream().filter(p -> p.getCodigo() == cod).findFirst().orElse(null);

                if (pEncontrado == null) {
                    lblInfo.setText("❌ Produto não encontrado!");
                    lblInfo.setStyle("-fx-text-fill: red;");
                    return;
                }

                ItemCarrinho itemExistente = carrinho.stream().filter(i -> i.getCodigo() == cod).findFirst().orElse(null);

                int qtdTotalDesejada = qtd + (itemExistente != null ? itemExistente.getQuantidade() : 0);

                if (qtdTotalDesejada > pEncontrado.getQntdDisp()) {
                    lblInfo.setText("⚠ Estoque insuficiente! (Disponível: " + pEncontrado.getQntdDisp() + ")");
                    lblInfo.setStyle("-fx-text-fill: red;");
                    return;
                }

                if (itemExistente != null) {
                    itemExistente.adicionarQuantidade(qtd);
                    tabelaCart.refresh();
                } else {
                    carrinho.add(new ItemCarrinho(pEncontrado, qtd));
                }

                lblInfo.setText("✅ Adicionado: " + pEncontrado.getDescricao());
                lblInfo.setStyle("-fx-text-fill: green;");
                txtCod.clear();
                txtQtd.setText("1");
                txtCod.requestFocus();

                atualizarTotal.run();

            } catch (NumberFormatException ex) {
                lblInfo.setText("❌ Código e Quantidade devem ser números válidos.");
                lblInfo.setStyle("-fx-text-fill: red;");
            }
        });

        // --- AÇÃO DO BOTÃO FINALIZAR VENDA ---
        btnFinalizar.setOnAction(e -> {
            if (carrinho.isEmpty()) {
                mostrarErro("Carrinho Vazio", "Adicione produtos antes de finalizar a venda.");
                return;
            }

            // Exibe janela de confirmação para o vendedor
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Deseja finalizar a venda no valor total de " + lblTotalValor.getText() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirmacao.setHeaderText("Confirmar Venda");

            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        dao.MovimentacaoDAO daoMov = new dao.MovimentacaoDAO();
                        String usuarioAtual = core.Sessao.getUsuario().getNomeCompleto();

                        // Pega a lista do JavaFX, converte para uma lista padrão do Java e envia para o DAO!
                        daoMov.registrarVenda(new java.util.ArrayList<>(carrinho), usuarioAtual);

                        mostrarAlerta("Venda finalizada com sucesso!");

                        // Limpa o PDV para o próximo cliente da fila
                        carrinho.clear();
                        lblTotalValor.setText("R$ 0,00");
                        txtCod.clear();
                        txtQtd.setText("1");
                        lblInfo.setText("Caixa livre.");
                        lblInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        txtCod.requestFocus(); // Foco no código de barras!

                    } catch (java.sql.SQLException ex) {
                        mostrarErro("Venda Cancelada", ex.getMessage());
                    }
                }
            });
        });

        tabelaCart.setMinHeight(300);

        HBox split = new HBox(30, painelEsquerdo, painelDireito);
        VBox layout = new VBox(20, lblTitulo, split);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: transparent;");
        layout.setMinWidth(1050);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #ffffff;");

        return scrollPane;
    }

    // =================================================================================
    // TELA 4: RELATÓRIOS E LOGS (Agora com Abas Financeiras)
    // =================================================================================
    private ScrollPane criarTelaRelatorios() {
        Label lblTitulo = new Label("Relatórios");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        Label lblSub = new Label("Acompanhe o desempenho financeiro e o histórico de segurança do sistema.");
        lblSub.getStyleClass().add(Styles.TEXT_MUTED);

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().addAll(Styles.DENSE);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // ==========================================
        // ABA 1: DESEMPENHO FINANCEIRO MENSAL
        // ==========================================
        TableView<ResumoMes> tabelaFinancas = new TableView<>();
        tabelaFinancas.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        tabelaFinancas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(tabelaFinancas, Priority.ALWAYS);

        TableColumn<ResumoMes, String> colMes = new TableColumn<>("Mês/Ano");
        colMes.setCellValueFactory(new PropertyValueFactory<>("mesAno"));

        TableColumn<ResumoMes, Integer> colQtdVendas = new TableColumn<>("Itens Vendidos");
        colQtdVendas.setCellValueFactory(new PropertyValueFactory<>("qtdVendas"));

        TableColumn<ResumoMes, String> colTotalVendido = new TableColumn<>("Faturamento Bruto");
        colTotalVendido.setCellValueFactory(new PropertyValueFactory<>("totalVendidoFormatado"));

        TableColumn<ResumoMes, String> colLucro = new TableColumn<>("Lucro Estimado");
        colLucro.setCellValueFactory(new PropertyValueFactory<>("lucroTotalFormatado"));
        colLucro.setStyle("-fx-text-fill: #1a7f37; -fx-font-weight: bold;"); // Verde

        TableColumn<ResumoMes, String> colPrejuizo = new TableColumn<>("Prejuízo (Baixas/Vencidos)");
        colPrejuizo.setCellValueFactory(new PropertyValueFactory<>("prejuizoFormatado"));
        colPrejuizo.setStyle("-fx-text-fill: #cf222e; -fx-font-weight: bold;"); // Vermelho

        tabelaFinancas.getColumns().addAll(colMes, colQtdVendas, colTotalVendido, colLucro, colPrejuizo);

        tabelaFinancas.setRowFactory(tv -> {
            TableRow<core.ResumoMes> row = new TableRow<>();

            row.styleProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then("")
                            .otherwise("-fx-cursor: hand;")
            );

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    core.ResumoMes rowData = row.getItem();
                    abrirModalDetalhesMes(rowData.getMesAno()); // Chama a nova janela passando o mês!
                }
            });
            return row;
        });


        Runnable carregarFinancas = () -> {
            dao.MovimentacaoDAO daoMov = new dao.MovimentacaoDAO();
            List<core.LogMovimentacao> historico = daoMov.listarHistorico();
            List<Produto> produtos = produtoDAO.listarTodos();

            java.util.Map<String, ResumoMes> mapaMeses = new java.util.TreeMap<>(java.util.Collections.reverseOrder());
            java.time.format.DateTimeFormatter fmtStr = java.time.format.DateTimeFormatter.ofPattern("MM/yyyy");
            java.time.format.DateTimeFormatter fmtSort = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");

            for (core.LogMovimentacao log : historico) {
                if (log.getDataHora() == null) continue;
                java.time.LocalDate data = log.getDataHora().toLocalDateTime().toLocalDate();
                String chaveOrdem = data.format(fmtSort);
                String labelMes = data.format(fmtStr);

                mapaMeses.putIfAbsent(chaveOrdem, new ResumoMes(labelMes));
                ResumoMes resumo = mapaMeses.get(chaveOrdem);

                Produto p = produtos.stream().filter(prod -> prod.getCodigo() == log.getCodigoProduto()).findFirst().orElse(null);

                if (p != null) {
                    if ("VENDA".equals(log.getTipoMovimentacao())) {
                        resumo.addVenda(log.getQuantidade(), p.getValorUnitVenda() * log.getQuantidade(), p.getLucroUnitarioCalculado() * log.getQuantidade());
                    } else if ("BAIXA".equals(log.getTipoMovimentacao())) {
                        // O prejuízo é o preço de custo (venda - lucro)
                        resumo.addBaixa((p.getValorUnitVenda() - p.getLucroUnitarioCalculado()) * log.getQuantidade());
                    }
                }
            }
            tabelaFinancas.setItems(FXCollections.observableArrayList(mapaMeses.values()));
        };
        carregarFinancas.run();

        Button btnAttFinancas = new Button("🔄 Atualizar Relatório");
        btnAttFinancas.getStyleClass().add(Styles.BUTTON_OUTLINED);
        btnAttFinancas.setOnAction(e -> carregarFinancas.run());
        HBox barraFinancas = new HBox(btnAttFinancas);
        barraFinancas.setAlignment(Pos.CENTER_RIGHT);

        VBox layoutFinanceiro = new VBox(15, barraFinancas, tabelaFinancas);
        layoutFinanceiro.setPadding(new Insets(20));
        Tab tabFinanceiro = new Tab("Desempenho Financeiro (Mensal)", layoutFinanceiro);

        // ==========================================
        // ABA 2: LOGS (Código Original)
        // ==========================================
        TableView<core.LogMovimentacao> tabelaLogs = new TableView<>();
        tabelaLogs.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        tabelaLogs.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(tabelaLogs, Priority.ALWAYS);

        TableColumn<core.LogMovimentacao, String> colData = new TableColumn<>("Data / Hora");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataHoraFormatada"));
        colData.setMaxWidth(160);

        TableColumn<core.LogMovimentacao, String> colUser = new TableColumn<>("Usuário Responsável");
        colUser.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        TableColumn<core.LogMovimentacao, String> colTipo = new TableColumn<>("Ação");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimentacao"));
        colTipo.setMaxWidth(100);
        colTipo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); } else {
                    setText(item); setStyle("-fx-font-weight: bold;");
                    switch (item) {
                        case "ENTRADA" -> setTextFill(javafx.scene.paint.Color.web("#1a7f37"));
                        case "BAIXA" -> setTextFill(javafx.scene.paint.Color.web("#cf222e"));
                        case "VENDA" -> setTextFill(javafx.scene.paint.Color.web("#0969da"));
                    }
                }
            }
        });

        TableColumn<core.LogMovimentacao, Integer> colCod = new TableColumn<>("Cód");
        colCod.setCellValueFactory(new PropertyValueFactory<>("codigoProduto"));
        colCod.setMaxWidth(80);

        TableColumn<core.LogMovimentacao, Integer> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colQtd.setMaxWidth(80);

        TableColumn<core.LogMovimentacao, String> colMotivo = new TableColumn<>("Motivo");
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        TableColumn<core.LogMovimentacao, String> colObs = new TableColumn<>("Observações");
        colObs.setCellValueFactory(new PropertyValueFactory<>("observacao"));

        tabelaLogs.getColumns().addAll(colData, colUser, colTipo, colCod, colQtd, colMotivo, colObs);

        dao.MovimentacaoDAO daoMov = new dao.MovimentacaoDAO();
        ObservableList<core.LogMovimentacao> listaLogs = FXCollections.observableArrayList(daoMov.listarHistorico());
        tabelaLogs.setItems(listaLogs);

        Button btnAtualizarLogs = new Button("🔄 Atualizar Histórico");
        btnAtualizarLogs.getStyleClass().add(Styles.BUTTON_OUTLINED);
        btnAtualizarLogs.setOnAction(e -> listaLogs.setAll(daoMov.listarHistorico()));
        HBox barraLogs = new HBox(btnAtualizarLogs);
        barraLogs.setAlignment(Pos.CENTER_RIGHT);

        VBox layoutLogs = new VBox(15, barraLogs, tabelaLogs);
        layoutLogs.setPadding(new Insets(20));
        Tab tabLogs = new Tab("Movimentações", layoutLogs);

        // --- Adiciona abas e finaliza tela ---
        tabPane.getTabs().addAll(tabFinanceiro, tabLogs);

        // Previne amassar a tabela e aplica scroll
        tabelaFinancas.setMinHeight(400);
        tabelaLogs.setMinHeight(400);

        VBox layout = new VBox(15, lblTitulo, lblSub, tabPane);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: transparent;");
        layout.setMinWidth(1000);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #ffffff;");

        return scrollPane;
    }

    // =================================================================================
    // TELA UNIFICADA: ESTOQUE (Consulta + Entradas + Baixas)
    // =================================================================================
    private ScrollPane criarTelaEstoque() {
        Label lblTitulo = new Label("Gestão de Estoque");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().addAll(Styles.DENSE);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Nossas 3 funcionalidades agora vivem lado a lado de forma super limpa!
        Tab tabConsulta = new Tab("Consultar e Gerenciar", criarAbaConsulta());
        Tab tabEntrada = new Tab("Adicionar Produtos", criarFormularioEntradaLote());
        Tab tabBaixa = new Tab("Baixa de Perdas/Ajustes", criarFormularioBaixa());

        tabPane.getTabs().addAll(tabConsulta, tabEntrada, tabBaixa);

        tabPane.setMinHeight(650);

        VBox layout = new VBox(20, lblTitulo, tabPane);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: transparent;");
        layout.setMinWidth(1050);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #ffffff;");

        return scrollPane;
    }

    // =================================================================================
    // FORMULÁRIO DE BAIXA DE ESTOQUE
    // =================================================================================
    private VBox criarFormularioBaixa() {
        // --- 1. ÁREA DE BUSCA ---
        TextField txtCod = new TextField();
        txtCod.setPromptText("Ex: 101");
        Button btnBuscar = new Button("Buscar Produto");
        btnBuscar.getStyleClass().addAll(Styles.ACCENT);

        HBox boxBusca = new HBox(10, new Label("Código do Produto:"), txtCod, btnBuscar);
        boxBusca.setAlignment(Pos.CENTER_LEFT);

        // --- 2. ÁREA DE INFORMAÇÃO DO PRODUTO (Visual) ---
        Label lblNomeProduto = new Label("Produto: (Nenhum selecionado)");
        lblNomeProduto.getStyleClass().add(Styles.TEXT_MUTED);
        Label lblEstoqueAtual = new Label("Estoque Disponível: -");
        lblEstoqueAtual.getStyleClass().add(Styles.TEXT_MUTED);

        VBox boxInfo = new VBox(5, lblNomeProduto, lblEstoqueAtual);
        boxInfo.setPadding(new Insets(10));
        boxInfo.setStyle("-fx-background-color: #f6f8fa; -fx-border-color: #d0d7de; -fx-border-radius: 4px;");

        // --- 3. ÁREA DE BAIXA (Com o Motivo que você sugeriu!) ---
        TextField txtQtd = new TextField();
        txtQtd.setPromptText("Qtd a remover");

        ComboBox<String> cmbMotivo = new ComboBox<>();
        cmbMotivo.getItems().addAll("Produto Danificado", "Vencimento (Perecível)", "Defeito / Acionamento de Garantia", "Roubo / Furto", "Ajuste de Inventário (Erro de contagem)");
        cmbMotivo.setPromptText("Selecione o motivo da baixa...");
        cmbMotivo.setPrefWidth(300);

        TextField txtObservacao = new TextField();
        txtObservacao.setPromptText("Detalhes extras (Ex: Garantia acionada pelo cliente João)");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.addRow(0, new Label("Quantidade:"), txtQtd);
        form.addRow(1, new Label("Motivo:"), cmbMotivo);
        form.addRow(2, new Label("Observações:"), txtObservacao);

        Button btnSalvar = new Button("Registrar Baixa do Produto");
        btnSalvar.getStyleClass().addAll(Styles.DANGER, Styles.LARGE);
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

        // =================================================================
        // LÓGICA DE BUSCA E SALVAMENTO
        // =================================================================

        dao.ProdutoDAO daoProduto = new dao.ProdutoDAO();
        dao.MovimentacaoDAO daoMov = new dao.MovimentacaoDAO();

        final Produto[] produtoSelecionado = {null};

        txtCod.setOnAction(e -> btnBuscar.fire());
        btnBuscar.setOnAction(e -> {

            try {
                int codBusca = Integer.parseInt(txtCod.getText());

                Produto pEncontrado = daoProduto.listarTodos().stream()
                        .filter(p -> p.getCodigo() == codBusca)
                        .findFirst().orElse(null);

                if (pEncontrado != null) {
                    produtoSelecionado[0] = pEncontrado;
                    lblNomeProduto.setText("Produto: " + pEncontrado.getDescricao());
                    lblNomeProduto.getStyleClass().remove(Styles.TEXT_MUTED); // Tira o tom cinza

                    lblEstoqueAtual.setText("Estoque Disponível: " + pEncontrado.getQntdDisp() + " un.");
                    if(pEncontrado.getQntdDisp() <= 0) {
                        lblEstoqueAtual.setStyle("-fx-text-fill: #cf222e; -fx-font-weight: bold;"); // Vermelho
                    } else {
                        lblEstoqueAtual.setStyle("-fx-text-fill: #1a7f37; -fx-font-weight: bold;"); // Verde
                    }
                    txtQtd.requestFocus();
                } else {
                    produtoSelecionado[0] = null;
                    lblNomeProduto.setText("Produto não encontrado no sistema!");
                    lblNomeProduto.setStyle("-fx-text-fill: #cf222e;");
                    lblEstoqueAtual.setText("Estoque Disponível: -");
                }
            } catch (NumberFormatException ex) {
                mostrarErro("Formato Inválido", "Por favor, digite um código numérico válido.");
            }
        });

        // AÇÃO DO BOTÃO REGISTRAR BAIXA
        btnSalvar.setOnAction(e -> {
            // Travas de segurança visuais
            if (produtoSelecionado[0] == null) {
                mostrarErro("Atenção", "Primeiro, busque e selecione um produto válido clicando em 'Buscar'.");
                return;
            }
            if (txtQtd.getText().isEmpty() || cmbMotivo.getValue() == null) {
                mostrarErro("Atenção", "Os campos 'Quantidade' e 'Motivo' são obrigatórios!");
                return;
            }

            try {
                int qtdRemover = Integer.parseInt(txtQtd.getText());
                if (qtdRemover <= 0) throw new NumberFormatException();

                String motivo = cmbMotivo.getValue();
                String obs = txtObservacao.getText();

                // Pegamos o nome de quem logou no sistema
                String usuarioResponsavel = core.Sessao.getUsuario().getNomeCompleto();

                daoMov.registrarBaixa(produtoSelecionado[0].getCodigo(), qtdRemover, motivo, obs, usuarioResponsavel);

                mostrarAlerta("Baixa de estoque registrada com sucesso!");

                // Limpa a tela para a próxima operação
                txtCod.clear();
                txtQtd.clear();
                cmbMotivo.setValue(null);
                txtObservacao.clear();
                lblNomeProduto.setText("Produto: (Nenhum selecionado)");
                lblNomeProduto.setStyle("");
                lblEstoqueAtual.setText("Estoque Disponível: -");
                lblEstoqueAtual.setStyle("");
                produtoSelecionado[0] = null;

            } catch (NumberFormatException ex) {
                mostrarErro("Erro de Formato", "A quantidade a remover deve ser um número inteiro e maior que zero.");
            } catch (java.sql.SQLException ex) {
                // Aqui o banco avisa se tentarmos tirar mais do que tem
                mostrarErro("Operação Bloqueada pelo Banco", ex.getMessage());
            }
        });

        VBox layout = new VBox(20, boxBusca, boxInfo, form, btnSalvar);
        layout.setPadding(new Insets(20, 0, 0, 0));
        layout.setMaxWidth(600);

        return layout;
    }


    private VBox criarFormularioEntradaLote() {
        // --- 1. LISTA TEMPORÁRIA ---
        // Reaproveitamos a classe ItemCarrinho para guardar o "Produto + Qtd" na memória
        ObservableList<core.ItemCarrinho> listaLote = FXCollections.observableArrayList();
        dao.ProdutoDAO daoProduto = new dao.ProdutoDAO();

        // --- 2. ÁREA DE INPUT ---
        TextField txtCod = new TextField();
        txtCod.setPromptText("Cód. Produto");
        TextField txtQtd = new TextField();
        txtQtd.setPromptText("Qtd Recebida");

        Label lblInfo = new Label("Aguardando produto...");
        lblInfo.getStyleClass().add(Styles.TEXT_MUTED);

        Button btnAdd = new Button("Adicionar à Lista");
        btnAdd.getStyleClass().addAll(Styles.ACCENT);

        Button btnNovo = new Button("Cadastrar Novo Produto");
        btnNovo.getStyleClass().addAll(Styles.WARNING, Styles.BUTTON_OUTLINED);

        HBox formAdd = new HBox(10, txtCod, txtQtd, btnAdd, lblInfo, new Region(), btnNovo);
        formAdd.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(formAdd.getChildren().get(5), Priority.ALWAYS); // Empurra o btnNovo pro canto direito

        // --- 3. TABELA ---
        TableView<core.ItemCarrinho> tabelaLote = new TableView<>();
        tabelaLote.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        tabelaLote.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaLote.setEditable(true);
        VBox.setVgrow(tabelaLote, Priority.ALWAYS);

        TableColumn<core.ItemCarrinho, String> colCod = new TableColumn<>("Cód");
        colCod.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCod.setMaxWidth(100);

        TableColumn<core.ItemCarrinho, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        // Coluna de Quantidade Editável
        TableColumn<core.ItemCarrinho, Integer> colQtd = new TableColumn<>("Qtd a Adicionar (2 cliques para editar)");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colQtd.setMaxWidth(250);

        // A coluna vira uma "caixinha de texto" quando clicada
        colQtd.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter()));

        // O que acontece quando o usuário aperta Enter após editar:
        colQtd.setOnEditCommit(event -> {
            core.ItemCarrinho item = event.getRowValue();
            int novaQtd = event.getNewValue();

            if (novaQtd > 0) {
                item.setQuantidade(novaQtd); // Salva a nova quantidade
            } else {
                tabelaLote.refresh();
                mostrarErro("Atenção", "A quantidade deve ser maior que zero. Para remover o item, clique no botão ❌.");
            }
        });

        // <--- COLUNA COM O BOTÃO DE EXCLUIR --->
        TableColumn<core.ItemCarrinho, Void> colAcao = new TableColumn<>("Remover");
        colAcao.setMaxWidth(100);
        colAcao.setCellFactory(param -> new TableCell<>() {
            private final Button btnExcluir = new Button("❌");

            {
                btnExcluir.getStyleClass().addAll(Styles.DANGER, Styles.FLAT);
                btnExcluir.setOnAction(event -> {
                    core.ItemCarrinho item = getTableView().getItems().get(getIndex());
                    listaLote.remove(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnExcluir);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        tabelaLote.getColumns().addAll(colCod, colDesc, colQtd, colAcao);
        tabelaLote.setItems(listaLote);

        Button btnSalvarLote = new Button("Processar Entrada");
        btnSalvarLote.getStyleClass().addAll(Styles.SUCCESS, Styles.LARGE);
        btnSalvarLote.setMaxWidth(Double.MAX_VALUE);

        // --- 4. LÓGICA DE ADICIONAR NA LISTA ---

        txtCod.setOnAction(e -> txtQtd.requestFocus());
        txtQtd.setOnAction(e -> btnAdd.fire());
        btnAdd.setOnAction(e -> {
            try {
                int cod = Integer.parseInt(txtCod.getText());
                int qtd = Integer.parseInt(txtQtd.getText());

                if (qtd <= 0) throw new NumberFormatException();

                // Busca o produto no banco
                core.Produto pEncontrado = daoProduto.listarTodos().stream()
                        .filter(p -> p.getCodigo() == cod)
                        .findFirst().orElse(null);

                if (pEncontrado == null) {
                    lblInfo.setText("❌ Produto não encontrado!");
                    lblInfo.setStyle("-fx-text-fill: red;");
                    txtCod.requestFocus();
                    txtCod.selectAll();
                    return;
                }

                // Verifica se já está na lista para apenas somar (evita linhas duplicadas)
                core.ItemCarrinho itemExistente = listaLote.stream()
                        .filter(i -> i.getCodigo() == cod)
                        .findFirst().orElse(null);

                if (itemExistente != null) {
                    itemExistente.adicionarQuantidade(qtd);
                    tabelaLote.refresh(); // Atualiza a tela
                } else {
                    listaLote.add(new core.ItemCarrinho(pEncontrado, qtd));
                }

                lblInfo.setText("✅ " + pEncontrado.getDescricao() + " (" + qtd + "x) na lista.");
                lblInfo.setStyle("-fx-text-fill: green;");
                txtCod.clear();
                txtQtd.clear();
                txtCod.requestFocus();

            } catch (NumberFormatException ex) {
                lblInfo.setText("❌ Código e Quantidade inválidos.");
                lblInfo.setStyle("-fx-text-fill: red;");
                txtCod.requestFocus();
                txtCod.selectAll();
            }
        });

        // --- 5. AÇÃO DO BOTÃO SALVAR LOTE NO BANCO ---
        btnSalvarLote.setOnAction(e -> {
            if (listaLote.isEmpty()) {
                mostrarErro("Lista Vazia", "Adicione pelo menos um produto na lista antes de processar a entrada.");
                return;
            }

            // Pede confirmação para evitar cliques acidentais
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Deseja confirmar a entrada destes " + listaLote.size() + " itens no estoque?",
                    ButtonType.YES, ButtonType.NO);
            confirmacao.setHeaderText("Confirmar Entrada");

            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        dao.MovimentacaoDAO daoMov = new dao.MovimentacaoDAO();
                        String usuarioAtual = core.Sessao.getUsuario().getNomeCompleto();

                        // Envia a lista toda para o banco
                        daoMov.registrarEntradaLote(new java.util.ArrayList<>(listaLote), usuarioAtual);

                        mostrarAlerta("Processado e estoque atualizado com sucesso!");

                        // Limpa a tela para a próxima carreta de produtos
                        listaLote.clear();
                        txtCod.clear();
                        txtQtd.clear();
                        lblInfo.setText("Finalizado. Aguardando novos itens...");
                        lblInfo.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        txtCod.requestFocus();

                    } catch (java.sql.SQLException ex) {
                        mostrarErro("Erro ao processar", ex.getMessage());
                    }
                }
            });
        });
        VBox layout = new VBox(15, new Label("Digite os itens recebidos na nota fiscal:"), formAdd, tabelaLote, btnSalvarLote);
        layout.setPadding(new Insets(20, 0, 0, 0));
        return layout;
    }

    // =================================================================================
    // TELA 3: FORNECEDORES (Nova Versão - Tabela Cheia)
    // =================================================================================
    private ScrollPane criarTelaFornecedores() {
        Label lblTitulo = new Label("Fornecedores Cadastrados");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        // --- TABELA DE CONSULTA ---
        TableView<Fornecedor> tabela = new TableView<>();
        tabela.getStyleClass().add(Styles.STRIPED);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(tabela, Priority.ALWAYS); // Faz a tabela crescer e preencher o espaço

        TableColumn<Fornecedor, String> colNome = new TableColumn<>("Razão Social");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Fornecedor, String> colCnpj = new TableColumn<>("CNPJ/CPF");
        colCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpjCpf"));
        colCnpj.setMaxWidth(200);

        TableColumn<Fornecedor, String> colContato = new TableColumn<>("Contato");
        colContato.setCellValueFactory(new PropertyValueFactory<>("contato"));
        colContato.setMaxWidth(250);

        tabela.getColumns().addAll(colNome, colCnpj, colContato);

        // Carrega dados
        dao.FornecedorDAO fornecedorDAO = new dao.FornecedorDAO();
        ObservableList<Fornecedor> listaFornecedores = FXCollections.observableArrayList(fornecedorDAO.listarTodos());
        tabela.setItems(listaFornecedores);

        // --- BOTÕES DE AÇÃO ---

        Button btnAtualizar = new Button("Atualizar Lista");
        btnAtualizar.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.LARGE);
        btnAtualizar.setOnAction(e -> listaFornecedores.setAll(fornecedorDAO.listarTodos()));

        Button btnNovo = new Button("Cadastrar Fornecedor");
        btnNovo.getStyleClass().addAll(Styles.SUCCESS, Styles.LARGE);
        btnNovo.setOnAction(e -> abrirModalCadastroFornecedor(listaFornecedores));

        Button btnExcluir = new Button("Excluir Selecionado");
        btnExcluir.getStyleClass().addAll(Styles.DANGER, Styles.LARGE);
        btnExcluir.setOnAction(e -> {
            Fornecedor selecionado = tabela.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                mostrarErro("Atenção", "Selecione um fornecedor na tabela clicando nele primeiro.");
                return;
            }

            // Confirmação de segurança
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Tem certeza que deseja excluir o fornecedor '" + selecionado.getNome() + "'?",
                    ButtonType.YES, ButtonType.NO);
            confirmacao.setHeaderText("Confirmação de Exclusão");

            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        fornecedorDAO.excluir(selecionado.getId());
                        listaFornecedores.remove(selecionado);
                        mostrarAlerta("Fornecedor excluído com sucesso!");
                    } catch (Exception ex) {
                        mostrarErro("Ação Bloqueada", "Não é possível excluir este fornecedor pois existem produtos vinculados a ele no sistema.");
                    }
                }
            });
        });

        HBox barraBotoes = new HBox(15, btnAtualizar, btnExcluir, new Region(), btnNovo);
        HBox.setHgrow(barraBotoes.getChildren().get(2), Priority.ALWAYS);
        barraBotoes.setAlignment(Pos.CENTER);

        tabela.setMinHeight(400);

        VBox layout = new VBox(20, lblTitulo, tabela, barraBotoes);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: transparent;");
        layout.setMinWidth(900);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("edge-to-edge");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #ffffff;");

        return scrollPane;
    }

    // =================================================================================
    // MODAL DE CADASTRO DE FORNECEDORES
    // =================================================================================
    private void abrirModalCadastroFornecedor(ObservableList<Fornecedor> listaAtual) {
        Stage stageModal = new Stage();
        stageModal.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Trava a tela de trás
        stageModal.setTitle("Novo Cadastro de Fornecedor");

        TextField txtCnpj = new TextField();
        TextField txtNome = new TextField();
        TextField txtContato = new TextField();

        aplicarMascaraCnpjCpf(txtCnpj);
        aplicarMascaraContato(txtContato);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.addRow(0, new Label("CNPJ/CPF:"), txtCnpj);
        form.addRow(1, new Label("Razão Social:"), txtNome);
        form.addRow(2, new Label("Contato (Tel/Email):"), txtContato);

        Button btnSalvar = new Button("Salvar Cadastro");
        btnSalvar.getStyleClass().addAll(Styles.SUCCESS);
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(btnSalvar, 2);
        form.addRow(4, btnSalvar);

        dao.FornecedorDAO fornecedorDAO = new dao.FornecedorDAO();

        btnSalvar.setOnAction(e -> {
            try {
                if (txtNome.getText().isEmpty() || txtCnpj.getText().isEmpty()) {
                    throw new IllegalArgumentException("Os campos 'Razão Social' e 'CNPJ/CPF' são obrigatórios.");
                }

                Fornecedor f = new Fornecedor();
                f.setNome(txtNome.getText());
                f.setCnpjCpf(txtCnpj.getText());
                f.setContato(txtContato.getText());

                fornecedorDAO.salvar(f);

                listaAtual.setAll(fornecedorDAO.listarTodos());

                mostrarAlerta("Fornecedor cadastrado com sucesso!");
                stageModal.close();

            } catch (IllegalArgumentException ex) {
                mostrarErro("Atenção", ex.getMessage());
            } catch (java.sql.SQLException ex) {
                if (ex.getMessage().contains("duplicate key")) {
                    mostrarErro("Erro", "Este CNPJ/CPF já está cadastrado no sistema.");
                } else {
                    mostrarErro("Erro", "Falha de banco: " + ex.getMessage());
                }
            }
        });

        VBox layout = new VBox(20, form);
        layout.setPadding(new Insets(30));

        Scene scene = new Scene(layout, 380, 250);
        stageModal.setScene(scene);
        stageModal.setResizable(false);
        stageModal.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // =================================================================================
    // NAVEGAÇÃO E SESSÃO
    // =================================================================================

    private void onLogout() {
        try {
            Sessao.logout();
            LoginFX login = new LoginFX();
            Stage stageLogin = new Stage();
            login.start(stageLogin);
            Stage stageAtual = (Stage) rootLayout.getScene().getWindow();
            stageAtual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =================================================================================
    // AJUSTES
    // =================================================================================

    // =================================================================================
    // REGRAS DE NEGÓCIO
    // =================================================================================
    private boolean isEstoqueBaixo(Produto p) {
        return p.getQntdDisp() <= 5;
    }

    // Regra para o que JÁ VENCEU (Data da validade ficou para trás)
    private boolean isVencido(Produto p) {
        java.util.Date hoje = new java.util.Date();
        if (p instanceof core.ProdutoPerecivel perecivel && perecivel.getDataValidade() != null) {
            return perecivel.getDataValidade().before(hoje);
        } else if (p instanceof core.Cosmetico cosmetico && cosmetico.getDataValidade() != null) {
            return cosmetico.getDataValidade().before(hoje);
        }
        return false;
    }

    // Regra para o que ESTÁ A VENCER (Ainda não venceu, mas tá no limite de 1 ou 3 meses)
    private boolean isVencendo(Produto p) {
        if (isVencido(p)) return false; // Se já venceu, não entra na conta do "a vencer"!

        java.util.Date hoje = new java.util.Date();
        java.util.Calendar cal = java.util.Calendar.getInstance();

        if (p instanceof core.ProdutoPerecivel perecivel && perecivel.getDataValidade() != null) {
            cal.setTime(hoje);
            cal.add(java.util.Calendar.MONTH, 1);
            return perecivel.getDataValidade().before(cal.getTime());
        } else if (p instanceof core.Cosmetico cosmetico && cosmetico.getDataValidade() != null) {
            cal.setTime(hoje);
            cal.add(java.util.Calendar.MONTH, 3);
            return cosmetico.getDataValidade().before(cal.getTime());
        }
        return false;
    }

    // =================================================================================
    // MODAL DE DETALHAMENTO DO MÊS (Visão de Vendas)
    // =================================================================================
    private void abrirModalDetalhesMes(String mesAno) {
        Stage stageModal = new Stage();
        stageModal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        stageModal.setTitle("Detalhamento de Vendas - " + mesAno);

        Label lblTitulo = new Label("Relatório de Vendas: " + mesAno);
        lblTitulo.getStyleClass().addAll(Styles.TITLE_2);

        // Tabela de Detalhes
        TableView<DetalheVenda> tabela = new TableView<>();
        tabela.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        TableColumn<DetalheVenda, String> colData = new TableColumn<>("Data/Hora");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataHora"));

        TableColumn<DetalheVenda, String> colProd = new TableColumn<>("Produto");
        colProd.setCellValueFactory(new PropertyValueFactory<>("produto"));

        TableColumn<DetalheVenda, Integer> colQtd = new TableColumn<>("Qtd");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("qtd"));
        colQtd.setMaxWidth(80);

        TableColumn<DetalheVenda, String> colSub = new TableColumn<>("Faturamento");
        colSub.setCellValueFactory(new PropertyValueFactory<>("subtotalFormatado"));

        TableColumn<DetalheVenda, String> colLucro = new TableColumn<>("Lucro Gerado");
        colLucro.setCellValueFactory(new PropertyValueFactory<>("lucroFormatado"));
        colLucro.setStyle("-fx-text-fill: #1a7f37; -fx-font-weight: bold;");

        tabela.getColumns().addAll(colData, colProd, colQtd, colSub, colLucro);

        // Busca e cruza os dados do Banco
        dao.MovimentacaoDAO daoMov = new dao.MovimentacaoDAO();
        dao.ProdutoDAO daoProd = new dao.ProdutoDAO();
        List<core.LogMovimentacao> historico = daoMov.listarHistorico();
        List<core.Produto> produtos = daoProd.listarTodos();

        List<DetalheVenda> listaDetalhes = new java.util.ArrayList<>();
        double totalFaturamento = 0;
        double totalLucro = 0;

        java.time.format.DateTimeFormatter fmtStr = java.time.format.DateTimeFormatter.ofPattern("MM/yyyy");

        for (core.LogMovimentacao log : historico) {
            // Filtra só o que foi VENDA e que não tem data vazia
            if (log.getDataHora() == null || !"VENDA".equals(log.getTipoMovimentacao())) continue;

            java.time.LocalDate dataLog = log.getDataHora().toLocalDateTime().toLocalDate();

            // Verifica se a venda pertence ao mês que o usuário clicou
            if (dataLog.format(fmtStr).equals(mesAno)) {
                core.Produto p = produtos.stream().filter(prod -> prod.getCodigo() == log.getCodigoProduto()).findFirst().orElse(null);
                if (p != null) {
                    double sub = p.getValorUnitVenda() * log.getQuantidade();
                    double luc = p.getLucroUnitarioCalculado() * log.getQuantidade();
                    totalFaturamento += sub;
                    totalLucro += luc;

                    listaDetalhes.add(new DetalheVenda(log.getDataHoraFormatada(), p.getDescricao(), log.getQuantidade(), sub, luc));
                }
            }
        }

        tabela.setItems(FXCollections.observableArrayList(listaDetalhes));

        // Rodapé com Totais Finais
        Label lblTotalFat = new Label("Total Faturado: " + formatarValorAbreviado(totalFaturamento));
        lblTotalFat.getStyleClass().addAll(Styles.TITLE_3);

        Label lblTotalLucro = new Label("  |  Lucro: " + formatarValorAbreviado(totalLucro));
        lblTotalLucro.getStyleClass().addAll(Styles.TITLE_3);
        lblTotalLucro.setStyle("-fx-text-fill: #1a7f37;"); // Deixa verdinho

        HBox rodape = new HBox(10, lblTotalFat, lblTotalLucro);
        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.setPadding(new Insets(10, 0, 0, 0));

        VBox layout = new VBox(15, lblTitulo, tabela, rodape);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 750, 500);
        stageModal.setScene(scene);
        stageModal.showAndWait();
    }

    public static String formatarValorAbreviado(double valor) {
        if (valor < 1_000) {
            return String.format("R$ %.0f", valor);
        } else if (valor < 1_000_000) {
            return String.format("R$ %.1f mil", valor / 1_000);
        } else {
            return String.format("R$ %.1f milhão", valor / 1_000_000);
        }
    }

    // =================================================================================
    // MÁSCARAS DE FORMATAÇÃO (TEMPO REAL)
    // =================================================================================

    private void aplicarMascaraCnpjCpf(TextField textField) {
        final boolean[] isUpdating = {false};

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating[0]) return;
            isUpdating[0] = true;

            String apenasNumeros = newValue.replaceAll("[^\\d]", "");

            if (apenasNumeros.length() > 14) {
                apenasNumeros = apenasNumeros.substring(0, 14);
            }

            StringBuilder sb = new StringBuilder(apenasNumeros);

            if (apenasNumeros.length() <= 11) {
                if (sb.length() > 9) sb.insert(9, "-");
                if (sb.length() > 6) sb.insert(6, ".");
                if (sb.length() > 3) sb.insert(3, ".");
            } else {
                if (sb.length() > 12) sb.insert(12, "-");
                if (sb.length() > 8) sb.insert(8, "/");
                if (sb.length() > 5) sb.insert(5, ".");
                if (sb.length() > 2) sb.insert(2, ".");
            }

            String mascara = sb.toString();

            // Colocamos TUDO (texto, cursor e destrava) na fila de execução do JavaFX
            javafx.application.Platform.runLater(() -> {
                textField.setText(mascara);
                textField.positionCaret(mascara.length());
                isUpdating[0] = false;
            });
        });
    }

    private void aplicarMascaraContato(TextField textField) {
        final boolean[] isUpdating = {false};

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating[0]) return;

            if (newValue.matches(".*[a-zA-Z@].*")) return;

            isUpdating[0] = true;

            String apenasNumeros = newValue.replaceAll("[^\\d]", "");

            if (apenasNumeros.length() > 11) {
                apenasNumeros = apenasNumeros.substring(0, 11);
            }

            StringBuilder sb = new StringBuilder(apenasNumeros);

            if (apenasNumeros.length() > 2) {
                sb.insert(2, ") ").insert(0, "(");
            }

            if (apenasNumeros.length() == 11) {
                if (sb.length() > 10) sb.insert(10, "-");
            } else if (apenasNumeros.length() > 6) {
                if (sb.length() > 9) sb.insert(9, "-");
            }

            String mascara = sb.toString();

            // Colocamos TUDO na fila de execução do JavaFX
            javafx.application.Platform.runLater(() -> {
                textField.setText(mascara);
                textField.positionCaret(mascara.length());
                isUpdating[0] = false;
            });
        });
    }

    private void aplicarMascaraData(TextField textField) {
        final boolean[] isUpdating = {false};

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating[0]) return;
            isUpdating[0] = true;

            // Remove tudo o que não for número
            String apenasNumeros = newValue.replaceAll("[^\\d]", "");

            // Trava: Limita a 8 dígitos (ddMMyyyy)
            if (apenasNumeros.length() > 8) {
                apenasNumeros = apenasNumeros.substring(0, 8);
            }

            StringBuilder sb = new StringBuilder(apenasNumeros);

            // Insere as barras nas posições corretas
            if (sb.length() > 4) {
                sb.insert(4, "/"); // Barra do ano
            }
            if (sb.length() > 2) {
                sb.insert(2, "/"); // Barra do mês
            }

            String mascara = sb.toString();

            javafx.application.Platform.runLater(() -> {
                textField.setText(mascara);
                textField.positionCaret(mascara.length());
                isUpdating[0] = false;
            });
        });
    }

    private void aplicarMascaraMoeda(TextField textField) {
        final boolean[] isUpdating = {false};

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isUpdating[0]) return;
            isUpdating[0] = true;

            // Deixa apenas os números digitados
            String apenasNumeros = newValue.replaceAll("[^\\d]", "");

            if (apenasNumeros.isEmpty()) {
                javafx.application.Platform.runLater(() -> {
                    textField.setText("");
                    isUpdating[0] = false;
                });
                return;
            }

            // Divide por 100 para criar os centavos matematicamente
            double valor = Double.parseDouble(apenasNumeros) / 100;

            java.text.NumberFormat formatoMoeda = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.of("pt", "BR"));
            String textoFormatado = formatoMoeda.format(valor);

            javafx.application.Platform.runLater(() -> {
                textField.setText(textoFormatado);
                textField.positionCaret(textoFormatado.length());
                isUpdating[0] = false;
            });
        });
    }

    // =================================================================================
    // MÉTODOS UTILITÁRIOS (ALERTAS)
    // =================================================================================

    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sistema GEST");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}