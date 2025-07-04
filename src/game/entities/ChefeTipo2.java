package game.entities;
import java.awt.Color;
import java.util.ArrayList;
import game.GameLib;

public class ChefeTipo2 extends Chefe {

    private double anguloRotacao;
    private long ultimoTiroCircular;
    private long ultimoTiroJogador;

    public ChefeTipo2(double x, double y, int pontosVida) {
        super(x, y, 30.0, pontosVida);
        this.velocidade = 0.08;
        this.anguloRotacao = 0;
        this.ultimoTiroCircular = 0;
        this.ultimoTiroJogador = 0;
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
                if (coordenadaY >= 120) {
                    entrou = true;
                }
            } else {
                // Movimento circular
                anguloRotacao += 0.001 * delta;
                coordenadaX = GameLib.WIDTH / 2 + Math.cos(anguloRotacao) * 80;
                coordenadaY = 120 + Math.sin(anguloRotacao) * 20;
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
            GameLib.setColor(Color.PINK);
            GameLib.drawDiamond(coordenadaX, coordenadaY, raio);

            // Detalhes rotativos
            GameLib.setColor(new Color(128, 0, 128)); // PURPLE
            for (int i = 0; i < 4; i++) {
                double a = anguloRotacao + i * Math.PI / 2;
                double px = coordenadaX + Math.cos(a) * (raio - 5);
                double py = coordenadaY + Math.sin(a) * (raio - 5);
                GameLib.drawCircle(px, py, 3);
            }
        }

        desenharBarraVida();
    }

    @Override
    public void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis) {
        if (!podeAtirar(tempoAtual)) return;

        // Tiro circular a cada 2 segundos
        if (tempoAtual - ultimoTiroCircular > 2000) {
            for (int i = 0; i < 8; i++) {
                double angulo = (2 * Math.PI * i) / 8;
                double vx = Math.cos(angulo) * 0.2;
                double vy = Math.sin(angulo) * 0.2;

                projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, vx, vy));
            }
            ultimoTiroCircular = tempoAtual;
        }

        // Tiro direcionado ao jogador a cada 1.5 segundos
        if (tempoAtual - ultimoTiroJogador > 1500) {
            double jogadorX = GameLib.WIDTH / 2;
            double jogadorY = GameLib.HEIGHT * 0.9;

            double dx = jogadorX - coordenadaX;
            double dy = jogadorY - coordenadaY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > 0) {
                double vx = (dx / dist) * 0.3;
                double vy = (dy / dist) * 0.3;

                projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, vx, vy));
            }

            ultimoTiroJogador = tempoAtual;
        }
    }
}