package game.entities;

import game.GameLib; 
import java.awt.Color;

public abstract class Entidade {

    // Constantes para os estados da entidade
    // Movidas para cá para maior encapsulamento e independência de Main.java
    public static final int INATIVA = 0;
    public static final int ATIVA = 1;
    public static final int EXPLODINDO = 2;

    // Atributos protegidos para que as subclasses possam acessá-los diretamente
    protected int estado; // Estado atual da entidade (INATIVA, ATIVA, EXPLODINDO)
    protected double coordenadaX; // Coordenada X da entidade
    protected double coordenadaY; // Coordenada Y da entidade
    protected double raio; // Raio (tamanho aproximado) da entidade

    // Atributos relacionados à explosão
    protected double inicioExplosao; // Instante de início da explosão
    protected double fimExplosao; // Instante de fim da explosão

    /**
     * Construtor para a classe abstrata Entidade.
     * Inicializa os atributos básicos de qualquer entidade no jogo.
     *
     * @param x Coordenada X inicial da entidade.
     * @param y Coordenada Y inicial da entidade.
     * @param raio Raio da entidade, usado para colisão e desenho.
     */
    public Entidade(double x, double y, double raio) {
        this.coordenadaX = x;
        this.coordenadaY = y;
        this.raio = raio;
        this.estado = ATIVA; // Por padrão, uma entidade nasce ATIVA
        this.inicioExplosao = 0;
        this.fimExplosao = 0;
    }

    /**
     * Método abstrato para atualização do estado da entidade.
     * Cada subclasse deverá implementar sua própria lógica de atualização (movimento, ações, etc.).
     *
     * @param delta Tempo decorrido desde a última atualização (em milissegundos).
     * @param tempoAtual Instante de tempo atual do jogo (em milissegundos).
     */
    public abstract void atualizar(long delta, long tempoAtual);

    /**
     * Método abstrato para desenhar a entidade na tela.
     * Cada subclasse deverá implementar sua própria lógica de desenho.
     */
    public abstract void desenhar(long tempoAtual);

    /**
     * Verifica se esta entidade está colidindo com outra entidade circular.
     *
     * @param outroX Coordenada X do centro da outra entidade.
     * @param outroY Coordenada Y do centro da outra entidade.
     * @param outroRaio Raio da outra entidade.
     * @return true se houver colisão, false caso contrário.
     */
    public boolean estaColidindo(double outroX, double outroY, double outroRaio) {
        // Entidades inativas ou já explodindo não participam ativamente de novas colisões
        if (this.estado == INATIVA || this.estado == EXPLODINDO) {
            return false;
        }

        double dx = this.coordenadaX - outroX;
        double dy = this.coordenadaY - outroY;
        double distancia = Math.sqrt(dx * dx + dy * dy);

        // Colisão ocorre se a distância entre os centros for menor que a soma dos raios
        // O fator 0.8 é um ajuste de sensibilidade da colisão (pode ser ajustado)
        return distancia < (this.raio + outroRaio) * 0.8;
    }

    /**
     * Inicia o processo de explosão da entidade.
     * O estado é alterado para EXPLODINDO e os tempos de início/fim da explosão são definidos.
     *
     * @param tempoAtual Instante de tempo atual do jogo.
     */
    public void explodir(long tempoAtual) {
        this.estado = EXPLODINDO;
        this.inicioExplosao = tempoAtual;
        // Duração padrão da explosão. Pode ser sobrescrita em subclasses se a explosão for de duração diferente.
        this.fimExplosao = tempoAtual + 500; // 500 milissegundos de duração
    }

    /**
     * Verifica se a animação de explosão da entidade já terminou.
     *
     * @param tempoAtual Instante de tempo atual do jogo.
     * @return true se a explosão terminou, false caso contrário.
     */
    public boolean explosaoFinalizada(long tempoAtual) {
        return this.estado == EXPLODINDO && tempoAtual > this.fimExplosao;
    }

    /**
     * Desativa a entidade, mudando seu estado para INATIVA.
     * Isso geralmente ocorre quando a entidade sai da tela ou é destruída.
     */
    public void desativar() {
        this.estado = INATIVA;
    }

    // Métodos Getters (para acessar os atributos da entidade)
    public int getEstado() {
        return estado;
    }

    public double getCoordenadaX() {
        return coordenadaX;
    }

    public double getCoordenadaY() {
        return coordenadaY;
    }

    public double getRaio() {
        return raio;
    }

    public double getInicioExplosao() {
        return inicioExplosao;
    }

    public double getFimExplosao() {
        return fimExplosao;
    }
}


