package game.entidades.Projetil;

import game.entidades.Jogador;
import game.lib.GameLib;

public class ProjetilTeleguiado extends ProjetilInimigo {

    private Jogador jogadorAlvo;
    private double velocidadeTeleguiada;

    public ProjetilTeleguiado(double x, double y, double vx, double vy, Jogador jogador) {
        super(x, y, vx, vy);
        this.jogadorAlvo = jogador;
        this.velocidadeTeleguiada = 0.00005; // Sensibilidade do teleguiado
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (getEstado() == ATIVA) {
            if (jogadorAlvo != null && jogadorAlvo.getEstado() == ATIVA) {
                double dx = jogadorAlvo.getCoordenadaX() - getCoordenadaX();
                double dy = jogadorAlvo.getCoordenadaY() - getCoordenadaY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist > 0) {
                    velocidadeX += (dx / dist) * velocidadeTeleguiada * delta;
                    velocidadeY += (dy / dist) * velocidadeTeleguiada * delta;

                    // Limita a velocidade máxima do projétil teleguiado
                    double velocidadeAtual = Math.sqrt(velocidadeX * velocidadeX + velocidadeY * velocidadeY);
                    double velocidadeMaxima = 0.4;
                    if (velocidadeAtual > velocidadeMaxima) {
                        velocidadeX = (velocidadeX / velocidadeAtual) * velocidadeMaxima;
                        velocidadeY = (velocidadeY / velocidadeAtual) * velocidadeMaxima;
                    }
                }
            }

            coordenadaX += velocidadeX * delta;
            coordenadaY += velocidadeY * delta;

            if (coordenadaY > GameLib.HEIGHT + 10 || coordenadaY < -10 ||
                    coordenadaX > GameLib.WIDTH + 10 || coordenadaX < -10) {
                desativar();
            }
        }
    }
}