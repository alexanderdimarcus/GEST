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
    private ProdutoDAO produtoDAO = new ProdutoDAO();

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

    // Ajuste sutil na cor de fundo para destacar mais a logo
        sidebar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d7de; -fx-border-width: 0 1 0 0;");

    // 1. LOGO REFINADA
        ImageView logoView = new ImageView();
        try {
            // Tenta carregar. Se falhar, não quebra o app.
            java.io.InputStream is = getClass().getResourceAsStream("/logoG.png");
            if (is == null) is = getClass().getResourceAsStream("/images/logoG.png"); // Tenta subpasta

            if (is != null) {
                logoView.setImage(new Image(is));
                // Aumentei um pouco para preencher melhor
                logoView.setFitWidth(160);
                logoView.setPreserveRatio(true);
                // TRUQUE DE QUALIDADE: Suaviza a imagem
                logoView.setSmooth(true);
                logoView.setCache(true);
            }
        } catch (Exception e) {
            /* Ignora erro silenciosamente ou usa System.out */
        }

    // Container para centralizar a logo se quiser
        HBox logoContainer = new HBox(logoView);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        logoContainer.setPadding(new Insets(0, 0, 10, 0));

    // 2. INFORMAÇÕES DO USUÁRIO
        Label lblUser = new Label(Sessao.getUsuario().getNomeCompleto()); // Usa Nome Completo é mais formal
        lblUser.getStyleClass().addAll(Styles.TITLE_4);
        lblUser.setWrapText(true); // Quebra linha se o nome for longo

    // Pega o cargo do usuário
        String cargoTexto = Sessao.getUsuario().getCargo() != null ? Sessao.getUsuario().getCargo() : "Colaborador";
        Label lblCargo = new Label(cargoTexto.toUpperCase());
        lblCargo.getStyleClass().addAll(Styles.TEXT_SMALL, Styles.TEXT_MUTED);

    // Cria um "Avatar" simples
        Label lblAvatar = new Label(Sessao.getUsuario().getLogin().substring(0, 1).toUpperCase());
        lblAvatar.setStyle("-fx-background-color: #0969da; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 35; -fx-min-height: 35; -fx-alignment: center;");

        VBox userDetails = new VBox(2, lblUser, lblCargo);
        HBox userBox = new HBox(10, lblAvatar, userDetails);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(10, 0, 10, 0));

        // 3. BOTÃO DE SAIR
        Button btnLogout = new Button("Sair");
        btnLogout.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.SMALL); // Botão menor
        btnLogout.setPrefWidth(100);
        btnLogout.setOnAction(e -> onLogout());

        // Montagem do Header
        VBox header = new VBox(15, logoContainer, userBox, btnLogout);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        // --- Navegação
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

        // --- STATUS DA CONEXÃO ---
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label lblStatusBD = new Label("● Conectado: PostgreSQL");
        lblStatusBD.getStyleClass().add(Styles.TEXT_SMALL);
        lblStatusBD.setPadding(new Insets(0, 0, 10, 20)); // Margem inferior e esquerda

        // Lógica simples de verificação (Visual)
        try (Connection conn = ConexaoBD.conectar()) {
            if (conn != null && !conn.isClosed()) {
                lblStatusBD.setStyle("-fx-text-fill: green; -fx-font-size: 11px;");
            }
        } catch (Exception e) {
            lblStatusBD.setText("● Offline / Erro BD");
            lblStatusBD.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        }

        Label lblVersao = new Label("v1.0.0 (Sprint 2)");
        lblVersao.getStyleClass().addAll(Styles.TEXT_MUTED, Styles.TEXT_SMALL);
        lblVersao.setPadding(new Insets(0, 0, 20, 20));

        // Adiciona tudo na sidebar
        sidebar.getChildren().addAll(header, botoesLayout, spacer, lblStatusBD, lblVersao);
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

        // 1. Buscando dados reais
        List<Produto> listaProdutos = produtoDAO.listarTodos();

        // Cálculo: Total de Itens
        int totalProdutos = listaProdutos.size();

        // Cálculo: Estoque Crítico
        long estoqueCritico = listaProdutos.stream()
                .filter(p -> p.getQntdDisp() < 10)
                .count();

        // Cálculo: Valor total em estoque
        double valorEmEstoque = listaProdutos.stream().mapToDouble(p -> p.getQntdDisp() * p.getValorUnitVenda()).sum();

        HBox cards = new HBox(20);
        cards.getChildren().addAll(
                criarCard("Catálogo Ativo", String.valueOf(totalProdutos), "Produtos cadastrados"),
                criarCard("Estoque Crítico", String.valueOf(estoqueCritico), "Abaixo de 10 un."),
                criarCard("Valor em Estoque", formatarValorAbreviado(valorEmEstoque), "Preço de venda")
        );

        Label lblRecentes = new Label("Últimas Movimentações");
        lblRecentes.getStyleClass().addAll(Styles.TITLE_3);
        VBox.setMargin(lblRecentes, new Insets(20, 0, 0, 0));

        TableView<String> tabRecentes = new TableView<>();
        tabRecentes.getStyleClass().add(Styles.STRIPED);
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

        HBox barraFerramentas = new HBox(10, txtBusca, cmbOrdenacao, btnAtualizar);
        barraFerramentas.setAlignment(Pos.CENTER_LEFT);

        // 3. TABELA (SortedList)
        TableView<Produto> tabela = new TableView<>();
        tabela.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

        TableColumn<Produto, Integer> colQtd = new TableColumn<>("Estoque");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("qntdDisp"));
        colQtd.setMaxWidth(100);
        // Destaca em vermelho se estoque baixo
        colQtd.setCellFactory(column -> new TableCell<Produto, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item < 5) {
                        setStyle("-fx-text-fill: #cf222e; -fx-font-weight: bold;"); // Vermelho
                    } else {
                        setStyle("-fx-text-fill: #1a7f37;"); // Verde
                    }
                }
            }
        });

        TableColumn<Produto, String> colValor = new TableColumn<>("Valor (R$)");
        colValor.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("R$ %.2f", cellData.getValue().getValorUnitVenda()))
        );
        colValor.setMaxWidth(120);

        tabela.getColumns().addAll(colId, colDesc, colCat, colOrigem, colQtd, colValor);

        // 4. LÓGICA DE FILTRAGEM (Ao digitar)
        txtBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(produto -> {
                // Se o filtro estiver vazio, mostra tudo
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Regras de busca:
                if (String.valueOf(produto.getCodigo()).contains(lowerCaseFilter)) return true;
                if (produto.getDescricao().toLowerCase().contains(lowerCaseFilter)) return true;
                if (produto.getCategoria().toLowerCase().contains(lowerCaseFilter)) return true;

                // Busca extra: Verifica fabricante se for cosmético
                if (produto instanceof Cosmetico) {
                    if (((Cosmetico) produto).getFabricante().toLowerCase().contains(lowerCaseFilter)) return true;
                }

                return false; // Não encontrou nada
            });
        });

        // 5. LÓGICA DE ORDENAÇÃO (ComboBox + Tabela)
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
        // Atualiza o contador sempre que a lista mudar (filtrar ou carregar)
        filteredData.predicateProperty().addListener(o ->
                lblTotal.setText("Exibindo " + filteredData.size() + " registros")
        );
        lblTotal.setText("Exibindo " + masterData.size() + " registros"); // Valor inicial

        VBox layout = new VBox(15, lblTitulo, barraFerramentas, tabela, lblTotal);
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
        TextField txtCod = new TextField();
        txtCod.setPromptText("Cód. Produto");
        TextField txtQtd = new TextField();
        txtQtd.setPromptText("Qtd");

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
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20, 0, 0, 0));

        TextField txtCod = new TextField();
        TextField txtQtd = new TextField();
        TextField txtDestino = new TextField();
        txtDestino.setPromptText("Ex: Venda #123");

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
        form.setHgap(10);
        form.setVgap(15);
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
        VBox vbox = new VBox(lbl);
        vbox.setAlignment(Pos.CENTER);
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

    public static String formatarValorAbreviado(double valor) {
        if (valor < 1_000) {
            return String.format("R$ %.0f", valor);
        } else if (valor < 1_000_000) {
            return String.format("R$ %.1f mil", valor / 1_000);
        } else {
            return String.format("R$ %.1f milhão", valor / 1_000_000);
        }
    }
}