package game.entidades;

import game.GameLib;

public class ProjetilTeleguiado extends ProjetilInimigo {

    private Jogador jogadorAlvo;
    private double velocidadeTeleguiada;

    public ProjetilTeleguiado(double x, double y, double vx, double vy, Jogador jogador) {
        super(x, y, vx, vy);
        this.jogadorAlvo = jogador;
        this.velocidadeTeleguiada = 0.0001; // Ajuste a sensibilidade do teleguiado
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == ATIVA) {
            if (jogadorAlvo != null && jogadorAlvo.estado == ATIVA) {
                double dx = jogadorAlvo.coordenadaX - coordenadaX;
                double dy = jogadorAlvo.coordenadaY - coordenadaY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist > 0) {
                    velocidadeX += (dx / dist) * velocidadeTeleguiada * delta;
                    velocidadeY += (dy / dist) * velocidadeTeleguiada * delta;

                    // Limita a velocidade máxima do projétil teleguiado
                    double velocidadeAtual = Math.sqrt(velocidadeX * velocidadeX + velocidadeY * velocidadeY);
                    double velocidadeMaxima = 0.4; // Ajuste a velocidade máxima
                    if (velocidadeAtual > velocidadeMaxima) {
                        velocidadeX = (velocidadeX / velocidadeAtual) * velocidadeMaxima;
                        velocidadeY = (velocidadeY / velocidadeAtual) * velocidadeMaxima;
                    }
                }
            }

            coordenadaX += velocidadeX * delta;
            coordenadaY += velocidadeY * delta;

            if (coordenadaY > GameLib.HEIGHT + 10 || coordenadaY < -10 || coordenadaX > GameLib.WIDTH + 10 || coordenadaX < -10) {
                estado = INATIVA;
            }
        }
    }
}