package game;

/**
* Controles do jogo:
*    - Cima, baixo, esquerda, direita: movimentação do jogador.
*    - Control: disparo de projéteis.
*    - ESC: sair do jogo.
*/

public class Main {
    /**
     * Espera ativa até que o tempo atual atinja ou ultrapasse o tempo indicado.
     * @param tempoAlvo timestamp em milissegundos para término da espera
     */
    public static void esperaAtiva(long tempoAlvo) {
        while (System.currentTimeMillis() < tempoAlvo) {
            Thread.yield();
        }
    }

    public static void main(String[] args) {
        boolean executando = true;
        long delta;
        long tempoAnterior = System.currentTimeMillis();

        // Inicializar o gerenciador do jogo
        GerenciadorJogo gerenciador = new GerenciadorJogo();

        // Tenta carregar configuração externa, caso falhe, usa padrão
        if (!gerenciador.carregarConfiguracao("config/game_config.txt")) {
            System.err.println("Falha ao carregar configuração. Usando configuração padrão.");
        }

        // Inicializa a interface gráfica
        GameLib.initGraphics();

        // Loop principal do jogo
        while (executando) {
            long tempoAtual = System.currentTimeMillis();
            delta = tempoAtual - tempoAnterior;
            tempoAnterior = tempoAtual;

            // Atualiza o estado do jogo
            gerenciador.atualizar(delta, tempoAtual);

            // Verifica condições de término
            if (gerenciador.jogoTerminado()) {
                System.out.println("Fim de jogo!");
                executando = false;
            } else if (gerenciador.jogoCompleto()) {
                System.out.println("Parabéns! Você completou o jogo!");
                executando = false;
            }

            // Verifica entrada do usuário (ESC para sair)
            if (GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) {
                executando = false;
            }

            // Desenha a cena atual
            gerenciador.desenhar();
            GameLib.display();

            // Aguarda para manter loop com intervalo aproximado de 3ms
            esperaAtiva(tempoAtual + 3);
        }

        System.exit(0);
    }
}