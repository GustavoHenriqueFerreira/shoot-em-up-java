package game.entidades;
import java.awt.Color;
import java.util.ArrayList;
import game.GameLib;

public class Jogador extends Entidade {

    private double velocidadeX;
    private double velocidadeY;
    private long proximoTiro;
    private int pontosVida;
    private int pontosVidaMaximos;

    // Power-ups ativos
    private boolean tiroRapido;
    private long fimTiroRapido;
    private boolean escudo;
    private long fimEscudo;

    public Jogador(double x, double y, int pontosVida) {
        super(x, y, 12.0);
        this.velocidadeX = 0.25;
        this.velocidadeY = 0.25;
        this.proximoTiro = 0;
        this.pontosVida = pontosVida;
        this.pontosVidaMaximos = pontosVida;
        this.tiroRapido = false;
        this.escudo = false;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        // Verificar se power-ups expiraram
        if (tiroRapido && tempoAtual > fimTiroRapido) {
            tiroRapido = false;
        }
        if (escudo && tempoAtual > fimEscudo) {
            escudo = false;
        }

        // Processar entrada do usuário
        if (estado == ATIVA) {
            if (GameLib.iskeyPressed(GameLib.KEY_UP)) coordenadaY -= delta * velocidadeY;
            if (GameLib.iskeyPressed(GameLib.KEY_DOWN)) coordenadaY += delta * velocidadeY;
            if (GameLib.iskeyPressed(GameLib.KEY_LEFT)) coordenadaX -= delta * velocidadeX;
            if (GameLib.iskeyPressed(GameLib.KEY_RIGHT)) coordenadaX += delta * velocidadeX;

            // Manter jogador dentro da tela
            if (coordenadaX < 0.0) coordenadaX = 0.0;
            if (coordenadaX >= GameLib.WIDTH) coordenadaX = GameLib.WIDTH - 1;
            if (coordenadaY < 25.0) coordenadaY = 25.0;
            if (coordenadaY >= GameLib.HEIGHT) coordenadaY = GameLib.HEIGHT - 1;
        }

        // Verificar se explosão terminou
        if (estado == EXPLODINDO && tempoAtual > fimExplosao) {
            if (pontosVida > 0) {
                estado = ATIVA;
            } else {
                estado = INATIVA;
            }
        }
    }

    @Override
    public void desenhar() {
        if (estado == EXPLODINDO) {
            double alpha = (System.currentTimeMillis() - inicioExplosao) / (fimExplosao - inicioExplosao);
            GameLib.drawExplosion(coordenadaX, coordenadaY, alpha);
        } else if (estado == ATIVA) {
            if (escudo) {
                GameLib.setColor(Color.YELLOW);
                GameLib.drawCircle(coordenadaX, coordenadaY, raio + 5);
            }
            GameLib.setColor(Color.BLUE);
            GameLib.drawPlayer(coordenadaX, coordenadaY, raio);
        }
    }

    public boolean podeAtirar(long tempoAtual) {
        return estado == ATIVA && tempoAtual > proximoTiro;
    }

    public void atirar(long tempoAtual, ArrayList<ProjetilJogador> projeteis) {
        if (podeAtirar(tempoAtual)) {
            projeteis.add(new ProjetilJogador(coordenadaX, coordenadaY - 2 * raio, 0.0, -1.0));

            // Definir próximo tiro baseado no power-up
            if (tiroRapido) {
                proximoTiro = tempoAtual + 50; // Tiro mais rápido
            } else {
                proximoTiro = tempoAtual + 100; // Tiro normal
            }
        }
    }

    public void receberDano() {
        if (!escudo) {
            pontosVida--;
            explodir(System.currentTimeMillis());
        }
    }

    public void ativarTiroRapido(long tempoAtual) {
        this.tiroRapido = true;
        this.fimTiroRapido = tempoAtual + 10000; // 10 segundos
    }

    public void ativarEscudo(long tempoAtual) {
        this.escudo = true;
        this.fimEscudo = tempoAtual + 8000; // 8 segundos
    }

    public int getPontosVida() {
        return pontosVida;
    }

    public int getPontosVidaMaximos() {
        return pontosVidaMaximos;
    }

    public boolean temEscudo() {
        return escudo;
    }
}