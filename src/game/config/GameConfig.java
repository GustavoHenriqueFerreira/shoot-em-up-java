package game.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameConfig {
    private int playerLives;
    private List<String> phaseFiles;

    public GameConfig(String configFile) throws IOException {
        phaseFiles = new ArrayList<>();
        readConfigFile(configFile);
    }

    private void readConfigFile(String configFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            // Lê a linha de vidas do jogador
            line = br.readLine();
            if (line != null && line.startsWith("playerLives=")) {
                playerLives = Integer.parseInt(line.split("=")[1]);
            } else {
                throw new IOException("Formato inválido para playerLives no arquivo de configuração.");
            }

            // Lê o número de fases
            line = br.readLine();
            int numPhases = 0;
            if (line != null && line.startsWith("numPhases=")) {
                numPhases = Integer.parseInt(line.split("=")[1]);
            } else {
                throw new IOException("Formato inválido para numPhases no arquivo de configuração.");
            }

            // Lê os arquivos de fase
            for (int i = 0; i < numPhases; i++) {
                line = br.readLine();
                if (line != null) {
                    phaseFiles.add(line);
                } else {
                    throw new IOException("Número de arquivos de fase menor que o esperado.");
                }
            }
        }
    }

    public int getPlayerLives() {
        return playerLives;
    }

    public List<String> getPhaseFiles() {
        return phaseFiles;
    }
}


