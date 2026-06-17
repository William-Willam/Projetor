package com.projector.controller;

import com.projector.view.TelaStage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller da janela principal de controle.
 * Conecta os elementos do MainControl.fxml com a lógica da aplicação.
 */
public class MainController {

    // ═══════════════════════════════════
    // Elementos da interface (FXML)
    // ═══════════════════════════════════

    @FXML private Button btnPlay;
    @FXML private Button btnStop;
    @FXML private Button btnAdd;
    @FXML private Button btnRemove;
    @FXML private Button btnMoveUp;
    @FXML private Button btnMoveDown;

    @FXML private TextField txtUrl;
    @FXML private ListView<String> listPlaylist;
    @FXML private Label lblStatus;

    private TelaStage telaStage;

    // ═══════════════════════════════════
    // Inicialização
    // ═══════════════════════════════════

    /**
     * Chamado automaticamente pelo JavaFX após carregar o FXML.
     */
    @FXML
    public void initialize() {
        setStatus("Aguardando...");
    }

    // ═══════════════════════════════════
    // Ações dos botões
    // ═══════════════════════════════════

    @FXML
    private void onAddMedia() {
        String url = txtUrl.getText().trim();
        if (!url.isEmpty()) {
            listPlaylist.getItems().add(url);
            txtUrl.clear();
            setStatus("Mídia adicionada à fila.");
        }
    }

    @FXML
    private void onPlay() {
        String selected = listPlaylist.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Selecione uma mídia na fila.");
            return;
        }

        // Remove aspas se o usuário colou o caminho com aspas
        final String url = selected.replace("\"", "").trim();

        try {
            if (telaStage == null || !telaStage.estaAberto()) {
                telaStage = new TelaStage();
                telaStage.mostrar();

                // Aguarda janela abrir e vínculo ser feito antes de reproduzir
                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // 1 segundo para garantir o vínculo
                        javafx.application.Platform.runLater(() -> {
                            boolean ok = telaStage.getController().reproduzir(url);
                            setStatus(ok ? "Reproduzindo: " + url : "Erro ao reproduzir.");
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } else {
                boolean ok = telaStage.getController().reproduzir(url);
                setStatus(ok ? "Reproduzindo: " + url : "Erro ao reproduzir.");
            }

        } catch (Exception e) {
            setStatus("Erro: " + e.getMessage());
        }
    }
    @FXML
    private void onStop() {
        if (telaStage != null && telaStage.estaAberto()) {
            telaStage.getController().dispose();
            telaStage.fechar();
            telaStage = null;
        }
        setStatus("Reprodução encerrada.");
    }

    @FXML
    private void onRemove() {
        int index = listPlaylist.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            listPlaylist.getItems().remove(index);
            setStatus("Mídia removida da fila.");
        }
    }

    @FXML
    private void onMoveUp() {
        int index = listPlaylist.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            String item = listPlaylist.getItems().remove(index);
            listPlaylist.getItems().add(index - 1, item);
            listPlaylist.getSelectionModel().select(index - 1);
        }
    }

    @FXML
    private void onMoveDown() {
        int index = listPlaylist.getSelectionModel().getSelectedIndex();
        if (index < listPlaylist.getItems().size() - 1) {
            String item = listPlaylist.getItems().remove(index);
            listPlaylist.getItems().add(index + 1, item);
            listPlaylist.getSelectionModel().select(index + 1);
        }
    }

    @FXML
    private void onOpenFile() {
        // Abre o explorador de arquivos
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Selecionar Arquivo de Mídia");

        // Filtros de arquivo aceitos
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter(
                        "Arquivos de Vídeo",
                        "*.mp4", "*.avi", "*.mkv", "*.mov",
                        "*.wmv", "*.flv", "*.webm", "*.ts"
                ),
                new javafx.stage.FileChooser.ExtensionFilter(
                        "Todos os Arquivos", "*.*"
                )
        );

        // Abre na pasta Vídeos do usuário por padrão
        java.io.File pastaInicial = new java.io.File(
                System.getProperty("user.home") + "/Videos"
        );
        if (pastaInicial.exists()) {
            fileChooser.setInitialDirectory(pastaInicial);
        }

        // Obtém a janela principal para abrir o diálogo
        javafx.stage.Stage stage = (javafx.stage.Stage)
                btnAdd.getScene().getWindow();

        java.io.File arquivo = fileChooser.showOpenDialog(stage);

        if (arquivo != null) {
            listPlaylist.getItems().add(arquivo.getAbsolutePath());
            setStatus("Arquivo adicionado: " + arquivo.getName());
        }
    }

    // ═══════════════════════════════════
    // Utilitários
    // ═══════════════════════════════════

    private void setStatus(String message) {
        lblStatus.setText("● " + message);
    }
}