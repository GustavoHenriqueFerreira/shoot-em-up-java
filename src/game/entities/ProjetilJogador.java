package game.entities;

import game.GameLib;
import java.awt.Color;

public class ProjetilJogador extends Entidade {

    private double velocidadeX;
    private double velocidadeY;

    public ProjetilJogador(double x, double y, double raio, double vx, double vy) {
        super(x, y, raio);
        this.velocidadeX = vx;
        this.velocidadeY = vy;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (getEstado() == ATIVA) {
            coordenadaX += velocidadeX * delta;
            coordenadaY += velocidadeY * delta;

            if (coordenadaY < 0) {
                estado = INATIVA;
            }
        }
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            GameLib.setColor(Color.GREEN);
            GameLib.drawLine(getCoordenadaX(), getCoordenadaY() - getRaio(), getCoordenadaX(), getCoordenadaY() + getRaio());
            GameLib.drawLine(getCoordenadaX() - getRaio(), getCoordenadaY(), getCoordenadaX() + getRaio(), getCoordenadaY());
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


