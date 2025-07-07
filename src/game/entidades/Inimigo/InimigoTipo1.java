package game.entidades.Inimigo;
import java.awt.Color;
import java.util.ArrayList;
import game.lib.GameLib;
import game.entidades.Projetil.ProjetilInimigo;

public class InimigoTipo1 extends Inimigo {

    public InimigoTipo1(double x, double y) {
        super(x, y, 9.0, 0.20 + Math.random() * 0.15);
        this.proximoTiro = System.currentTimeMillis() + 500;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == EXPLODINDO) {
            if (tempoAtual > fimExplosao) {
                estado = INATIVA;
            }
        } else if (estado == ATIVA) {
            // Movimento
            coordenadaX += velocidade * Math.cos(angulo) * delta;
            coordenadaY += velocidade * Math.sin(angulo) * delta * (-1.0);
            angulo += velocidadeRotacao * delta;

            // Verificar se saiu da tela
            if (coordenadaY > GameLib.HEIGHT + 10) {
                estado = INATIVA;
            }
        }
    }

    @Override
    public void desenhar() {
        if (estado == EXPLODINDO) {
            double alpha = (System.currentTimeMillis() - inicioExplosao) / (fimExplosao - inicioExplosao);
            GameLib.drawExplosion(coordenadaX, coordenadaY, alpha);
        } else if (estado == ATIVA) {
            GameLib.setColor(Color.CYAN);
            GameLib.drawCircle(coordenadaX, coordenadaY, raio);
        }
    }

    @Override
    public void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis) {
        if (podeAtirar(tempoAtual) && coordenadaY < GameLib.HEIGHT * 0.8) {
            double vx = Math.cos(angulo) * 0.45;
            double vy = Math.sin(angulo) * 0.45 * (-1.0);

            projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, vx, vy));
            proximoTiro = (long) (tempoAtual + 200 + Math.random() * 500);
        }
    }
}