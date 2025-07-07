package game.entidades;
import java.awt.Color;
import game.GameLib;

public class ProjetilJogador extends Entidade {

    private double velocidadeX;
    private double velocidadeY;

    public ProjetilJogador(double x, double y, double vx, double vy) {
        super(x, y, 2.0);
        this.velocidadeX = vx;
        this.velocidadeY = vy;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == ATIVA) {
            coordenadaX += velocidadeX * delta;
            coordenadaY += velocidadeY * delta;

            // Verificar se saiu da tela
            if (coordenadaY < 0 || coordenadaY > GameLib.HEIGHT ||
                    coordenadaX < 0 || coordenadaX > GameLib.WIDTH) {
                estado = INATIVA;
            }
        }
    }

    @Override
    public void desenhar() {
        if (estado == ATIVA) {
            GameLib.setColor(Color.GREEN);
            GameLib.drawLine(coordenadaX, coordenadaY - 5, coordenadaX, coordenadaY + 5);
            GameLib.drawLine(coordenadaX - 1, coordenadaY - 3, coordenadaX - 1, coordenadaY + 3);
            GameLib.drawLine(coordenadaX + 1, coordenadaY - 3, coordenadaX + 1, coordenadaY + 3);
        }
    }
}