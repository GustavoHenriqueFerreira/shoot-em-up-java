package game.entidades;
import java.awt.Color;
import game.GameLib;

public class PowerUpEscudo extends PowerUp {

    public PowerUpEscudo(double x, double y) {
        super(x, y, 8.0);
    }

    @Override
    public void desenhar() {
        if (estado == ATIVA) {
            GameLib.setColor(Color.YELLOW);
            GameLib.fillRect(coordenadaX, coordenadaY, raio * 2, raio * 2);
            GameLib.setColor(Color.BLACK);
            GameLib.drawCircle(coordenadaX, coordenadaY, 3);
        }
    }

    @Override
    public void aplicarEfeito(Jogador jogador, long tempoAtual) {
        jogador.ativarEscudo(tempoAtual);
        estado = INATIVA;
    }
}