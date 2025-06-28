package game.entities;

import game.GameLib;
import java.awt.Color;

public class InimigoTipo2 extends Inimigo {

    private double spawnX;
    private int estadoMovimento; // 0: descendo, 1: movendo horizontalmente

    public InimigoTipo2(double x, double y, double raio, double velocidade, double angulo, double velocidadeRotacao, double spawnX, int estadoMovimento) {
        super(x, y, raio, velocidade, angulo, velocidadeRotacao);
        this.spawnX = spawnX;
        this.estadoMovimento = estadoMovimento;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (getEstado() == ATIVA) {
            if (estadoMovimento == 0) {
                // Movimento inicial de descida
                coordenadaY += velocidade * delta;
                if (coordenadaY >= GameLib.HEIGHT * 0.25) {
                    estadoMovimento = 1; // Mudar para movimento horizontal
                }
            } else if (estadoMovimento == 1) {
                // Movimento horizontal em zigue-zague
                coordenadaX += velocidade * Math.cos(angulo) * delta;
                angulo += velocidadeRotacao * delta;

                // Inverte a direção se atingir as bordas
                if (coordenadaX <= 0 || coordenadaX >= GameLib.WIDTH) {
                    velocidade *= -1; // Inverte a direção horizontal
                }
            }

            // Lógica de tiro
            if (tempoAtual > getProximoTiro() && coordenadaY < GameLib.HEIGHT * 0.90) {
                setProximoTiro((long) (tempoAtual + 700 + Math.random() * 300));
            }

            // Verificando se inimigo saiu da tela (após o movimento horizontal)
            if (coordenadaY > GameLib.HEIGHT + 10) {
                estado = INATIVA;
            }

        } else if (getEstado() == EXPLODINDO) {
            if (explosaoFinalizada(tempoAtual)) {
                estado = INATIVA;
            }
        }
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            GameLib.setColor(Color.MAGENTA);
            GameLib.drawDiamond(getCoordenadaX(), getCoordenadaY(), getRaio());
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


