package game.entities;

import game.GameLib;
import java.awt.Color;

public class PowerUpVida extends PowerUp {

    public PowerUpVida(double x, double y, double raio) {
        super(x, y, raio);
    }

    @Override
    public void aplicarEfeito(Jogador jogador) {
        jogador.setVidas(jogador.getVidas() + 1); // Aumenta uma vida
        estado = INATIVA; // Desativa o power-up após o uso
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            GameLib.setColor(Color.GREEN);
            GameLib.drawCircle(getCoordenadaX(), getCoordenadaY(), getRaio());
            GameLib.setColor(Color.WHITE);
            GameLib.drawLine(getCoordenadaX() - getRaio() / 2, getCoordenadaY(), getCoordenadaX() + getRaio() / 2, getCoordenadaY());
            GameLib.drawLine(getCoordenadaX(), getCoordenadaY() - getRaio() / 2, getCoordenadaX(), getCoordenadaY() + getRaio() / 2);
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


