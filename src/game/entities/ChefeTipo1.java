package game.entities;

import game.GameLib;
import java.awt.Color;

public class ChefeTipo1 extends Chefe {

    public ChefeTipo1(double x, double y, double raio, double velocidade, double angulo, double velocidadeRotacao, int pontosVida) {
        super(x, y, raio, velocidade, angulo, velocidadeRotacao, pontosVida);
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        super.atualizar(delta, tempoAtual);

        if (getEstado() == ATIVA) {
            // Lógica de movimento específica do Chefe Tipo 1
            // Exemplo: move-se para a esquerda e direita na parte superior da tela
            if (getCoordenadaX() <= 0 || getCoordenadaX() >= GameLib.WIDTH) {
                velocidade *= -1; // Inverte a direção horizontal
            }

            // Lógica de tiro específica do Chefe Tipo 1
            if (tempoAtual > getProximoTiro()) {
                // Adicionar projéteis (exemplo: tiro único para baixo)
                // Isso será tratado na classe Main ou em um gerenciador de projéteis
                setProximoTiro((long) (tempoAtual + 1000 + Math.random() * 500));
            }
        }
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            GameLib.setColor(Color.ORANGE);
            GameLib.drawDiamond(getCoordenadaX(), getCoordenadaY(), getRaio() * 2); // Chefe maior
            desenharBarraVida();
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


