package com.projector.controller;

import com.projector.service.VlcjPlayerService;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Controller da janela do telão.
 */
public class TelaController {

    @FXML private StackPane rootPane;
    @FXML private StackPane mediaPane;
    @FXML private Pane overlayPane;

    private VlcjPlayerService playerService;

    @FXML
    public void initialize() {
        rootPane.setStyle("-fx-background-color: black;");
        playerService = new VlcjPlayerService();
    }

    /**
     * Vincula o player ao handle nativo da janela.
     * Deve ser chamado após a janela estar visível.
     */
    public void vincularJanela(Stage stage) {
        playerService.vincularJanela(stage);
    }

    /**
     * Reproduz a mídia a partir da URL.
     */
    public boolean reproduzir(String url) {
        return playerService.reproduzir(url);
    }

    /**
     * Para a reprodução.
     */
    public void parar() {
        playerService.parar();
    }

    /**
     * Libera recursos ao fechar.
     */
    public void dispose() {
        playerService.dispose();
    }

    public StackPane getMediaPane() {
        return mediaPane;
    }

    public Pane getOverlayPane() {
        return overlayPane;
    }

    public void limpar() {
        playerService.parar();
        overlayPane.getChildren().clear();
    }
}