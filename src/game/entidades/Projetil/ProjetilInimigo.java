package game.entidades.Projetil;
import java.awt.Color;

import game.entidades.Entidade;
import game.lib.GameLib;

public class ProjetilInimigo extends Entidade {

    protected double velocidadeX;
    protected double velocidadeY;

    public ProjetilInimigo(double x, double y, double vx, double vy) {
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
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(coordenadaX, coordenadaY, raio);
        }
    }
}