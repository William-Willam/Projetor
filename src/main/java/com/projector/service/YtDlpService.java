package com.projector.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Serviço que usa o yt-dlp para extrair a URL real do stream do YouTube.
 */
public class YtDlpService {

    // Caminho do yt-dlp
    private static final String YT_DLP_PATH = "D:\\Program Files\\VLC\\yt-dlp.exe";

    /**
     * Extrai a URL do melhor stream disponível para o vídeo/live.
     * Retorna null se falhar.
     */
    public String extrairStreamUrl(String youtubeUrl) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    YT_DLP_PATH,
                    "-f", "best[ext=mp4]/best",  // Melhor qualidade mp4
                    "-g",                          // Só retorna a URL, não baixa
                    youtubeUrl
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("[yt-dlp] " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("[yt-dlp] Exit code: " + exitCode);

            if (exitCode == 0) {
                // Retorna a primeira URL válida
                String result = output.toString().trim();
                String[] lines = result.split("\n");
                for (String l : lines) {
                    if (l.startsWith("http")) {
                        return l.trim();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao executar yt-dlp: " + e.getMessage());
        }

        return null;
    }

    /**
     * Verifica se a URL é do YouTube.
     */
    public boolean isYouTube(String url) {
        return url != null && (
                url.contains("youtube.com") ||
                        url.contains("youtu.be")
        );
    }
}