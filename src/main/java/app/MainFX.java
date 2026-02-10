package app;

import core.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class MainFX extends Application {

    private final Estoque meuEstoque = new Estoque();
    private final dao.ProdutoDAO produtoDAO = new dao.ProdutoDAO();

    // A lista visual começa vazia e será preenchida quando o banco for conectado
    private ObservableList<Produto> listaObservavel;
    private TableView<Produto> tabelaProdutos;

    @Override
    public void start(Stage stage) {
        try {
            var loader = new javafx.fxml.FXMLLoader(getClass().getResource("/app/Login.fxml"));

            // Carrega a cena
            Scene scene = new Scene(loader.load());

            stage.setTitle("Login - GEST");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void IniciarSistemaPrincipal() {
        Stage stage = new Stage();

        // Busca a lista direto do banco de dados
        List<Produto> produtosDoBanco = produtoDAO.listarTodos();

        // Conecta a lista do JavaFX com os dados que vieram do banco
        listaObservavel = FXCollections.observableArrayList(produtosDoBanco);

        TabPane tabPane = new TabPane();

        Tab tab1 = new Tab("Visão Geral", criarTelaListagem());
        tab1.setClosable(false);

        Tab tab2 = new Tab("Novo Produto", criarTelaCadastro());
        tab2.setClosable(false);

        tabPane.getTabs().addAll(tab1, tab2);

        Scene scene = new Scene(tabPane, 900, 600);
        stage.setTitle("GEST - Sistema de Estoque (PostgreSQL Version)");
        stage.setScene(scene);
        stage.show();
    }

    // =================================================================================
    // TELA 1: LISTAGEM (Tabela)
    // =================================================================================
    private VBox criarTelaListagem() {
        Label lblTitulo = new Label("Produtos em Estoque");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        tabelaProdutos = new TableView<>();
        tabelaProdutos.setItems(listaObservavel);
        tabelaProdutos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Produto, Integer> colId = new TableColumn<>("Cód");
        colId.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Produto, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        TableColumn<Produto, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(dado -> {
            // PROTEÇÃO: Só tenta ler a classe se o produto não for nulo
            if (dado.getValue() != null) {
                return new SimpleStringProperty(dado.getValue().getClass().getSimpleName());
            } else {
                return new SimpleStringProperty(""); // Retorna vazio se for nulo
            }
        });

        TableColumn<Produto, Integer> colQtd = new TableColumn<>("Qtd.");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("qntdDisp"));

        TableColumn<Produto, Double> colValor = new TableColumn<>("Preço (R$)");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorUnitVenda"));

        tabelaProdutos.getColumns().addAll(colId, colDesc, colTipo, colQtd, colValor);

        Button btnAtualizar = new Button("Atualizar Lista (DB)");
        btnAtualizar.setOnAction(e -> {
            // -----------------------------------------------------------------------
            // TODO: INTEGRACAO BANCO DE DADOS (Refresh)
            // Aqui deve-se recarregar os dados do banco para garantir que a lista está atualizada
            // -----------------------------------------------------------------------
            tabelaProdutos.refresh();
        });

        Button btnExcluir = new Button("Excluir Selecionado");
        btnExcluir.setStyle("-fx-base: #ffcccc;");
        btnExcluir.setOnAction(e -> {
            Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                // Remove da memória (Visual)
                meuEstoque.excluirProduto(selecionado.getCodigo());
                listaObservavel.remove(selecionado);

                // -----------------------------------------------------------------------
                // TODO: INTEGRACAO BANCO DE DADOS (Delete)
                // Chamar método para executar o DELETE no PostgreSQL passando o ID (selecionado.getCodigo())
                // -----------------------------------------------------------------------
            } else {
                mostrarAlerta("Selecione um item na tabela para excluir.");
            }
        });

        HBox botoes = new HBox(10, btnAtualizar, btnExcluir);
        botoes.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(15, lblTitulo, tabelaProdutos, botoes);
        layout.setPadding(new Insets(20));
        return layout;
    }

    // =================================================================================
    // TELA 2: CADASTRO (Formulário)
    // =================================================================================
    private VBox criarTelaCadastro() {
        Label lblTitulo = new Label("Cadastrar Novo Produto");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextField txtCodigo = new TextField(); txtCodigo.setPromptText("Ex: 101");
        TextField txtDesc = new TextField(); txtDesc.setPromptText("Nome do Produto");
        TextField txtPreco = new TextField(); txtPreco.setPromptText("0.00");
        TextField txtQtd = new TextField(); txtQtd.setPromptText("Quantidade Inicial");

        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("Cosmético", "Eletrônico", "Perecível");
        cmbTipo.setValue("Cosmético");

        ComboBox<Fornecedor> cmbFornecedor = new ComboBox<>();
        dao.FornecedorDAO fornecedorDAO = new dao.FornecedorDAO();
        cmbFornecedor.setItems(FXCollections.observableArrayList(fornecedorDAO.listarTodos()));

        VBox painelExtras = new VBox(10);
        painelExtras.setStyle("-fx-border-color: #ddd; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        DatePicker dateValidade = new DatePicker();
        configurarDatePicker(dateValidade);
        TextField txtFabricante = new TextField(); txtFabricante.setPromptText("Fabricante");
        TextField txtGarantia = new TextField(); txtGarantia.setPromptText("Meses de Garantia");

        Runnable atualizarCampos = () -> {
            painelExtras.getChildren().clear();
            String tipo = cmbTipo.getValue();
            if (tipo.equals("Cosmético")) {
                painelExtras.getChildren().addAll(new Label("Validade:"), dateValidade, new Label("Fabricante:"), txtFabricante);
            } else if (tipo.equals("Eletrônico")) {
                painelExtras.getChildren().addAll(new Label("Garantia (Meses):"), txtGarantia);
            } else {
                painelExtras.getChildren().addAll(new Label("Validade:"), dateValidade);
            }
        };
        cmbTipo.setOnAction(e -> atualizarCampos.run());
        atualizarCampos.run();

        Button btnSalvar = new Button("Salvar no Banco");
        btnSalvar.setStyle("-fx-base: #b6e7c9; -fx-font-weight: bold;");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);

        btnSalvar.setOnAction(e -> {
            try {
                if (txtCodigo.getText().isEmpty() || txtDesc.getText().isEmpty() || txtPreco.getText().isEmpty() || txtQtd.getText().isEmpty()) {
                    throw new IllegalArgumentException("Por favor, preencha todos os campos obrigatórios.");
                }

                int cod = Integer.parseInt(txtCodigo.getText());
                String desc = txtDesc.getText();
                double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
                int qtd = Integer.parseInt(txtQtd.getText());
                Fornecedor f = cmbFornecedor.getValue();

                if (f == null) throw new IllegalArgumentException("Selecione um fornecedor!");

                Produto novo = null;
                String tipo = cmbTipo.getValue();

                if (tipo.equals("Cosmético")) {
                    if (dateValidade.getValue() == null || dateValidade.getValue().isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException("A data de validade não pode ser anterior a hoje!");
                    }
                    Date validade = converterData(dateValidade.getValue());
                    novo = new Cosmetico(cod, desc, "Geral", qtd, preco, 30.0, f, validade, txtFabricante.getText());
                }
                else if (tipo.equals("Eletrônico")) {
                    if (txtGarantia.getText().isEmpty()) throw new IllegalArgumentException("Digite a garantia.");
                    int garantia = Integer.parseInt(txtGarantia.getText());
                    novo = new Eletronico(cod, desc, "Tech", qtd, preco, 50.0, f, garantia);
                }
                else {
                    if (dateValidade.getValue() == null || dateValidade.getValue().isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException("A data de validade não pode ser anterior a hoje!");
                    }
                    Date validade = converterData(dateValidade.getValue());
                    novo = new ProdutoPerecivel(cod, desc, "Alimento", qtd, preco, 15.0, f, validade);
                }

                produtoDAO.salvar(novo);

                listaObservavel.setAll(produtoDAO.listarTodos());
                mostrarAlerta("SUCESSO: Produto cadastrado corretamente!");

                txtCodigo.clear(); txtDesc.clear(); txtPreco.clear(); txtQtd.clear();

            } catch (NumberFormatException ex) {
                mostrarErro("Erro de Formato", "Verifique se digitou apenas números nos campos de Preço, Quantidade e Código.");
            } catch (IllegalArgumentException ex) {
                mostrarErro("Dados Inválidos", ex.getMessage());
            } catch (java.sql.SQLException ex) {
                if (ex.getMessage().contains("duplicate key") || ex.getMessage().contains("produtos_pkey")) {
                    mostrarErro("Código Duplicado", "O código " + txtCodigo.getText() + " já existe no sistema.\nPor favor, escolha outro.");
                } else {
                    mostrarErro("Erro de Banco de Dados", "Falha técnica: " + ex.getMessage());
                }
            } catch (Exception ex) {
                mostrarErro("Erro Inesperado", ex.getMessage());
                ex.printStackTrace();
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.addRow(0, new Label("Tipo:"), cmbTipo);
        form.addRow(1, new Label("Fornecedor:"), cmbFornecedor);
        form.addRow(2, new Label("Código:"), txtCodigo);
        form.addRow(3, new Label("Descrição:"), txtDesc);
        form.addRow(4, new Label("Preço Venda:"), txtPreco);
        form.addRow(5, new Label("Qtd Inicial:"), txtQtd);

        VBox layout = new VBox(15, lblTitulo, form, new Label("Detalhes Específicos:"), painelExtras, btnSalvar);
        layout.setPadding(new Insets(20));
        return layout;
    }

    private void configurarDatePicker(DatePicker datePicker) {
        String pattern = "dd/MM/yyyy";
        datePicker.setPromptText("dd/mm/aaaa");

        datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                       if (string.matches("\\d{8}")) {
                            string = string.substring(0, 2) + "/" + string.substring(2, 4) + "/" + string.substring(4);
                        }
                        return LocalDate.parse(string, dateFormatter);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        });

        // Garante que o texto digitado seja processado ao sair do campo
        datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
            }
        });
    }

    private Date converterData(LocalDate localDate) {
        if (localDate == null) return new Date();
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sistema GEST");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarErro(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Ícone Vermelho de Erro
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}