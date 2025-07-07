package game.entidades.Chefe;

import java.awt.Color;
import java.util.ArrayList;
import game.lib.GameLib;
import game.entidades.Jogador;
import game.entidades.Projetil.ProjetilInimigo;
import game.entidades.Projetil.ProjetilTeleguiado;

public class ChefeTipo1 extends Chefe {

    private double direcao = 1.0;
    private long intervaloTiro = 800; // Intervalo de tiro reduzido para maior dificuldade

    public ChefeTipo1(double x, double y, int pontosVida) {
        super(x, y, 25.0, pontosVida);
        this.velocidade = 0.2; // Velocidade aumentada
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == ATIVA) {
            // Ativa a fase dois quando a vida chega a 50%
            if (pontosVida <= pontosVidaMaximos * 0.5 && !faseDoisAtivada) {
                faseDoisAtivada = true;
                this.velocidade *= 1.5; // Aumenta a velocidade na fase 2
                this.intervaloTiro = 500; // Reduz ainda mais o intervalo de tiro
            }

            if (!entrou) {
                coordenadaY += velocidade * delta;
                if (coordenadaY >= 100) entrou = true;
            } else {
                coordenadaX += direcao * velocidade * delta;
                if (coordenadaX <= raio || coordenadaX >= GameLib.WIDTH - raio) {
                    direcao *= -1;
                }
            }
        }
    }

    @Override
    public void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis, Jogador jogador) {
        if (podeAtirar(tempoAtual)) {
            if (!faseDoisAtivada) {
                // Padrão de tiro em leque (Fase 1)
                double[] angulos = {
                        Math.PI/2 - Math.PI/4,
                        Math.PI/2 - Math.PI/8,
                        Math.PI/2,
                        Math.PI/2 + Math.PI/8,
                        Math.PI/2 + Math.PI/4
                };

                for (double angulo : angulos) {
                    double vx = Math.cos(angulo) * 0.35; // Velocidade do projétil aumentada
                    double vy = Math.sin(angulo) * 0.35; // Velocidade do projétil aumentada
                    projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY + raio, vx, vy));
                }
            } else {
                // Padrão de tiro mais agressivo (Fase 2)
                for (int i = 0; i < 7; i++) { // Mais projéteis
                    double angulo = (2 * Math.PI * i) / 7;
                    double vx = Math.cos(angulo) * 0.4; // Velocidade do projétil ainda maior
                    double vy = Math.sin(angulo) * 0.4; // Velocidade do projétil ainda maior
                    projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, vx, vy));
                }
                // Adiciona um tiro teleguiado se o jogador não for nulo
                if (jogador != null) {
                    projeteis.add(new ProjetilTeleguiado(coordenadaX, coordenadaY, 0, 0.1, jogador));
                }
            }
            this.proximoTiro = tempoAtual + this.intervaloTiro;
        }
    }

    @Override
    public void desenhar() {
        if (estado == EXPLODINDO) {
            double alpha = (System.currentTimeMillis() - inicioExplosao) / (fimExplosao - inicioExplosao);
            GameLib.drawExplosion(coordenadaX, coordenadaY, alpha);
        } else if (estado == ATIVA) {
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
}