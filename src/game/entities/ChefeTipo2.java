package game.entities;

import java.awt.Color;
import java.util.ArrayList;
import game.GameLib;

public class ChefeTipo2 extends Chefe {

    private double anguloRotacao = 0;
    private long ultimoTiroCircular, ultimoTiroTeleguiado, ultimoTiroPulso;
    private long intervaloTiroCircular = 1600;
    private long intervaloTiroTeleguiado = 1200;
    private long intervaloTiroPulso = 2500;

    public ChefeTipo2(double x, double y, int pontosVida) {
        super(x, y, 30.0, pontosVida);
        this.velocidade = 0.08;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == ATIVA) {
            double vidaPercentual = (double) pontosVida / pontosVidaMaximos;
            if (vidaPercentual <= 0.5 && !faseDoisAtivada) {
                this.faseDoisAtivada = true;
                this.velocidade *= 1.4;
                this.intervaloTiroCircular = 900;
                this.intervaloTiroTeleguiado = 600;
            }

            if (!entrou) {
                coordenadaY += this.velocidade * delta;
                if (coordenadaY >= 120) entrou = true;
            } else {
                anguloRotacao += 0.001 * delta * (faseDoisAtivada ? 1.5 : 1.0);
                coordenadaX = GameLib.WIDTH / 2 + Math.cos(anguloRotacao) * 100;
                coordenadaY = 120 + Math.sin(anguloRotacao) * 30;
            }
        }
    }

    @Override
    public void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis, Jogador jogador) {
        if (!entrou) return;

        if (!faseDoisAtivada) {
            // Fase 1: Apenas o tiro circular
            if (tempoAtual > ultimoTiroCircular + intervaloTiroCircular) {
                for (int i = 0; i < 8; i++) {
                    double angle = (2 * Math.PI * i) / 8;
                    projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, Math.cos(angle) * 0.2, Math.sin(angle) * 0.2));
                }
                ultimoTiroCircular = tempoAtual;
            }
        } else {
            // Fase 2: Múltiplos ataques
            // Tiro circular mais rápido
            if (tempoAtual > ultimoTiroCircular + intervaloTiroCircular) {
                for (int i = 0; i < 12; i++) {
                     double angle = (2 * Math.PI * i) / 12;
                    projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, Math.cos(angle) * 0.22, Math.sin(angle) * 0.22));
                }
                ultimoTiroCircular = tempoAtual;
            }
            // Tiro teleguiado (precisa da classe ProjetilTeleguiado)
            if (tempoAtual > ultimoTiroTeleguiado + intervaloTiroTeleguiado) {
                 projeteis.add(new ProjetilTeleguiado(coordenadaX, coordenadaY, 0, 0.1, jogador));
                 ultimoTiroTeleguiado = tempoAtual;
            }
            // Pulso Duplo
            if (tempoAtual > ultimoTiroPulso + intervaloTiroPulso) {
                for (int i = 0; i < 12; i++) projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, Math.cos(i*Math.PI/6)*0.18, Math.sin(i*Math.PI/6)*0.18));
                for (int i = 0; i < 8; i++) projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, Math.cos(i*Math.PI/4)*0.35, Math.sin(i*Math.PI/4)*0.35));
                ultimoTiroPulso = tempoAtual;
            }
        }
    }

    @Override
    public void desenhar() {
         if (estado == ATIVA) {
            GameLib.setColor(Color.PINK);
            GameLib.drawDiamond(coordenadaX, coordenadaY, raio);
        }
        desenharBarraVida();
    }
}