package game.entities;

import game.GameLib;
import java.awt.Color;

public class ProjetilInimigo extends Entidade {

    private double velocidadeX;
    private double velocidadeY;

    public ProjetilInimigo(double x, double y, double raio, double vx, double vy) {
        super(x, y, raio);
        this.velocidadeX = vx;
        this.velocidadeY = vy;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (getEstado() == ATIVA) {
            coordenadaX += velocidadeX * delta;
            coordenadaY += velocidadeY * delta;

            if (coordenadaY > GameLib.HEIGHT) {
                estado = INATIVA;
            }
        }
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(getCoordenadaX(), getCoordenadaY(), getRaio());
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


