package game.entities;

import java.awt.Color;
import game.GameLib;

public class Background {

    private double[] estrelas1X;
    private double[] estrelas1Y;
    private double velocidade1;
    private double contador1;

    private double[] estrelas2X;
    private double[] estrelas2Y;
    private double velocidade2;
    private double contador2;

    public Background() {
        // Estrelas do fundo distante
        estrelas1X = new double[50];
        estrelas1Y = new double[50];
        velocidade1 = 0.045;
        contador1 = 0.0;

        // Estrelas do fundo próximo
        estrelas2X = new double[20];
        estrelas2Y = new double[20];
        velocidade2 = 0.070;
        contador2 = 0.0;

        // Inicializar posições aleatórias
        for (int i = 0; i < estrelas1X.length; i++) {
            estrelas1X[i] = Math.random() * GameLib.WIDTH;
            estrelas1Y[i] = Math.random() * GameLib.HEIGHT;
        }

        for (int i = 0; i < estrelas2X.length; i++) {
            estrelas2X[i] = Math.random() * GameLib.WIDTH;
            estrelas2Y[i] = Math.random() * GameLib.HEIGHT;
        }
    }

    public void atualizar(long delta) {
        contador1 += velocidade1 * delta;
        contador2 += velocidade2 * delta;
    }

    public void desenhar() {
        // Desenhar fundo distante
        GameLib.setColor(Color.DARK_GRAY);
        for (int i = 0; i < estrelas1X.length; i++) {
            GameLib.fillRect(estrelas1X[i], (estrelas1Y[i] + contador1) % GameLib.HEIGHT, 2, 2);
        }

        // Desenhar fundo próximo
        GameLib.setColor(Color.GRAY);
        for (int i = 0; i < estrelas2X.length; i++) {
            GameLib.fillRect(estrelas2X[i], (estrelas2Y[i] + contador2) % GameLib.HEIGHT, 3, 3);
        }
    }
}