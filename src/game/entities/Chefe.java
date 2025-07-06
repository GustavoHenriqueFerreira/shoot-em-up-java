package game.entities;
import java.awt.Color;
import java.util.ArrayList;
import game.GameLib;

public abstract class Chefe extends Entidade {

    protected int pontosVida;
    protected int pontosVidaMaximos;
    protected double velocidade;
    protected long proximoTiro;
    protected boolean entrou;
    protected boolean faseDoisAtivada; 

    public Chefe(double x, double y, double raio, int pontosVida) {
        super(x, y, raio);
        this.pontosVida = pontosVida;
        this.pontosVidaMaximos = pontosVida;
        this.velocidade = 0.1;
        this.proximoTiro = 0;
        this.entrou = false;
        this.faseDoisAtivada = false; 
    }

    // ... (os métodos explodir, receberDano, desenharBarraVida continuam os mesmos) ...
    @Override
    public void explodir(long tempoAtual) {
        super.explodir(tempoAtual);
        this.fimExplosao = tempoAtual + 2000;
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
            GameLib.setColor(Color.DARK_GRAY);
            GameLib.fillRect(coordenadaX, coordenadaY - raio - 20, raio * 2, 8);
            GameLib.setColor(Color.RED);
            double porcentagem = (double) pontosVida / pontosVidaMaximos;
            GameLib.fillRect(coordenadaX, coordenadaY - raio - 20, raio * 2 * porcentagem, 8);
        }
    }

    public abstract void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis, Jogador jogador);

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
