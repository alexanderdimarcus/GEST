package app;

import dao.UsuarioDAO;
import core.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtSenha;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    public void initialize() {
        // Comportamento 1: Ao dar ENTER no Login, pula para a Senha
        txtLogin.setOnAction(event -> txtSenha.requestFocus());

        // Comportamento 2: Ao dar ENTER na Senha, clica no botão Entrar
        txtSenha.setOnAction(event -> onBtnEntrarClick());

        Platform.runLater(() -> txtLogin.requestFocus());
    }

    @FXML
    public void onBtnEntrarClick() {
        String login = txtLogin.getText();
        String senha = txtSenha.getText();

        if (login.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Por favor, preencha todos os campos.");
            return;
        }

        Usuario usuarioLogado = usuarioDAO.autenticar(login, senha);

        if (usuarioLogado != null) {
            System.out.println("Login realizado com sucesso: " + usuarioLogado.getNomeCompleto());
            abrirTelaPrincipal();
        } else {
            mostrarAlerta("Login ou Senha incorretos!");
        }
    }

    private void abrirTelaPrincipal() {
        try {
            Stage stageLogin = (Stage) txtLogin.getScene().getWindow();
            stageLogin.close();

            MainFX sistemaPrincipal = new MainFX();
            sistemaPrincipal.IniciarSistemaPrincipal();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro crítico ao abrir o sistema: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}