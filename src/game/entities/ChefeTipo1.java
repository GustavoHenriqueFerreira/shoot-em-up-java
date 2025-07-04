package game.entities;
import java.awt.Color;
import java.util.ArrayList;
import game.GameLib;

public class ChefeTipo1 extends Chefe {

    private double direcao; // -1 para esquerda, 1 para direita
    private long ultimoTiro;

    public ChefeTipo1(double x, double y, int pontosVida) {
        super(x, y, 25.0, pontosVida);
        this.velocidade = 0.15;
        this.direcao = 1;
        this.ultimoTiro = 0;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == EXPLODINDO) {
            if (tempoAtual > fimExplosao) {
                estado = INATIVA;
            }
        } else if (estado == ATIVA) {
            // Entrada na tela
            if (!entrou) {
                coordenadaY += velocidade * delta;
                if (coordenadaY >= 100) {
                    entrou = true;
                }
            } else {
                // Movimento lateral
                coordenadaX += direcao * velocidade * delta;

                // Inverter direção nas bordas
                if (coordenadaX <= raio || coordenadaX >= GameLib.WIDTH - raio) {
                    direcao *= -1;
                }
            }
        }
    }

    @Override
    public void desenhar() {
        if (estado == EXPLODINDO) {
            double alpha = (System.currentTimeMillis() - inicioExplosao) / (fimExplosao - inicioExplosao);
            GameLib.drawExplosion(coordenadaX, coordenadaY, alpha);
        } else if (estado == ATIVA) {
            // Corpo principal
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(coordenadaX, coordenadaY, raio);

            // Detalhes
            GameLib.setColor(Color.DARK_GRAY);
            GameLib.drawCircle(coordenadaX - 8, coordenadaY - 8, 5);
            GameLib.drawCircle(coordenadaX + 8, coordenadaY - 8, 5);
            GameLib.drawCircle(coordenadaX, coordenadaY + 8, 8);
        }

        desenharBarraVida();
    }

    @Override
    public void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis) {
        if (podeAtirar(tempoAtual)) {
            // Padrão de tiro em leque
            double[] angulos = {
                    Math.PI/2 - Math.PI/4,
                    Math.PI/2 - Math.PI/8,
                    Math.PI/2,
                    Math.PI/2 + Math.PI/8,
                    Math.PI/2 + Math.PI/4
            };

            for (double angulo : angulos) {
                double vx = Math.cos(angulo) * 0.25;
                double vy = Math.sin(angulo) * 0.25;

                projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY + raio, vx, vy));
            }

            proximoTiro = tempoAtual + 1000; // Atira a cada segundo
        }
    }
}