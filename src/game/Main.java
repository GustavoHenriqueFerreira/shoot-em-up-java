package game;

/***********************************************************************/
/*                                                                     */
/* Para jogar:                                                         */
/*                                                                     */
/*    - cima, baixo, esquerda, direita: movimentação do player.        */
/*    - control: disparo de projéteis.                                 */
/*    - ESC: para sair do jogo.                                        */
/*                                                                     */
/***********************************************************************/

public class Main {

    /* Espera, sem fazer nada, até que o instante de tempo atual seja */
    /* maior ou igual ao instante especificado no parâmetro "time.    */

    public static void busyWait(long time){

        while(System.currentTimeMillis() < time) Thread.yield();
    }

    /* Método principal */

    public static void main(String [] args){

        /* Indica que o jogo está em execução */
        boolean running = true;

        /* variáveis usadas no controle de tempo efetuado no main loop */
        long delta;
        long currentTime = System.currentTimeMillis();

        /* Inicializar o gerenciador do jogo */
        GameManager gameManager = new GameManager();

        /* Carregar configuração do jogo */
        if (!gameManager.carregarConfiguracao("config/game_config.txt")) {
            System.err.println("Erro ao carregar configuração do jogo. Usando configuração padrão.");
            // Continuar com configuração padrão se não conseguir carregar
        }

        /* iniciado interface gráfica */
        GameLib.initGraphics();

        /*************************************************************************************************/
        /*                                                                                               */
        /* Main loop do jogo                                                                             */
        /* -----------------                                                                             */
        /*                                                                                               */
        /* O main loop do jogo executa as seguintes operações:                                           */
        /*                                                                                               */
        /* 1) Verifica se há colisões e atualiza estados dos elementos conforme a necessidade.           */
        /*                                                                                               */
        /* 2) Atualiza estados dos elementos baseados no tempo que correu entre a última atualização     */
        /*    e o timestamp atual: posição e orientação, execução de disparos de projéteis, etc.         */
        /*                                                                                               */
        /* 3) Processa entrada do usuário (teclado) e atualiza estados do player conforme a necessidade. */
        /*                                                                                               */
        /* 4) Desenha a cena, a partir dos estados dos elementos.                                        */
        /*                                                                                               */
        /* 5) Espera um período de tempo (de modo que delta seja aproximadamente sempre constante).      */
        /*                                                                                               */
        /*************************************************************************************************/

        while(running){

            /* Usada para atualizar o estado dos elementos do jogo    */
            /* (player, projéteis e inimigos) "delta" indica quantos  */
            /* ms se passaram desde a última atualização.             */

            delta = System.currentTimeMillis() - currentTime;

            /* Já a variável "currentTime" nos dá o timestamp atual.  */

            currentTime = System.currentTimeMillis();

            /* Atualizar o jogo */
            gameManager.atualizar(delta, currentTime);

            /* Verificar se o jogo terminou */
            if (gameManager.isJogoTerminado()) {
                System.out.println("Game Over!");
                running = false;
            }

            if (gameManager.isJogoCompleto()) {
                System.out.println("Parabéns! Você completou o jogo!");
                running = false;
            }

            /********************************************/
            /* Verificando entrada do usuário (teclado) */
            /********************************************/

            if(GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) running = false;

            /*******************/
            /* Desenho da cena */
            /*******************/

            gameManager.desenhar();

            /* chamada a display() da classe GameLib atualiza o desenho exibido pela interface do jogo. */

            GameLib.display();

            /* faz uma pausa de modo que cada execução do laço do main loop demore aproximadamente 3 ms. */

            busyWait(currentTime + 3);
        }

        System.exit(0);
    }
}