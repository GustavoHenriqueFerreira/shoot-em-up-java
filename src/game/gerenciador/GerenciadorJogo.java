package game.gerenciador;

import game.entidades.*;
import game.entidades.Chefe.Chefe;
import game.entidades.Chefe.ChefeTipo1;
import game.entidades.Chefe.ChefeTipo2;
import game.entidades.Inimigo.Inimigo;
import game.entidades.Inimigo.InimigoTipo1;
import game.entidades.Inimigo.InimigoTipo2;
import game.entidades.Jogador;
import game.entidades.PowerUp.PowerUp;
import game.entidades.PowerUp.PowerUpEscudo;
import game.entidades.PowerUp.PowerUpTiroRapido;
import game.entidades.Projetil.ProjetilInimigo;
import game.entidades.Projetil.ProjetilJogador;
import game.lib.GameLib;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GerenciadorJogo {

    private Jogador jogador;
    private ArrayList<ProjetilJogador> projeteisDoJogador = new ArrayList<>();
    private ArrayList<ProjetilInimigo> projeteisDosInimigos = new ArrayList<>();
    private ArrayList<Inimigo> inimigos = new ArrayList<>();
    private ArrayList<PowerUp> powerUps = new ArrayList<>();
    private ArrayList<Chefe> chefes = new ArrayList<>();
    private Fundo fundo = new Fundo();

    private int faseAtual = 0;
    private int totalFases;
    private ArrayList<ConfiguracaoFase> configuracoesFases = new ArrayList<>();
    private long inicioDaFase;
    private boolean faseCompleta;

    private ArrayList<ConfiguracaoFase.EventoInimigo> eventosInimigosPendentes;
    private ArrayList<ConfiguracaoFase.EventoPowerUp> eventosPowerUpsPendentes;
    private ArrayList<ConfiguracaoFase.EventoChefe> eventosChefesPendentes;

    private long proximoSpawnInimigoTipo1;
    private long proximoSpawnInimigoTipo2;
    private double posicaoSpawnInimigoTipo2;
    private int contadorInimigoTipo2;

    public GerenciadorJogo() {
        reiniciarEstadoDoJogo();
    }

    /** Inicializa ou reinicia os estados do jogo e das entidades */
    private void reiniciarEstadoDoJogo() {
        projeteisDoJogador.clear();
        projeteisDosInimigos.clear();
        inimigos.clear();
        powerUps.clear();
        chefes.clear();
        configuracoesFases.clear();

        faseAtual = 0;
        faseCompleta = false;

        long tempoAtual = System.currentTimeMillis();
        proximoSpawnInimigoTipo1 = tempoAtual + 2000;
        proximoSpawnInimigoTipo2 = tempoAtual + 7000;
        posicaoSpawnInimigoTipo2 = GameLib.WIDTH * 0.20;
        contadorInimigoTipo2 = 0;
    }

    /**
     * Carrega o arquivo de configuração geral do jogo.
     * @param arquivoConfig caminho do arquivo de configuração
     * @return true se carregou com sucesso, false caso contrário
     */
    public boolean carregarConfiguracao(String arquivoConfig) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoConfig))) {
            int pontosVidaJogador = Integer.parseInt(br.readLine().trim());
            jogador = new Jogador(GameLib.WIDTH / 2, GameLib.HEIGHT * 0.90, pontosVidaJogador);

            totalFases = Integer.parseInt(br.readLine().trim());

            for (int i = 0; i < totalFases; i++) {
                String arquivoFase = br.readLine().trim();
                ConfiguracaoFase configuracaoFase = carregarFase(arquivoFase);
                if (configuracaoFase == null) return false;
                configuracoesFases.add(configuracaoFase);
            }

            iniciarFase(0);
            return true;

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar configuração do jogo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carrega as configurações de uma fase específica a partir do arquivo.
     */
    private ConfiguracaoFase carregarFase(String arquivoFase) {
        ConfiguracaoFase configuracao = new ConfiguracaoFase();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoFase))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] partes = linha.trim().split("\\s+");
                if (partes.length < 5) continue;

                String tipo = partes[0];
                int tipoEntidade = Integer.parseInt(partes[1]);

                if (tipo.equals("INIMIGO")) {
                    long quando = Long.parseLong(partes[2]);
                    double x = Double.parseDouble(partes[3]);
                    double y = Double.parseDouble(partes[4]);
                    configuracao.adicionarInimigo(tipoEntidade, quando, x, y);

                } else if (tipo.equals("POWERUP")) {
                    long quando = Long.parseLong(partes[2]);
                    double x = Double.parseDouble(partes[3]);
                    double y = Double.parseDouble(partes[4]);
                    configuracao.adicionarPowerUp(tipoEntidade, quando, x, y);

                } else if (tipo.equals("CHEFE") && partes.length >= 6) {
                    int pontosVida = Integer.parseInt(partes[2]);
                    long quando = Long.parseLong(partes[3]);
                    double x = Double.parseDouble(partes[4]);
                    double y = Double.parseDouble(partes[5]);
                    configuracao.adicionarChefe(tipoEntidade, pontosVida, quando, x, y);
                }
            }

            return configuracao;

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar fase " + arquivoFase + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Inicializa uma fase com índice fornecido.
     */
    private void iniciarFase(int numeroFase) {
        if (numeroFase >= configuracoesFases.size()) return;

        faseAtual = numeroFase;
        inicioDaFase = System.currentTimeMillis();
        faseCompleta = false;

        inimigos.clear();
        chefes.clear();
        powerUps.clear();
        projeteisDosInimigos.clear();

        long agora = System.currentTimeMillis();
        proximoSpawnInimigoTipo1 = agora + 2000;
        proximoSpawnInimigoTipo2 = agora + 7000;
        posicaoSpawnInimigoTipo2 = GameLib.WIDTH * 0.20;
        contadorInimigoTipo2 = 0;

        ConfiguracaoFase configAtual = configuracoesFases.get(faseAtual);
        eventosInimigosPendentes = new ArrayList<>(configAtual.getEventosInimigos());
        eventosPowerUpsPendentes = new ArrayList<>(configAtual.getEventosPowerUps());
        eventosChefesPendentes = new ArrayList<>(configAtual.getEventosChefes());

        System.out.println("Iniciando fase " + (faseAtual + 1));
    }

    /**
     * Atualiza o estado do jogo a cada frame.
     * @param delta tempo decorrido desde o último frame
     * @param tempoAtual tempo atual em milissegundos
     */
    public void atualizar(long delta, long tempoAtual) {
        fundo.atualizar(delta);

        if (jogador != null) {
            jogador.atualizar(delta, tempoAtual);
            if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
                jogador.atirar(tempoAtual, projeteisDoJogador);
            }
        }

        if (!faseCompleta) {
            processarEventosDaFase(tempoAtual);
            if (chefes.isEmpty()) spawnarInimigosNormais(tempoAtual);
        }

        atualizarEntidades(delta, tempoAtual);
        verificarColisoes();
        removerEntidadesInativas();
        verificarSeFaseFoiConcluida();
    }

    /** Processa os eventos pendentes da fase, como spawn de inimigos, chefes e power-ups */
    private void processarEventosDaFase(long tempoAtual) {
        long tempoDecorrido = tempoAtual - inicioDaFase;

        processarEventos(eventosInimigosPendentes, e -> tempoDecorrido >= e.quando, e -> spawnarInimigo(e.tipo, e.x, e.y));
        processarEventos(eventosPowerUpsPendentes, e -> tempoDecorrido >= e.quando, e -> spawnarPowerUp(e.tipo, e.x, e.y));
        processarEventos(eventosChefesPendentes, e -> tempoDecorrido >= e.quando, e -> spawnarChefe(e.tipo, e.pontosVida, e.x, e.y));
    }

    /**
     * Método genérico para processar eventos de spawn.
     */
    private <T> void processarEventos(Iterable<T> eventos, java.util.function.Predicate<T> condicao, java.util.function.Consumer<T> acao) {
        Iterator<T> iterador = ((ArrayList<T>) eventos).iterator();
        while (iterador.hasNext()) {
            T evento = iterador.next();
            if (condicao.test(evento)) {
                acao.accept(evento);
                iterador.remove();
            }
        }
    }

    /** Spawn automático de inimigos normais */
    private void spawnarInimigosNormais(long tempoAtual) {
        if (tempoAtual > proximoSpawnInimigoTipo1) {
            double xAleatorio = Math.random() * (GameLib.WIDTH - 20.0) + 10.0;
            spawnarInimigo(1, xAleatorio, -10.0);
            proximoSpawnInimigoTipo1 = tempoAtual + 500;
        }

        if (tempoAtual > proximoSpawnInimigoTipo2) {
            spawnarInimigo(2, posicaoSpawnInimigoTipo2, -10.0);
            contadorInimigoTipo2++;

            if (contadorInimigoTipo2 < 10) {
                proximoSpawnInimigoTipo2 = tempoAtual + 120;
            } else {
                contadorInimigoTipo2 = 0;
                posicaoSpawnInimigoTipo2 = Math.random() > 0.5 ? GameLib.WIDTH * 0.2 : GameLib.WIDTH * 0.8;
                proximoSpawnInimigoTipo2 = tempoAtual + 3000 + (long)(Math.random() * 3000);
            }
        }
    }

    private void spawnarInimigo(int tipo, double x, double y) {
        if (tipo == 1) inimigos.add(new InimigoTipo1(x, y));
        else if (tipo == 2) inimigos.add(new InimigoTipo2(x, y));
    }

    private void spawnarPowerUp(int tipo, double x, double y) {
        if (tipo == 1) powerUps.add(new PowerUpTiroRapido(x, y));
        else if (tipo == 2) powerUps.add(new PowerUpEscudo(x, y));
    }

    private void spawnarChefe(int tipo, int pontosVida, double x, double y) {
        if (tipo == 1) chefes.add(new ChefeTipo1(x, y, pontosVida));
        else if (tipo == 2) chefes.add(new ChefeTipo2(x, y, pontosVida));
    }

    /** Atualiza todas as entidades ativas do jogo */
    private void atualizarEntidades(long delta, long tempoAtual) {
        projeteisDoJogador.forEach(p -> p.atualizar(delta, tempoAtual));
        projeteisDosInimigos.forEach(p -> p.atualizar(delta, tempoAtual));

        inimigos.forEach(i -> {
            i.atualizar(delta, tempoAtual);
            i.atirar(tempoAtual, projeteisDosInimigos);
        });

        powerUps.forEach(p -> p.atualizar(delta, tempoAtual));

        chefes.forEach(c -> {
            c.atualizar(delta, tempoAtual);
            c.atirar(tempoAtual, projeteisDosInimigos, jogador); // Passa o jogador para o método atirar do chefe
        });
    }

    /** Verifica todas as colisões relevantes entre entidades */
    private void verificarColisoes() {
        if (jogador == null || jogador.getEstado() != Entidade.ATIVA) return;

        verificarColisoesJogadorComProjetilInimigo();
        verificarColisoesJogadorComInimigos();
        verificarColisoesJogadorComChefes();
        verificarColisoesJogadorComPowerUps();
        verificarColisoesProjetilJogadorComInimigosEChefes();
    }

    private void verificarColisoesJogadorComProjetilInimigo() {
        for (ProjetilInimigo p : projeteisDosInimigos) {
            if (p.getEstado() == Entidade.ATIVA && jogador.estaColidindo(p.getCoordenadaX(), p.getCoordenadaY(), p.getRaio())) {
                jogador.receberDano();
                p.desativar();
            }
        }
    }

    private void verificarColisoesJogadorComInimigos() {
        for (Inimigo i : inimigos) {
            if (i.getEstado() == Entidade.ATIVA && jogador.estaColidindo(i.getCoordenadaX(), i.getCoordenadaY(), i.getRaio())) {
                jogador.receberDano();
                i.explodir(System.currentTimeMillis());
            }
        }
    }

    private void verificarColisoesJogadorComChefes() {
        for (Chefe c : chefes) {
            if (c.getEstado() == Entidade.ATIVA && jogador.estaColidindo(c.getCoordenadaX(), c.getCoordenadaY(), c.getRaio())) {
                jogador.receberDano();
            }
        }
    }

    private void verificarColisoesJogadorComPowerUps() {
        for (PowerUp p : powerUps) {
            if (p.getEstado() == Entidade.ATIVA && jogador.estaColidindo(p.getCoordenadaX(), p.getCoordenadaY(), p.getRaio())) {
                p.aplicarEfeito(jogador, System.currentTimeMillis());
            }
        }
    }

    private void verificarColisoesProjetilJogadorComInimigosEChefes() {
        for (ProjetilJogador p : projeteisDoJogador) {
            if (p.getEstado() != Entidade.ATIVA) continue;

            for (Inimigo i : inimigos) {
                if (i.getEstado() == Entidade.ATIVA && p.estaColidindo(i.getCoordenadaX(), i.getCoordenadaY(), i.getRaio())) {
                    i.explodir(System.currentTimeMillis());
                    p.desativar();
                    break;
                }
            }

            for (Chefe c : chefes) {
                if (c.getEstado() == Entidade.ATIVA && p.estaColidindo(c.getCoordenadaX(), c.getCoordenadaY(), c.getRaio())) {
                    c.receberDano();
                    p.desativar();
                    break;
                }
            }
        }
    }

    /** Remove entidades que estão inativas ou tiveram explosão concluída */
    private void removerEntidadesInativas() {
        projeteisDoJogador.removeIf(p -> p.getEstado() == Entidade.INATIVA);
        projeteisDosInimigos.removeIf(p -> p.getEstado() == Entidade.INATIVA);
        inimigos.removeIf(i -> i.getEstado() == Entidade.INATIVA || i.explosaoFinalizada(System.currentTimeMillis()));
        powerUps.removeIf(p -> p.getEstado() == Entidade.INATIVA);
        chefes.removeIf(c -> c.getEstado() == Entidade.INATIVA || c.explosaoFinalizada(System.currentTimeMillis()));
    }

    /** Verifica se a fase foi concluída e avança para a próxima ou termina o jogo */
    private void verificarSeFaseFoiConcluida() {
        if (faseCompleta) return;

        boolean chefesAtivos = chefes.stream()
                .anyMatch(c -> c.getEstado() == Entidade.ATIVA || c.getEstado() == Entidade.EXPLODINDO);

        boolean chefesPendentes = !eventosChefesPendentes.isEmpty();

        if (!chefesAtivos && !chefesPendentes) {
            faseCompleta = true;

            if (faseAtual + 1 < totalFases) {
                iniciarFase(faseAtual + 1);
            } else {
                System.out.println("Parabéns! Você completou o jogo!");
            }
        }
    }

    /** Desenha todos os elementos do jogo na tela */
    public void desenhar() {
        fundo.desenhar();
        if (jogador != null) jogador.desenhar();

        projeteisDoJogador.forEach(ProjetilJogador::desenhar);
        projeteisDosInimigos.forEach(ProjetilInimigo::desenhar);
        inimigos.forEach(Inimigo::desenhar);
        powerUps.forEach(PowerUp::desenhar);
        chefes.forEach(Chefe::desenhar);

        desenharHUD();
    }

    /** Desenha informações como vida do jogador */
    private void desenharHUD() {
        if (jogador == null) return;

        GameLib.setColor(java.awt.Color.WHITE);
        for (int i = 0; i < jogador.getPontosVida(); i++) {
            GameLib.fillRect(20 + i * 15, GameLib.HEIGHT - 30, 10, 10);
        }

        GameLib.setColor(java.awt.Color.YELLOW);
        GameLib.fillRect(GameLib.WIDTH - 100, 20, 80, 15);
    }

    public boolean jogoTerminado() {
        return jogador != null && jogador.getEstado() == Entidade.INATIVA && jogador.getPontosVida() <= 0;
    }

    public boolean jogoCompleto() {
        return faseAtual >= totalFases - 1 && faseCompleta;
    }
}