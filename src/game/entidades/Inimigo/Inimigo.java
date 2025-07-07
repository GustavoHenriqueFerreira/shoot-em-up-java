package game.entidades.Inimigo;
import game.entidades.Entidade;
import game.entidades.Projetil.ProjetilInimigo;

import java.util.ArrayList;

public abstract class Inimigo extends Entidade {

    protected double velocidade;
    protected double angulo;
    protected double velocidadeRotacao;
    protected long proximoTiro;

    public Inimigo(double x, double y, double raio, double velocidade) {
        super(x, y, raio);
        this.velocidade = velocidade;
        this.angulo = (3 * Math.PI) / 2; // Apontando para baixo
        this.velocidadeRotacao = 0.0;
        this.proximoTiro = 0;
    }

    @Override
    public void explodir(long tempoAtual) {
        super.explodir(tempoAtual);
        // Duração específica para inimigos
        this.fimExplosao = tempoAtual + 500;
    }

    public abstract void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis);

    protected boolean podeAtirar(long tempoAtual) {
        return estado == ATIVA && tempoAtual > proximoTiro;
    }
}