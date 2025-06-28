package game.entities;

import game.GameLib;
import java.awt.Color;

public abstract class Inimigo extends Entidade {

    protected double velocidade;
    protected double angulo;
    protected double velocidadeRotacao;
    protected long proximoTiro;

    public Inimigo(double x, double y, double raio, double velocidade, double angulo, double velocidadeRotacao) {
        super(x, y, raio);
        this.velocidade = velocidade;
        this.angulo = angulo;
        this.velocidadeRotacao = velocidadeRotacao;
        this.proximoTiro = 0;
    }

    public long getProximoTiro() {
        return proximoTiro;
    }

    public void setProximoTiro(long proximoTiro) {
        this.proximoTiro = proximoTiro;
    }

    public double getAngulo() {
        return angulo;
    }
}


