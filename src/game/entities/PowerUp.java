package game.entities;
import game.GameLib;

public abstract class PowerUp extends Entidade {

    protected double velocidade;

    public PowerUp(double x, double y, double raio) {
        super(x, y, raio);
        this.velocidade = 0.1;
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == ATIVA) {
            coordenadaY += velocidade * delta;

            // Verificar se saiu da tela
            if (coordenadaY > GameLib.HEIGHT + 10) {
                estado = INATIVA;
            }
        }
    }

    public abstract void aplicarEfeito(Jogador jogador, long tempoAtual);
}