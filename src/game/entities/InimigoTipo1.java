package game.entities;

import game.GameLib;
import java.awt.Color;

public class InimigoTipo1 extends Inimigo {

    public InimigoTipo1(double x, double y, double raio, double velocidade, double angulo, double velocidadeRotacao) {
        super(x, y, raio, velocidade, angulo, velocidadeRotacao);
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (getEstado() == ATIVA) {
            // Movimento
            coordenadaX += velocidade * Math.cos(angulo) * delta;
            coordenadaY += velocidade * Math.sin(angulo) * delta * (-1.0);
            angulo += velocidadeRotacao * delta;

            // Verificando se inimigo saiu da tela
            if (coordenadaY > GameLib.HEIGHT + 10) {
                estado = INATIVA;
            }

            // Lógica de tiro
            if (tempoAtual > getProximoTiro() && coordenadaY < GameLib.HEIGHT * 0.90) { // Só atira se estiver acima do jogador
                // Lógica para adicionar um novo ProjetilInimigo à lista de projéteis do inimigo
                // Isso será tratado na classe Main ou em um gerenciador de projéteis
                setProximoTiro((long) (tempoAtual + 200 + Math.random() * 500));
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
            GameLib.setColor(Color.CYAN);
            GameLib.drawCircle(getCoordenadaX(), getCoordenadaY(), getRaio());
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


