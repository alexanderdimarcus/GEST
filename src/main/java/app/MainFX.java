package app;

import core.*;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainFX extends Application {

    private BorderPane rootLayout;

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
        stage.show();
    }

    // =================================================================================
    // MENU LATERAL (AtlantaFX Styled)
    // =================================================================================
    private VBox criarMenuLateral() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(260);
        // Usando uma cor levemente destacada do tema para a sidebar
        sidebar.setStyle("-fx-background-color: #f6f8fa; -fx-border-color: #d0d7de; -fx-border-width: 0 1 0 0;");

        // --- Cabeçalho ---
        Label lblLogo = new Label("GEST");
        lblLogo.getStyleClass().addAll(Styles.TITLE_1);
        lblLogo.setStyle("-fx-font-style: italic; -fx-text-fill: #0969da;"); // Azul primer

        Label lblSub = new Label("Gestão de Estoque");
        lblSub.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TITLE_4);

        Label lblUser = new Label("joao@admin");
        lblUser.getStyleClass().addAll(Styles.ACCENT);
        lblUser.setPadding(new Insets(10, 0, 5, 0));

        Button btnLogout = new Button("Sair do Sistema");
        btnLogout.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> onLogout());

        VBox header = new VBox(5, lblLogo, lblSub, lblUser, btnLogout);
        header.setPadding(new Insets(30, 20, 30, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        // --- Navegação ---
        Button btnDash = criarBotaoMenu("Dashboard");
        Button btnConsulta = criarBotaoMenu("Consulta de Estoque");
        Button btnControle = criarBotaoMenu("Movimentação");
        Button btnFornecedores = criarBotaoMenu("Fornecedores");
        Button btnRelatorios = criarBotaoMenu("Relatórios & Logs");

        btnDash.setOnAction(e -> rootLayout.setCenter(criarDashboard()));
        btnConsulta.setOnAction(e -> rootLayout.setCenter(criarTelaConsulta()));
        btnControle.setOnAction(e -> rootLayout.setCenter(criarTelaControle()));
        btnFornecedores.setOnAction(e -> rootLayout.setCenter(criarTelaFornecedores()));
        btnRelatorios.setOnAction(e -> rootLayout.setCenter(criarTelaTemporaria("Relatórios (H11)")));

        VBox botoesLayout = new VBox(2, btnDash, btnConsulta, btnControle, btnFornecedores, btnRelatorios);
        VBox.setVgrow(botoesLayout, Priority.ALWAYS);

        sidebar.getChildren().addAll(header, botoesLayout);
        return sidebar;
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
    // TELA 0: DASHBOARD (H13)
    // =================================================================================
    private VBox criarDashboard() {
        Label lblTitulo = new Label("Visão Geral");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        HBox cards = new HBox(20);
        cards.getChildren().addAll(
                criarCard("Catálogo Ativo", "", "Produtos cadastrados"),
                criarCard("Estoque Crítico", "", "Abaixo do mínimo ideal"),
                criarCard("Saídas Hoje", "", "Volume de movimentação")
        );

        Label lblRecentes = new Label("Últimas Movimentações");
        lblRecentes.getStyleClass().addAll(Styles.TITLE_3);
        VBox.setMargin(lblRecentes, new Insets(20, 0, 0, 0));

        TableView<String> tabRecentes = new TableView<>();
        tabRecentes.getStyleClass().add(Styles.STRIPED); // Tabela listrada
        tabRecentes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabRecentes.getColumns().addAll(
                new TableColumn<>("Ação"), new TableColumn<>("Produto"),
                new TableColumn<>("Qtd"), new TableColumn<>("Data/Hora")
        );
        VBox.setVgrow(tabRecentes, Priority.ALWAYS);

        VBox layout = new VBox(20, lblTitulo, cards, lblRecentes, tabRecentes);
        layout.setPadding(new Insets(40));
        return layout;
    }

    private VBox criarCard(String titulo, String valor, String subtitulo) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TITLE_4);

        Label lblValor = new Label(valor);
        lblValor.getStyleClass().addAll(Styles.TITLE_1);
        lblValor.setStyle("-fx-font-size: 36px;"); // Sobrescreve apenas o tamanho

        Label lblSub = new Label(subtitulo);
        lblSub.getStyleClass().addAll(Styles.TEXT_SMALL);

        VBox card = new VBox(5, lblTitulo, lblValor, lblSub);
        card.setPadding(new Insets(20));
        card.setPrefSize(280, 120);
        // Aplica a classe de elevação do AtlantaFX (Sombra suave)
        card.getStyleClass().addAll("card", Styles.ELEVATED_1);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 6px;");
        return card;
    }

    // =================================================================================
    // TELA 1: CONSULTA DE ESTOQUE (Busca Avançada)
    // =================================================================================
    private VBox criarTelaConsulta() {
        Label lblTitulo = new Label("Consulta de Estoque");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        // --- BARRA DE BUSCA AVANÇADA ---
        TextField txtBusca = new TextField();
        txtBusca.setPromptText("Buscar por código, nome, marca...");
        txtBusca.setPrefWidth(300);

        ComboBox<String> cmbCategoria = new ComboBox<>();
        cmbCategoria.getItems().addAll("Todas Categorias", "Eletrônicos", "Cosméticos", "Perecíveis");
        cmbCategoria.setValue("Todas Categorias");

        ComboBox<String> cmbStatus = new ComboBox<>();
        cmbStatus.getItems().addAll("Qualquer Status", "Estoque Normal", "Estoque Crítico (H12)", "Vencimento Próximo");
        cmbStatus.setValue("Qualquer Status");

        Button btnBuscar = new Button("Filtrar");
        btnBuscar.getStyleClass().addAll(Styles.ACCENT); // Botão Azul de ação primária

        Button btnLimpar = new Button("Limpar");
        btnLimpar.getStyleClass().addAll(Styles.BUTTON_OUTLINED);

        HBox filtros = new HBox(10, txtBusca, cmbCategoria, cmbStatus, btnBuscar, btnLimpar);
        filtros.setAlignment(Pos.CENTER_LEFT);

        // --- FERRAMENTAS EXTRAS ---
        Button btnDetalhes = new Button("Visualizar Ficha Técnica");
        Button btnExportar = new Button("Exportar (.csv)");
        btnExportar.getStyleClass().addAll(Styles.SUCCESS, Styles.BUTTON_OUTLINED); // Verde vazado

        HBox acoesTabela = new HBox(10, btnDetalhes, new Region(), btnExportar);
        HBox.setHgrow(acoesTabela.getChildren().get(1), Priority.ALWAYS); // Espaçador

        // --- TABELA ---
        TableView<Produto> tabela = new TableView<>();
        tabela.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.getColumns().addAll(
                new TableColumn<>("Cód"), new TableColumn<>("Descrição"),
                new TableColumn<>("Categoria"), new TableColumn<>("Estoque"),
                new TableColumn<>("Status")
        );
        VBox.setVgrow(tabela, Priority.ALWAYS);

        VBox layout = new VBox(20, lblTitulo, filtros, tabela, acoesTabela);
        layout.setPadding(new Insets(40));
        return layout;
    }

    // =================================================================================
    // TELA 2: CONTROLE DE ESTOQUE
    // =================================================================================
    private VBox criarTelaControle() {
        Label lblTitulo = new Label("Movimentação de Estoque");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        TabPane tabPane = new TabPane();
        // AtlantaFX deixa as abas nativamente bonitas
        tabPane.getStyleClass().addAll(Styles.DENSE);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab tabEntrada = new Tab("Entrada em Lote", criarFormularioEntradaLote());
        Tab tabSaida = new Tab("Saída / Venda", criarFormularioSaida());

        tabPane.getTabs().addAll(tabEntrada, tabSaida);

        VBox layout = new VBox(20, lblTitulo, tabPane);
        layout.setPadding(new Insets(40));
        return layout;
    }

    private VBox criarFormularioEntradaLote() {
        TextField txtCod = new TextField(); txtCod.setPromptText("Cód. Produto");
        TextField txtQtd = new TextField(); txtQtd.setPromptText("Qtd");

        Button btnAdd = new Button("Adicionar");
        btnAdd.getStyleClass().addAll(Styles.ACCENT);

        Button btnNovo = new Button("Cadastrar Novo Produto");
        btnNovo.getStyleClass().addAll(Styles.WARNING, Styles.BUTTON_OUTLINED); // Amarelo para chamar atenção sutil

        HBox formAdd = new HBox(10, txtCod, txtQtd, btnAdd, new Region(), btnNovo);
        HBox.setHgrow(formAdd.getChildren().get(3), Priority.ALWAYS);

        TableView<Produto> tabelaLote = new TableView<>();
        tabelaLote.getStyleClass().add(Styles.BORDERED);
        tabelaLote.getColumns().addAll(new TableColumn<>("Produto"), new TableColumn<>("Qtd"));
        VBox.setVgrow(tabelaLote, Priority.ALWAYS);

        Button btnSalvarLote = new Button("Processar Lote de Entrada");
        btnSalvarLote.getStyleClass().addAll(Styles.SUCCESS, Styles.LARGE); // Botão grande e verde
        btnSalvarLote.setMaxWidth(Double.MAX_VALUE);

        VBox layout = new VBox(15, new Label("Bipe ou digite os itens recebidos:"), formAdd, tabelaLote, btnSalvarLote);
        layout.setPadding(new Insets(20, 0, 0, 0));
        return layout;
    }

    private VBox criarFormularioSaida() {
        GridPane form = new GridPane();
        form.setHgap(15); form.setVgap(15); form.setPadding(new Insets(20, 0, 0, 0));

        TextField txtCod = new TextField();
        TextField txtQtd = new TextField();
        TextField txtDestino = new TextField(); txtDestino.setPromptText("Ex: Venda #123");

        Button btnSalvar = new Button("Confirmar Saída");
        btnSalvar.getStyleClass().addAll(Styles.DANGER, Styles.LARGE); // Vermelho/Laranja para saídas

        form.addRow(0, new Label("Cód. Produto:"), txtCod);
        form.addRow(1, new Label("Quantidade:"), txtQtd);
        form.addRow(2, new Label("Documento:"), txtDestino);
        form.addRow(4, new Label(""), btnSalvar);

        return new VBox(form);
    }

    // =================================================================================
    // TELA 3: FORNECEDORES
    // =================================================================================
    private VBox criarTelaFornecedores() {
        Label lblTitulo = new Label("Fornecedores");
        lblTitulo.getStyleClass().addAll(Styles.TITLE_1);

        SplitPane split = new SplitPane();
        VBox.setVgrow(split, Priority.ALWAYS);

        // Formulário
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(15);
        form.addRow(0, new Label("CNPJ:"), new TextField());
        form.addRow(1, new Label("Razão Social:"), new TextField());
        form.addRow(2, new Label("E-mail:"), new TextField());
        form.addRow(3, new Label("Telefone:"), new TextField());

        Button btnSalvar = new Button("Salvar Cadastro");
        btnSalvar.getStyleClass().addAll(Styles.SUCCESS);
        form.addRow(5, new Label(""), btnSalvar);

        VBox painelEsq = new VBox(20, new Label("Novo Cadastro"), form);
        painelEsq.setPadding(new Insets(20));

        // Tabela
        TableView<Fornecedor> tabela = new TableView<>();
        tabela.getStyleClass().add(Styles.STRIPED);
        tabela.getColumns().addAll(new TableColumn<>("Fornecedor"), new TableColumn<>("Contato"));
        VBox painelDir = new VBox(10, new Label("Contatos Salvos"), tabela);
        painelDir.setPadding(new Insets(20));

        split.getItems().addAll(painelEsq, painelDir);
        split.setDividerPositions(0.35);

        VBox layout = new VBox(20, lblTitulo, split);
        layout.setPadding(new Insets(40));
        return layout;
    }

    private VBox criarTelaTemporaria(String titulo) {
        Label lbl = new Label(titulo + "\n(Em Desenvolvimento)");
        lbl.getStyleClass().addAll(Styles.TITLE_2, Styles.TEXT_MUTED);
        lbl.setStyle("-fx-text-alignment: center;");
        VBox vbox = new VBox(lbl); vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // =================================================================================
    // NAVEGAÇÃO E SESSÃO
    // =================================================================================
    private void onLogout() {
        try {
            // Abre a tela de Login novamente
            LoginFX login = new LoginFX();
            Stage stageLogin = new Stage();
            login.start(stageLogin);

            // Pega a referência da janela atual (MainFX) através do rootLayout e a fecha
            Stage stageAtual = (Stage) rootLayout.getScene().getWindow();
            stageAtual.close();

        } catch (Exception e) {
            e.printStackTrace();
            // Aqui você pode colocar um Alerta de erro caso o JavaFX falhe ao abrir a tela
        }
    }

}