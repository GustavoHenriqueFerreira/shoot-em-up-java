package game.entities;

import game.GameLib;
import java.awt.Color;

public class Jogador extends Entidade {

    private double velocidadeX;
    private double velocidadeY;
    private long proximoTiro;
    private int vidas;

    public Jogador(double x, double y, double raio, int vidas) {
        super(x, y, raio);
        this.velocidadeX = 0.25;
        this.velocidadeY = 0.25;
        this.proximoTiro = 0;
        this.vidas = vidas;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (getEstado() == ATIVA) {
            if (GameLib.iskeyPressed(GameLib.KEY_UP)) coordenadaY -= velocidadeY * delta;
            if (GameLib.iskeyPressed(GameLib.KEY_DOWN)) coordenadaY += velocidadeY * delta;
            if (GameLib.iskeyPressed(GameLib.KEY_LEFT)) coordenadaX -= velocidadeX * delta;
            if (GameLib.iskeyPressed(GameLib.KEY_RIGHT)) coordenadaX += velocidadeX * delta;

            // Garante que o jogador não saia da tela
            if (coordenadaX < 0) coordenadaX = 0;
            if (coordenadaX > GameLib.WIDTH) coordenadaX = GameLib.WIDTH;
            if (coordenadaY < 0) coordenadaY = 0;
            if (coordenadaY > GameLib.HEIGHT) coordenadaY = GameLib.HEIGHT;

            // Lógica de tiro
            if (GameLib.iskeyPressed(GameLib.KEY_CONTROL) && tempoAtual > proximoTiro) {
                // Lógica para adicionar um novo ProjetilJogador à lista de projéteis do jogador
                // Isso será tratado na classe Main ou em um gerenciador de projéteis
                proximoTiro = tempoAtual + 100; // Intervalo entre tiros
            }
        } else if (getEstado() == EXPLODINDO) {
            if (explosaoFinalizada(tempoAtual)) {
                vidas--;
                if (vidas > 0) {
                    estado = ATIVA; // Renasce se tiver vidas
                    coordenadaX = GameLib.WIDTH / 2;
                    coordenadaY = GameLib.HEIGHT * 0.90;
                } else {
                    estado = INATIVA; // Game Over
                }
            }
        }
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            GameLib.setColor(java.awt.Color.BLUE);
            GameLib.drawPlayer(coordenadaX, coordenadaY, raio);
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(coordenadaX, coordenadaY, alpha);
        }
    }

    public long getProximoTiro() {
        return proximoTiro;
    }

    public void setProximoTiro(long proximoTiro) {
        this.proximoTiro = proximoTiro;
    }

    public int getVidas() {
        return vidas;
    }

    public void perderVida() {
        this.vidas--;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }
}


