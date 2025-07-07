package game.entidades.Chefe;

import java.awt.Color;
import java.util.ArrayList;
import game.lib.GameLib;
import game.entidades.Jogador;
import game.entidades.Projetil.ProjetilInimigo;
import game.entidades.Projetil.ProjetilTeleguiado;

public class ChefeTipo2 extends Chefe {

    private double anguloRotacao = 0;
    private long ultimoTiroCircular, ultimoTiroTeleguiado, ultimoTiroPulso;

    private long intervaloTiroCircular = 1500;
    private long intervaloTiroTeleguiado = 1000;
    private long intervaloTiroPulso = 2500;

    // Para controlar o efeito de dano
    private long tempoUltimoDano = 0;
    private static final long DURACAO_FLASH = 100; // duração do flash em ms

    public ChefeTipo2(double x, double y, int pontosVida) {
        super(x, y, 30.0, pontosVida);
        this.velocidade = 0.1; // mais rápido
    }

    @Override
    public void atualizar(long delta, long tempoAtual) {
        if (estado == EXPLODINDO) {
            if (tempoAtual > fimExplosao) estado = INATIVA;
            return;
        }

        if (estado == ATIVA) {
            double vidaPercentual = (double) pontosVida / pontosVidaMaximos;

            if (vidaPercentual <= 0.6 && !faseDoisAtivada) {
                faseDoisAtivada = true;
                velocidade *= 1.7;
                intervaloTiroCircular = 700;
                intervaloTiroTeleguiado = 500;
                intervaloTiroPulso = 1500;
            }

            if (!entrou) {
                coordenadaY += velocidade * delta;
                if (coordenadaY >= 120) {
                    entrou = true;
                    double dx = coordenadaX - GameLib.WIDTH / 2.0;
                    double dy = coordenadaY - 120;
                    anguloRotacao = Math.atan2(dy, dx);
                }
            } else {
                anguloRotacao += 0.002 * delta * (faseDoisAtivada ? 2.2 : 1.0);
                coordenadaX = GameLib.WIDTH / 2 + Math.cos(anguloRotacao) * (faseDoisAtivada ? 150 : 100);
                coordenadaY = 120 + Math.sin(anguloRotacao) * (faseDoisAtivada ? 60 : 30);
            }
        }
    }

    @Override
    public void atirar(long tempoAtual, ArrayList<ProjetilInimigo> projeteis, Jogador jogador) {
        if (!podeAtirar(tempoAtual)) return;

        if (!faseDoisAtivada) {
            if (tempoAtual > ultimoTiroCircular + intervaloTiroCircular) {
                for (int i = 0; i < 16; i++) {
                    double angle = (2 * Math.PI * i) / 16;
                    projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, Math.cos(angle) * 0.28, Math.sin(angle) * 0.28));
                }
                ultimoTiroCircular = tempoAtual;
            }

            if (tempoAtual > ultimoTiroTeleguiado + intervaloTiroTeleguiado && jogador != null) {
                projeteis.add(new ProjetilTeleguiado(coordenadaX, coordenadaY, 0, 0.25, jogador));
                ultimoTiroTeleguiado = tempoAtual;
            }

        } else {
            if (tempoAtual > ultimoTiroCircular + intervaloTiroCircular) {
                for (int i = 0; i < 24; i++) {
                    double angle = (2 * Math.PI * i) / 24;
                    projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, Math.cos(angle) * 0.35, Math.sin(angle) * 0.35));
                }
                ultimoTiroCircular = tempoAtual;
            }

            if (tempoAtual > ultimoTiroTeleguiado + intervaloTiroTeleguiado && jogador != null) {
                for (int i = 0; i < 2; i++) { // dois teleguiados de uma vez
                    projeteis.add(new ProjetilTeleguiado(coordenadaX, coordenadaY, 0, 0.3 + (i * 0.05), jogador));
                }
                ultimoTiroTeleguiado = tempoAtual;
            }

            if (tempoAtual > ultimoTiroPulso + intervaloTiroPulso) {
                for (int i = 0; i < 18; i++) {
                    double angle = (2 * Math.PI * i) / 18;
                    projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, Math.cos(angle) * 0.3, Math.sin(angle) * 0.3));
                }

                for (int i = 0; i < 12; i++) {
                    double angle = (2 * Math.PI * i) / 12 + Math.PI / 12; // intercalado
                    projeteis.add(new ProjetilInimigo(coordenadaX, coordenadaY, Math.cos(angle) * 0.45, Math.sin(angle) * 0.45));
                }

                ultimoTiroPulso = tempoAtual;
            }
        }
    }

    // Novo método para aplicar dano e controlar flash
    public void tomarDano(int dano, long tempoAtual) {
        pontosVida -= dano;
        tempoUltimoDano = tempoAtual;

        if (pontosVida <= 0 && estado != EXPLODINDO) {
            pontosVida = 0;
            estado = EXPLODINDO;
            inicioExplosao = tempoAtual;
            fimExplosao = tempoAtual + 2000; // duração da explosão
        }
    }

    @Override
    public void desenhar() {
        long tempoAtual = System.currentTimeMillis();
        boolean estaEmFlash = (tempoAtual - tempoUltimoDano) < DURACAO_FLASH;

        if (estado == EXPLODINDO) {
            double alpha = (tempoAtual - inicioExplosao) / (double)(fimExplosao - inicioExplosao);
            GameLib.drawExplosion(coordenadaX, coordenadaY, alpha);
        } else if (estado == ATIVA) {
            Color corPrincipal = Color.MAGENTA;
            Color corDetalhe = Color.RED;

            GameLib.setColor(corPrincipal);
            GameLib.drawDiamond(coordenadaX, coordenadaY, raio);

            GameLib.setColor(corDetalhe);
            for (int i = 0; i < 4; i++) {
                double a = anguloRotacao + i * Math.PI / 2;
                double px = coordenadaX + Math.cos(a) * (raio - 5);
                double py = coordenadaY + Math.sin(a) * (raio - 5);
                GameLib.drawCircle(px, py, 3);
            }

            if (estaEmFlash) {
                GameLib.setColor(new Color(1f, 1f, 1f, 0.6f));
                GameLib.drawDiamond(coordenadaX, coordenadaY, raio);
                for (int i = 0; i < 4; i++) {
                    double a = anguloRotacao + i * Math.PI / 2;
                    double px = coordenadaX + Math.cos(a) * (raio - 5);
                    double py = coordenadaY + Math.sin(a) * (raio - 5);
                    GameLib.drawCircle(px, py, 3);
                }
            }

            desenharBarraVida();
        }
    }
}