package game.entidades;
import java.awt.Color;
import game.GameLib;

public class PowerUpTiroRapido extends PowerUp {

    public PowerUpTiroRapido(double x, double y) {
        super(x, y, 8.0);
    }

    @Override
    public void desenhar() {
        if (estado == ATIVA) {
            GameLib.setColor(Color.ORANGE);
            GameLib.fillRect(coordenadaX, coordenadaY, raio * 2, raio * 2);
            GameLib.setColor(Color.WHITE);
            GameLib.drawLine(coordenadaX - 3, coordenadaY, coordenadaX + 3, coordenadaY);
            GameLib.drawLine(coordenadaX, coordenadaY - 3, coordenadaX, coordenadaY + 3);
        }
    }

    @Override
    public void aplicarEfeito(Jogador jogador, long tempoAtual) {
        jogador.ativarTiroRapido(tempoAtual);
        estado = INATIVA;
    }
}