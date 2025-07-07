package game.gerenciador;
import java.util.ArrayList;

public class ConfiguracaoFase {

    private ArrayList<EventoInimigo> eventosInimigos;
    private ArrayList<EventoPowerUp> eventosPowerUps;
    private ArrayList<EventoChefe> eventosChefes;

    public ConfiguracaoFase() {
        eventosInimigos = new ArrayList<>();
        eventosPowerUps = new ArrayList<>();
        eventosChefes = new ArrayList<>();
    }

    public void adicionarInimigo(int tipo, long quando, double x, double y) {
        eventosInimigos.add(new EventoInimigo(tipo, quando, x, y));
    }

    public void adicionarPowerUp(int tipo, long quando, double x, double y) {
        eventosPowerUps.add(new EventoPowerUp(tipo, quando, x, y));
    }

    public void adicionarChefe(int tipo, int pontosVida, long quando, double x, double y) {
        // Aumentando a vida dos chefes em 100% para maior dificuldade
        eventosChefes.add(new EventoChefe(tipo, (int)(pontosVida * 2), quando, x, y));
    }

    public ArrayList<EventoInimigo> getEventosInimigos() {
        return eventosInimigos;
    }

    public ArrayList<EventoPowerUp> getEventosPowerUps() {
        return eventosPowerUps;
    }

    public ArrayList<EventoChefe> getEventosChefes() {
        return eventosChefes;
    }

    public static class EventoInimigo {
        public int tipo;
        public long quando;
        public double x, y;

        public EventoInimigo(int tipo, long quando, double x, double y) {
            this.tipo = tipo;
            this.quando = quando;
            this.x = x;
            this.y = y;
        }
    }

    public static class EventoPowerUp {
        public int tipo;
        public long quando;
        public double x, y;

        public EventoPowerUp(int tipo, long quando, double x, double y) {
            this.tipo = tipo;
            this.quando = quando;
            this.x = x;
            this.y = y;
        }
    }

    public static class EventoChefe {
        public int tipo;
        public int pontosVida;
        public long quando;
        public double x, y;

        public EventoChefe(int tipo, int pontosVida, long quando, double x, double y) {
            this.tipo = tipo;
            this.pontosVida = pontosVida;
            this.quando = quando;
            this.x = x;
            this.y = y;
        }
    }
}