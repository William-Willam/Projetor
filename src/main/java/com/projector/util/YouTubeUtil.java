package com.projector.util;

/**
 * Utilitário para manipulação de URLs do YouTube.
 */
public class YouTubeUtil {

    /**
     * Extrai o ID do vídeo de diferentes formatos de URL do YouTube.
     *
     * Formatos suportados:
     * - https://www.youtube.com/watch?v=ABC123
     * - https://youtu.be/ABC123
     * - https://www.youtube.com/live/ABC123
     * - https://www.youtube.com/embed/ABC123
     */
    public static String extrairVideoId(String url) {
        if (url == null || url.isBlank()) return null;

        // Formato: youtu.be/ID
        if (url.contains("youtu.be/")) {
            String id = url.substring(url.indexOf("youtu.be/") + 9);
            return limpar(id);
        }

        // Formato: /live/ID
        if (url.contains("/live/")) {
            String id = url.substring(url.indexOf("/live/") + 6);
            return limpar(id);
        }

        // Formato: /embed/ID
        if (url.contains("/embed/")) {
            String id = url.substring(url.indexOf("/embed/") + 7);
            return limpar(id);
        }

        // Formato: ?v=ID ou &v=ID
        if (url.contains("v=")) {
            String id = url.substring(url.indexOf("v=") + 2);
            return limpar(id);
        }

        return null;
    }

    /**
     * Remove parâmetros extras após o ID (ex: &t=30s).
     */
    private static String limpar(String id) {
        if (id.contains("&")) id = id.substring(0, id.indexOf("&"));
        if (id.contains("?")) id = id.substring(0, id.indexOf("?"));
        if (id.contains("/")) id = id.substring(0, id.indexOf("/"));
        return id.trim();
    }

    /**
     * Monta a URL direta do YouTube (não embed).
     * Evita o Erro 153 do player de embed.
     */
    public static String montarEmbedUrl(String videoId, boolean isLive) {
        return "https://www.youtube.com/watch?v=" + videoId;
    }

    /**
     * Verifica se a URL é uma transmissão ao vivo.
     */
    public static boolean isLive(String url) {
        return url != null && (url.contains("/live/") || url.contains("live"));
    }
}