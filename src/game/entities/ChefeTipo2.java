package game.entities;

import game.GameLib;
import java.awt.Color;

public class ChefeTipo2 extends Chefe {

    public ChefeTipo2(double x, double y, double raio, double velocidade, double angulo, double velocidadeRotacao, int pontosVida) {
        super(x, y, raio, velocidade, angulo, velocidadeRotacao, pontosVida);
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        super.atualizar(delta, tempoAtual);

        if (getEstado() == ATIVA) {
            // Lógica de movimento específica do Chefe Tipo 2
            // Exemplo: movimento em zigue-zague
            angulo += velocidadeRotacao * delta;

            // Lógica de tiro específica do Chefe Tipo 2
            if (tempoAtual > getProximoTiro()) {
                // Adicionar projéteis (exemplo: múltiplos tiros em leque)
                // Isso será tratado na classe Main ou em um gerenciador de projéteis
                setProximoTiro((long) (tempoAtual + 700 + Math.random() * 300));
            }
        }
    }

    @Override
    public void desenhar(long tempoAtual) {
        if (getEstado() == ATIVA) {
            GameLib.setColor(Color.RED);
            GameLib.drawCircle(getCoordenadaX(), getCoordenadaY(), getRaio() * 2.5); // Chefe maior e circular
            desenharBarraVida();
        } else if (getEstado() == EXPLODINDO) {
            double alpha = (tempoAtual - getInicioExplosao()) / (getFimExplosao() - getInicioExplosao());
            GameLib.drawExplosion(getCoordenadaX(), getCoordenadaY(), alpha);
        }
    }
}


