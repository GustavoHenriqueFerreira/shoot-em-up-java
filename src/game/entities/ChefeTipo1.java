package game.entities;

import java.awt.Color;
import java.util.ArrayList;
import game.GameLib;

public class ChefeTipo1 extends Chefe {

    private double direcao = 1.0;
    private long intervaloTiro = 1000; 

    public ChefeTipo1(double x, double y, int pontosVida) {
        super(x, y, 25.0, pontosVida);
        this.velocidade = 0.15; 
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == ATIVA) {
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
            
            double[] angulos = { Math.PI / 2 - Math.PI / 5, Math.PI / 2, Math.PI / 2 + Math.PI / 5 };

            for (double angulo : angulos) {
                double vx = Math.cos(angulo) * 0.30;
                double vy = Math.sin(angulo) * 0.30;
                projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY + raio, vx, vy));
            }
            
            this.proximoTiro = tempoAtual + this.intervaloTiro;
        }
    }

    @Override
    public void desenhar() {
        if (estado == ATIVA) {
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(coordenadaX, coordenadaY, raio);
        }
        desenharBarraVida();
    }
}