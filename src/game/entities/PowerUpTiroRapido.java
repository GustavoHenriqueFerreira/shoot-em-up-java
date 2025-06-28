package game.entities;

import game.GameLib;
import java.awt.Color;

public class PowerUpTiroRapido extends PowerUp {

    public PowerUpTiroRapido(double x, double y, double raio) {
        super(x, y, raio);
    }

    @Override
    public void aplicarEfeito(Jogador jogador) {
        jogador.setProximoTiro(jogador.getProximoTiro() - 50); // Diminui o tempo entre tiros
        estado = INATIVA; // Desativa o power-up após o uso
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            GameLib.setColor(Color.ORANGE);
            GameLib.drawDiamond(getCoordenadaX(), getCoordenadaY(), getRaio());
            GameLib.setColor(Color.WHITE);
            GameLib.drawLine(getCoordenadaX() - getRaio() / 2, getCoordenadaY(), getCoordenadaX() + getRaio() / 2, getCoordenadaY());
            GameLib.drawLine(getCoordenadaX() - getRaio() / 2, getCoordenadaY() - getRaio() / 4, getCoordenadaX() + getRaio() / 2, getCoordenadaY() - getRaio() / 4);
            GameLib.drawLine(getCoordenadaX() - getRaio() / 2, getCoordenadaY() + getRaio() / 4, getCoordenadaX() + getRaio() / 2, getCoordenadaY() + getRaio() / 4);
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


