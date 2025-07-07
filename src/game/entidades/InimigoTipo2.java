package game.entidades;
import java.awt.Color;
import java.util.ArrayList;
import game.GameLib;

public class InimigoTipo2 extends Inimigo {

    private boolean jaAtirou;

    public InimigoTipo2(double x, double y) {
        super(x, y, 12.0, 0.42);
        this.jaAtirou = false;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == EXPLODINDO) {
            if (tempoAtual > fimExplosao) {
                estado = INATIVA;
            }
        } else if (estado == ATIVA) {
            boolean atirarAgora = false;
            double yAnterior = coordenadaY;

            // Movimento
            coordenadaX += velocidade * Math.cos(angulo) * delta;
            coordenadaY += velocidade * Math.sin(angulo) * delta * (-1.0);
            angulo += velocidadeRotacao * delta;

            double limite = GameLib.HEIGHT * 0.30;

            if (yAnterior < limite && coordenadaY >= limite) {
                if (coordenadaX < GameLib.WIDTH / 2) velocidadeRotacao = 0.003;
                else velocidadeRotacao = -0.003;
            }

            if (velocidadeRotacao > 0 && Math.abs(angulo - 3 * Math.PI) < 0.05) {
                velocidadeRotacao = 0.0;
                angulo = 3 * Math.PI;
                atirarAgora = true;
            }

            if (velocidadeRotacao < 0 && Math.abs(angulo) < 0.05) {
                velocidadeRotacao = 0.0;
                angulo = 0.0;
                atirarAgora = true;
            }

            // Verificar se saiu da tela
            if (coordenadaX < -10 || coordenadaX > GameLib.WIDTH + 10) {
                estado = INATIVA;
            }

            if (atirarAgora && !jaAtirou) {
                jaAtirou = true;
            }
        }
    }

    @Override
    public void desenhar() {
        if (estado == EXPLODINDO) {
            double alpha = (System.currentTimeMillis() - inicioExplosao) / (fimExplosao - inicioExplosao);
            GameLib.drawExplosion(coordenadaX, coordenadaY, alpha);
        } else if (estado == ATIVA) {
            GameLib.setColor(Color.MAGENTA);
            GameLib.drawDiamond(coordenadaX, coordenadaY, raio);
        }
    }

    @Override
    public void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis) {
        if (jaAtirou) {
            double[] angulos = { Math.PI/2 + Math.PI/8, Math.PI/2, Math.PI/2 - Math.PI/8 };

            for (double a : angulos) {
                double anguloFinal = a + Math.random() * Math.PI/6 - Math.PI/12;
                double vx = Math.cos(anguloFinal) * 0.30;
                double vy = Math.sin(anguloFinal) * 0.30;

                projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, vx, vy));
            }

            jaAtirou = false; // Reset para próxima oportunidade
        }
    }
}