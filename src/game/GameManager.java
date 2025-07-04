package game;

import game.entities.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GameManager {

    private Jogador jogador;
    private ArrayList<ProjetilJogador> projeteisJogador;
    private ArrayList<ProjetilInimigo> projeteisInimigo;
    private ArrayList<Inimigo> inimigos;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<Chefe> chefes;
    private Background background;

    // Configuração de fases
    private int faseAtual;
    private int totalFases;
    private ArrayList<ConfiguracaoFase> configuracoesFases;
    private long inicioFase;
    private boolean faseCompleta;

    // Spawn de inimigos normais
    private long proximoInimigo1;
    private long proximoInimigo2;
    private double spawnXInimigo2;
    private int contadorInimigo2;

    public GameManager() {
        inicializar();
    }

    private void inicializar() {
        projeteisJogador = new ArrayList<>();
        projeteisInimigo = new ArrayList<>();
        inimigos = new ArrayList<>();
        powerUps = new ArrayList<>();
        chefes = new ArrayList<>();
        background = new Background();

        // Configuração inicial
        faseAtual = 0;
        faseCompleta = false;
        proximoInimigo1 = System.currentTimeMillis() + 2000;
        proximoInimigo2 = System.currentTimeMillis() + 7000;
        spawnXInimigo2 = GameLib.WIDTH * 0.20;
        contadorInimigo2 = 0;

        configuracoesFases = new ArrayList<>();
    }

    public boolean carregarConfiguracao(String arquivoConfig) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoConfig))) {
            // Primeira linha: pontos de vida do jogador
            int pontosVidaJogador = Integer.parseInt(br.readLine().trim());
            jogador = new Jogador(GameLib.WIDTH / 2, GameLib.HEIGHT * 0.90, pontosVidaJogador);

            // Segunda linha: número de fases
            totalFases = Integer.parseInt(br.readLine().trim());

            // Carregar configurações de cada fase
            for (int i = 0; i < totalFases; i++) {
                String arquivoFase = br.readLine().trim();
                ConfiguracaoFase config = carregarFase(arquivoFase);
                if (config != null) {
                    configuracoesFases.add(config);
                } else {
                    System.err.println("Erro ao carregar fase: " + arquivoFase);
                    return false;
                }
            }

            iniciarFase(0);
            return true;

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar configuração: " + e.getMessage());
            return false;
        }
    }

    private ConfiguracaoFase carregarFase(String arquivoFase) {
        ConfiguracaoFase config = new ConfiguracaoFase();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoFase))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                String[] partes = linha.split("\\s+");
                if (partes.length < 5) continue;

                String tipo = partes[0];
                int tipoNum = Integer.parseInt(partes[1]);
                long quando = Long.parseLong(partes[2]);
                double x = Double.parseDouble(partes[3]);
                double y = Double.parseDouble(partes[4]);

                if (tipo.equals("INIMIGO")) {
                    config.adicionarInimigo(tipoNum, quando, x, y);
                } else if (tipo.equals("CHEFE")) {
                    int pontosVida = Integer.parseInt(partes[2]);
                    quando = Long.parseLong(partes[3]);
                    x = Double.parseDouble(partes[4]);
                    y = Double.parseDouble(partes[5]);
                    config.adicionarChefe(tipoNum, pontosVida, quando, x, y);
                } else if (tipo.equals("POWERUP")) {
                    config.adicionarPowerUp(tipoNum, quando, x, y);
                }
            }

            return config;

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao carregar fase " + arquivoFase + ": " + e.getMessage());
            return null;
        }
    }

    private void iniciarFase(int numeroFase) {
        if (numeroFase >= configuracoesFases.size()) return;

        faseAtual = numeroFase;
        inicioFase = System.currentTimeMillis();
        faseCompleta = false;

        // Limpar entidades da fase anterior
        inimigos.clear();
        chefes.clear();
        powerUps.clear();
        projeteisInimigo.clear();

        System.out.println("Iniciando fase " + (faseAtual + 1));
    }

    public void atualizar(long delta, long tempoAtual) {
        // Atualizar background
        background.atualizar(delta);

        // Atualizar jogador
        if (jogador != null) {
            jogador.atualizar(delta, tempoAtual);

            // Verificar se jogador quer atirar
            if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
                jogador.atirar(tempoAtual, projeteisJogador);
            }
        }

        // Processar eventos da fase atual
        if (!faseCompleta && faseAtual < configuracoesFases.size()) {
            processarEventosFase(tempoAtual);
        }

        // Spawn de inimigos normais (apenas se não há chefe ativo)
        if (chefes.isEmpty()) {
            spawnInimigosNormais(tempoAtual);
        }

        // Atualizar entidades
        atualizarEntidades(delta, tempoAtual);

        // Verificar colisões
        verificarColisoes();

        // Remover entidades inativas
        removerEntidadesInativas();

        // Verificar se fase foi completada
        verificarFaseCompleta(tempoAtual);
    }

    private void processarEventosFase(long tempoAtual) {
        ConfiguracaoFase config = configuracoesFases.get(faseAtual);
        long tempoFase = tempoAtual - inicioFase;

        // Processar spawns de inimigos
        Iterator<ConfiguracaoFase.EventoInimigo> itInimigos = config.getEventosInimigos().iterator();
        while (itInimigos.hasNext()) {
            ConfiguracaoFase.EventoInimigo evento = itInimigos.next();
            if (tempoFase >= evento.quando) {
                spawnInimigo(evento.tipo, evento.x, evento.y);
                itInimigos.remove();
            }
        }

        // Processar spawns de power-ups
        Iterator<ConfiguracaoFase.EventoPowerUp> itPowerUps = config.getEventosPowerUps().iterator();
        while (itPowerUps.hasNext()) {
            ConfiguracaoFase.EventoPowerUp evento = itPowerUps.next();
            if (tempoFase >= evento.quando) {
                spawnPowerUp(evento.tipo, evento.x, evento.y);
                itPowerUps.remove();
            }
        }

        // Processar spawns de chefes
        Iterator<ConfiguracaoFase.EventoChefe> itChefes = config.getEventosChefes().iterator();
        while (itChefes.hasNext()) {
            ConfiguracaoFase.EventoChefe evento = itChefes.next();
            if (tempoFase >= evento.quando) {
                spawnChefe(evento.tipo, evento.pontosVida, evento.x, evento.y);
                itChefes.remove();
            }
        }
    }

    private void spawnInimigosNormais(long tempoAtual) {
        // Spawn inimigo tipo 1
        if (tempoAtual > proximoInimigo1) {
            double x = Math.random() * (GameLib.WIDTH - 20.0) + 10.0;
            spawnInimigo(1, x, -10.0);
            proximoInimigo1 = tempoAtual + 500;
        }

        // Spawn inimigo tipo 2
        if (tempoAtual > proximoInimigo2) {
            spawnInimigo(2, spawnXInimigo2, -10.0);
            contadorInimigo2++;

            if (contadorInimigo2 < 10) {
                proximoInimigo2 = tempoAtual + 120;
            } else {
                contadorInimigo2 = 0;
                spawnXInimigo2 = Math.random() > 0.5 ? GameLib.WIDTH * 0.2 : GameLib.WIDTH * 0.8;
                proximoInimigo2 = (long) (tempoAtual + 3000 + Math.random() * 3000);
            }
        }
    }

    private void spawnInimigo(int tipo, double x, double y) {
        if (tipo == 1) {
            inimigos.add(new InimigoTipo1(x, y));
        } else if (tipo == 2) {
            inimigos.add(new InimigoTipo2(x, y));
        }
    }

    private void spawnPowerUp(int tipo, double x, double y) {
        if (tipo == 1) {
            powerUps.add(new PowerUpTiroRapido(x, y));
        } else if (tipo == 2) {
            powerUps.add(new PowerUpEscudo(x, y));
        }
    }

    private void spawnChefe(int tipo, int pontosVida, double x, double y) {
        if (tipo == 1) {
            chefes.add(new ChefeTipo1(x, y, pontosVida));
        } else if (tipo == 2) {
            chefes.add(new ChefeTipo2(x, y, pontosVida));
        }
    }

    private void atualizarEntidades(long delta, long tempoAtual) {
        // Atualizar projéteis do jogador
        for (ProjetilJogador projetil : projeteisJogador) {
            projetil.atualizar(delta, tempoAtual);
        }

        // Atualizar projéteis dos inimigos
        for (ProjetilInimigo projetil : projeteisInimigo) {
            projetil.atualizar(delta, tempoAtual);
        }

        // Atualizar inimigos
        for (Inimigo inimigo : inimigos) {
            inimigo.atualizar(delta, tempoAtual);
            inimigo.atirar(tempoAtual, projeteisInimigo);
        }

        // Atualizar power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.atualizar(delta, tempoAtual);
        }

        // Atualizar chefes
        for (Chefe chefe : chefes) {
            chefe.atualizar(delta, tempoAtual);
            chefe.atirar(tempoAtual, projeteisInimigo);
        }
    }

    private void verificarColisoes() {
        if (jogador == null || jogador.getEstado() != Entidade.ATIVA) return;

        // Colisões jogador - projéteis inimigos
        for (ProjetilInimigo projetil : projeteisInimigo) {
            if (projetil.getEstado() == Entidade.ATIVA) {
                if (jogador.estaColidindo(projetil.getCoordenadaX(), projetil.getCoordenadaY(), projetil.getRaio())) {
                    jogador.receberDano();
                    projetil.desativar();
                }
            }
        }

        // Colisões jogador - inimigos
        for (Inimigo inimigo : inimigos) {
            if (inimigo.getEstado() == Entidade.ATIVA) {
                if (jogador.estaColidindo(inimigo.getCoordenadaX(), inimigo.getCoordenadaY(), inimigo.getRaio())) {
                    jogador.receberDano();
                    inimigo.explodir(System.currentTimeMillis());
                }
            }
        }

        // Colisões jogador - chefes
        for (Chefe chefe : chefes) {
            if (chefe.getEstado() == Entidade.ATIVA) {
                if (jogador.estaColidindo(chefe.getCoordenadaX(), chefe.getCoordenadaY(), chefe.getRaio())) {
                    jogador.receberDano();
                }
            }
        }

        // Colisões jogador - power-ups
        for (PowerUp powerUp : powerUps) {
            if (powerUp.getEstado() == Entidade.ATIVA) {
                if (jogador.estaColidindo(powerUp.getCoordenadaX(), powerUp.getCoordenadaY(), powerUp.getRaio())) {
                    powerUp.aplicarEfeito(jogador, System.currentTimeMillis());
                }
            }
        }

        // Colisões projéteis jogador - inimigos
        for (ProjetilJogador projetil : projeteisJogador) {
            if (projetil.getEstado() == Entidade.ATIVA) {
                // Contra inimigos normais
                for (Inimigo inimigo : inimigos) {
                    if (inimigo.getEstado() == Entidade.ATIVA) {
                        if (projetil.estaColidindo(inimigo.getCoordenadaX(), inimigo.getCoordenadaY(), inimigo.getRaio())) {
                            inimigo.explodir(System.currentTimeMillis());
                            projetil.desativar();
                        }
                    }
                }

                // Contra chefes
                for (Chefe chefe : chefes) {
                    if (chefe.getEstado() == Entidade.ATIVA) {
                        if (projetil.estaColidindo(chefe.getCoordenadaX(), chefe.getCoordenadaY(), chefe.getRaio())) {
                            chefe.receberDano();
                            projetil.desativar();
                        }
                    }
                }
            }
        }
    }

    private void removerEntidadesInativas() {
        projeteisJogador.removeIf(p -> p.getEstado() == Entidade.INATIVA);
        projeteisInimigo.removeIf(p -> p.getEstado() == Entidade.INATIVA);
        inimigos.removeIf(i -> i.getEstado() == Entidade.INATIVA || i.explosaoFinalizada(System.currentTimeMillis()));
        powerUps.removeIf(p -> p.getEstado() == Entidade.INATIVA);
        chefes.removeIf(c -> c.getEstado() == Entidade.INATIVA || c.explosaoFinalizada(System.currentTimeMillis()));
    }

    private void verificarFaseCompleta(long tempoAtual) {
        if (faseCompleta) return;

        // Fase completa quando todos os chefes foram derrotados
        boolean todosChefesDerrota = true;
        for (Chefe chefe : chefes) {
            if (chefe.getEstado() == Entidade.ATIVA || chefe.getEstado() == Entidade.EXPLODINDO) {
                todosChefesDerrota = false;
                break;
            }
        }

        if (todosChefesDerrota && !chefes.isEmpty()) {
            faseCompleta = true;

            // Avançar para próxima fase
            if (faseAtual + 1 < totalFases) {
                iniciarFase(faseAtual + 1);
            } else {
                System.out.println("Jogo completo! Parabéns!");
            }
        }
    }

    public void desenhar() {
        // Desenhar background
        background.desenhar();

        // Desenhar jogador
        if (jogador != null) {
            jogador.desenhar();
        }

        // Desenhar projéteis
        for (ProjetilJogador projetil : projeteisJogador) {
            projetil.desenhar();
        }

        for (ProjetilInimigo projetil : projeteisInimigo) {
            projetil.desenhar();
        }

        // Desenhar inimigos
        for (Inimigo inimigo : inimigos) {
            inimigo.desenhar();
        }

        // Desenhar power-ups
        for (PowerUp powerUp : powerUps) {
            powerUp.desenhar();
        }

        // Desenhar chefes
        for (Chefe chefe : chefes) {
            chefe.desenhar();
        }

        // Desenhar HUD
        desenharHUD();
    }

    private void desenharHUD() {
        if (jogador != null) {
            // Mostrar pontos de vida
            GameLib.setColor(java.awt.Color.WHITE);
            for (int i = 0; i < jogador.getPontosVida(); i++) {
                GameLib.fillRect(20 + i * 15, GameLib.HEIGHT - 30, 10, 10);
            }

            // Mostrar fase atual
            GameLib.setColor(java.awt.Color.YELLOW);
            // Simular texto mostrando "FASE X"
            GameLib.fillRect(GameLib.WIDTH - 100, 20, 80, 15);
        }
    }

    public boolean isJogoTerminado() {
        return jogador != null && jogador.getEstado() == Entidade.INATIVA && jogador.getPontosVida() <= 0;
    }

    public boolean isJogoCompleto() {
        return faseAtual >= totalFases - 1 && faseCompleta;
    }
}