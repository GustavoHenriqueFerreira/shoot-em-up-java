package game.entities;
import java.awt.Color;
import java.util.ArrayList;
import game.GameLib;

public abstract class Chefe extends Entidade {

    protected int pontosVida;
    protected int pontosVidaMaximos;
    protected double velocidade;
    protected long proximoTiro;
    protected boolean entrou; // Se já entrou completamente na tela

    public Chefe(double x, double y, double raio, int pontosVida) {
        super(x, y, raio);
        this.pontosVida = pontosVida;
        this.pontosVidaMaximos = pontosVida;
        this.velocidade = 0.1;
        this.proximoTiro = 0;
        this.entrou = false;
    }

    @Override
    public void explodir(long tempoAtual) {
        super.explodir(tempoAtual);
        // Duração específica para chefes
        this.fimExplosao = tempoAtual + 2000; // Explosão mais longa
    }

    public void receberDano() {
        if (estado == ATIVA) {
            pontosVida--;
            if (pontosVida <= 0) {
                explodir(System.currentTimeMillis());
            }
        }
    }

    public void desenharBarraVida() {
        if (estado == ATIVA && entrou) {
            // Barra de fundo
            GameLib.setColor(Color.RED);
            GameLib.fillRect(GameLib.WIDTH / 2, 30, 200, 10);

            // Barra de vida atual
            GameLib.setColor(Color.GREEN);
            double porcentagem = (double) pontosVida / pontosVidaMaximos;
            GameLib.fillRect(GameLib.WIDTH / 2, 30, 200 * porcentagem, 10);

            // Borda
            GameLib.setColor(Color.WHITE);
            GameLib.drawLine(GameLib.WIDTH / 2 - 100, 25, GameLib.WIDTH / 2 + 100, 25);
            GameLib.drawLine(GameLib.WIDTH / 2 - 100, 35, GameLib.WIDTH / 2 + 100, 35);
            GameLib.drawLine(GameLib.WIDTH / 2 - 100, 25, GameLib.WIDTH / 2 - 100, 35);
            GameLib.drawLine(GameLib.WIDTH / 2 + 100, 25, GameLib.WIDTH / 2 + 100, 35);
        }
    }

    public abstract void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis);

    protected boolean podeAtirar(long tempoAtual) {
        return estado == ATIVA && entrou && tempoAtual > proximoTiro;
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public boolean isEntrou() {
        return entrou;
    }

    public void setEntrou(boolean entrou) {
        this.entrou = entrou;
    }
}