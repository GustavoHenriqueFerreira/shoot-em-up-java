package game.entities;

import game.GameLib;
import java.awt.Color;

public abstract class Chefe extends Inimigo {

    protected int pontosVida;

    public Chefe(double x, double y, double raio, double velocidade, double angulo, double velocidadeRotacao, int pontosVida) {
        super(x, y, raio, velocidade, angulo, velocidadeRotacao);
        this.pontosVida = pontosVida;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public void setPontosVida(int pontosVida) {
        this.pontosVida = pontosVida;
    }

    public void sofrerDano(int dano) {
        this.pontosVida -= dano;
        if (this.pontosVida <= 0) {
            this.explodir(System.currentTimeMillis());
        }
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (getEstado() == ATIVA) {
            // Movimento
            coordenadaX += velocidade * Math.cos(angulo) * delta;
            coordenadaY += velocidade * Math.sin(angulo) * delta * (-1.0);
            angulo += velocidadeRotacao * delta;

            // Chefes não saem da tela
            if (coordenadaX < 0) coordenadaX = 0;
            if (coordenadaX > GameLib.WIDTH) coordenadaX = GameLib.WIDTH;
            if (coordenadaY < 0) coordenadaY = 0;
            if (coordenadaY > GameLib.HEIGHT) coordenadaY = GameLib.HEIGHT;

            // Lógica de tiro específica do chefe será implementada nas subclasses

        } else if (getEstado() == EXPLODINDO) {
            if (explosaoFinalizada(tempoAtual)) {
                estado = INATIVA;
            }
        }
    }

    // Método para desenhar a barra de vida do chefe
    public void desenharBarraVida() {
        if (getEstado() == ATIVA) {
            GameLib.setColor(Color.RED);
            GameLib.fillRect(coordenadaX, coordenadaY - raio - 10, raio * 2 * (pontosVida / 100.0), 5); // Exemplo: vida máxima 100
            GameLib.setColor(Color.WHITE);
            GameLib.drawRect(coordenadaX, coordenadaY - raio - 10, raio * 2, 5);
        }
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            // Implementação de desenho específica em subclasses
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(coordenadaX, coordenadaY, alpha);
        }
    }
}


