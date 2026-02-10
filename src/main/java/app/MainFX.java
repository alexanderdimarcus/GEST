package app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import core.Estoque;

import static cli.Main.carregarDadosIniciais;

public class MainFX extends Application {

    private final Estoque meuEstoque = new Estoque();

    @Override
    public void start(Stage stage) {
        carregarDadosIniciais();

        Button botao = new Button("Listar produtos");
        botao.setOnAction(e -> {
            meuEstoque.imprimirListaProdutos();
        });

        VBox root = new VBox(botao);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("GEST - Sistema de Estoque");
        stage.setScene(scene);
        stage.show();
    }
}
