package com.projector.service;

import com.projector.util.YouTubeUtil;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

/**
 * Serviço responsável pela reprodução de vídeos do YouTube no telão.
 * Utiliza o WebView do JavaFX como player embutido.
 */
public class PlayerService {

    private WebView webView;
    private WebEngine engine;
    private StackPane mediaPane;

    public PlayerService(StackPane mediaPane) {
        this.mediaPane = mediaPane;
        inicializar();
    }

    /**
     * Inicializa o WebView e o adiciona ao painel do telão.
     */
    private void inicializar() {
        webView = new WebView();
        engine = webView.getEngine();

        // Habilita JavaScript
        engine.setJavaScriptEnabled(true);

        // Define User Agent do Chrome
        engine.setUserAgent(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/120.0.0.0 Safari/537.36"
        );

        // Habilita cookies e armazenamento local
        java.net.CookieManager manager = new java.net.CookieManager();
        java.net.CookieHandler.setDefault(manager);

        // WebView ocupa todo o espaço disponível
        webView.prefWidthProperty().bind(mediaPane.widthProperty());
        webView.prefHeightProperty().bind(mediaPane.heightProperty());

        // Desativa menu de contexto
        webView.setContextMenuEnabled(false);

        mediaPane.getChildren().add(webView);
    }

    public boolean reproduzir(String url) {
        String videoId = YouTubeUtil.extrairVideoId(url);

        if (videoId == null) return false;

        String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            * { margin: 0; padding: 0; }
                            html, body {
                                width: 100%%;
                                height: 100%%;
                                background: black;
                                overflow: hidden;
                            }
                            iframe {
                                position: absolute;
                                top: 0; left: 0;
                                width: 100%%;
                                height: 100%%;
                                border: none;
                            }
                        </style>
                    </head>
                    <body>
                        <iframe
                            src="https://www.youtube-nocookie.com/embed/%s?autoplay=1&controls=1&rel=0"
                            allow="autoplay; fullscreen; encrypted-media"
                            allowfullscreen="true"
                            frameborder="0">
                        </iframe>
                    </body>
                    </html>
                """.formatted(videoId);

        engine.loadContent(html, "text/html");
        return true;
    }

    /**
     * Para a reprodução e limpa o WebView.
     */
    public void parar() {
        engine.load("about:blank");
    }

    public boolean estaReproduzindo() {
        return engine.getDocument() != null;
    }

}