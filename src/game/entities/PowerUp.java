package game.entities;

import game.GameLib;
import java.awt.Color;

public abstract class PowerUp extends Entidade {

    public PowerUp(double x, double y, double raio) {
        super(x, y, raio);
    }

    public abstract void aplicarEfeito(Jogador jogador);

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (getEstado() == ATIVA) {
            coordenadaY += 0.1 * delta; // Exemplo: power-up desce lentamente

            if (coordenadaY > GameLib.HEIGHT + 10) {
                estado = INATIVA;
            }
        }
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            // Implementação de desenho específica em subclasses
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


