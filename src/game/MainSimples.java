package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

/***********************************************************************/
/*                                                                     */
/* Versão simplificada para testar fases e chefes                     */
/*                                                                     */
/***********************************************************************/

public class MainSimples {
    // Estados das entidades
    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    public static final int EXPLODING = 2;

    // Classe simples para entidades
    static class Entity {
        int state = ACTIVE;
        double x, y, radius;
        double vx, vy;
        double explosionStart, explosionEnd;

        Entity(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        void explode(long currentTime) {
            state = EXPLODING;
            explosionStart = currentTime;
            explosionEnd = currentTime + 500;
        }

        boolean isColliding(Entity other) {
            if (state != ACTIVE || other.state != ACTIVE) return false;
            double dx = x - other.x;
            double dy = y - other.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            return dist < (radius + other.radius) * 0.8;
        }
    }

    // Classe para o jogador
    static class Player extends Entity {
        int health;
        long nextShot = 0;

        Player(double x, double y, int health) {
            super(x, y, 12.0);
            this.health = health;
        }

        void update(long delta, long currentTime) {
            if (state == ACTIVE) {
                if (GameLib.iskeyPressed(GameLib.KEY_UP)) y -= delta * 0.25;
                if (GameLib.iskeyPressed(GameLib.KEY_DOWN)) y += delta * 0.25;
                if (GameLib.iskeyPressed(GameLib.KEY_LEFT)) x -= delta * 0.25;
                if (GameLib.iskeyPressed(GameLib.KEY_RIGHT)) x += delta * 0.25;

                // Manter na tela
                if (x < 0) x = 0;
                if (x >= GameLib.WIDTH) x = GameLib.WIDTH - 1;
                if (y < 25) y = 25;
                if (y >= GameLib.HEIGHT) y = GameLib.HEIGHT - 1;
            }

            if (state == EXPLODING && currentTime > explosionEnd) {
                if (health > 0) {
                    state = ACTIVE;
                } else {
                    state = INACTIVE;
                }
            }
        }

        void draw() {
            if (state == EXPLODING) {
                double alpha = (System.currentTimeMillis() - explosionStart) / (explosionEnd - explosionStart);
                GameLib.drawExplosion(x, y, alpha);
            } else if (state == ACTIVE) {
                GameLib.setColor(Color.BLUE);
                GameLib.drawPlayer(x, y, radius);
            }
        }

        boolean canShoot(long currentTime) {
            return state == ACTIVE && currentTime > nextShot;
        }

        void shoot(long currentTime, ArrayList<Entity> projectiles) {
            if (canShoot(currentTime)) {
                Entity proj = new Entity(x, y - 2 * radius, 2.0);
                proj.vy = -1.0;
                projectiles.add(proj);
                nextShot = currentTime + 100;
            }
        }

        void takeDamage() {
            health--;
            explode(System.currentTimeMillis());
        }
    }

    // Classe para chefes
    static class Boss extends Entity {
        int health, maxHealth;
        long nextShot = 0;
        boolean entered = false;
        int type;
        double direction = 1;

        Boss(double x, double y, int health, int type) {
            super(x, y, type == 1 ? 25.0 : 30.0);
            this.health = health;
            this.maxHealth = health;
            this.type = type;
        }

        void update(long delta, long currentTime) {
            if (state == EXPLODING) {
                if (currentTime > explosionEnd) {
                    state = INACTIVE;
                }
                return;
            }

            if (state == ACTIVE) {
                if (!entered) {
                    y += 0.1 * delta;
                    if (y >= 100) entered = true;
                } else {
                    // Movimento específico por tipo
                    if (type == 1) {
                        // Chefe tipo 1: movimento lateral
                        x += direction * 0.15 * delta;
                        if (x <= radius || x >= GameLib.WIDTH - radius) {
                            direction *= -1;
                        }
                    } else {
                        // Chefe tipo 2: movimento circular
                        double angle = currentTime * 0.001;
                        x = GameLib.WIDTH / 2 + Math.cos(angle) * 80;
                        y = 120 + Math.sin(angle) * 20;
                    }
                }
            }
        }

        void draw() {
            if (state == EXPLODING) {
                double alpha = (System.currentTimeMillis() - explosionStart) / (explosionEnd - explosionStart);
                GameLib.drawExplosion(x, y, alpha);
            } else if (state == ACTIVE) {
                if (type == 1) {
                    GameLib.setColor(Color.RED);
                    GameLib.drawCircle(x, y, radius);
                } else {
                    GameLib.setColor(Color.PINK);
                    GameLib.drawDiamond(x, y, radius);
                }

                // Barra de vida
                if (entered) {
                    drawHealthBar();
                }
            }
        }

        void drawHealthBar() {
            GameLib.setColor(Color.RED);
            GameLib.fillRect(GameLib.WIDTH / 2, 30, 200, 10);

            GameLib.setColor(Color.GREEN);
            double percentage = (double) health / maxHealth;
            GameLib.fillRect(GameLib.WIDTH / 2, 30, 200 * percentage, 10);

            GameLib.setColor(Color.WHITE);
            GameLib.drawLine(GameLib.WIDTH / 2 - 100, 25, GameLib.WIDTH / 2 + 100, 25);
            GameLib.drawLine(GameLib.WIDTH / 2 - 100, 35, GameLib.WIDTH / 2 + 100, 35);
        }

        void shoot(long currentTime, ArrayList<Entity> enemyProjectiles) {
            if (!entered || currentTime <= nextShot) return;

            if (type == 1) {
                // Chefe 1: tiro em leque
                double[] angles = { Math.PI/2 - Math.PI/4, Math.PI/2, Math.PI/2 + Math.PI/4 };
                for (double angle : angles) {
                    Entity proj = new Entity(x, y + radius, 2.0);
                    proj.vx = Math.cos(angle) * 0.25;
                    proj.vy = Math.sin(angle) * 0.25;
                    enemyProjectiles.add(proj);
                }
                nextShot = currentTime + 1000;
            } else {
                // Chefe 2: tiro circular
                for (int i = 0; i < 8; i++) {
                    double angle = (2 * Math.PI * i) / 8;
                    Entity proj = new Entity(x, y, 2.0);
                    proj.vx = Math.cos(angle) * 0.2;
                    proj.vy = Math.sin(angle) * 0.2;
                    enemyProjectiles.add(proj);
                }
                nextShot = currentTime + 2000;
            }
        }

        void takeDamage() {
            if (state == ACTIVE) {
                health--;
                if (health <= 0) {
                    explode(System.currentTimeMillis());
                    explosionEnd = System.currentTimeMillis() + 2000; // Explosão mais longa
                }
            }
        }
    }

    // Configuração de fase
    static class PhaseConfig {
        ArrayList<BossSpawn> bosses = new ArrayList<>();

        void addBoss(int type, int health, long when, double x, double y) {
            bosses.add(new BossSpawn(type, health, when, x, y));
        }

        static class BossSpawn {
            int type, health;
            long when;
            double x, y;

            BossSpawn(int type, int health, long when, double x, double y) {
                this.type = type;
                this.health = health;
                this.when = when;
                this.x = x;
                this.y = y;
            }
        }
    }

    public static void busyWait(long time) {
        while(System.currentTimeMillis() < time) Thread.yield();
    }

    public static void main(String[] args) {
        boolean running = true;
        long delta;
        long currentTime = System.currentTimeMillis();

        // Jogador
        Player player = new Player(GameLib.WIDTH / 2, GameLib.HEIGHT * 0.90, 3);

        // Listas de entidades
        ArrayList<Entity> playerProjectiles = new ArrayList<>();
        ArrayList<Entity> enemyProjectiles = new ArrayList<>();
        ArrayList<Boss> bosses = new ArrayList<>();

        // Sistema de fases
        int currentPhase = 0;
        long phaseStartTime = currentTime;
        boolean phaseComplete = false;

        // Configuração das fases
        ArrayList<PhaseConfig> phases = new ArrayList<>();

        // Fase 1
        PhaseConfig phase1 = new PhaseConfig();
        phase1.addBoss(1, 10, 5000, GameLib.WIDTH / 2, -30); // Chefe tipo 1 após 5 segundos
        phases.add(phase1);

        // Fase 2
        PhaseConfig phase2 = new PhaseConfig();
        phase2.addBoss(2, 15, 3000, GameLib.WIDTH / 2, -30); // Chefe tipo 2 após 3 segundos
        phases.add(phase2);

        // Background
        double[] bg1X = new double[20];
        double[] bg1Y = new double[20];
        double bg1Count = 0;

        for (int i = 0; i < bg1X.length; i++) {
            bg1X[i] = Math.random() * GameLib.WIDTH;
            bg1Y[i] = Math.random() * GameLib.HEIGHT;
        }

        GameLib.initGraphics();

        System.out.println("=== TESTE DE FASES E CHEFES ===");
        System.out.println("Fase 1: Chefe tipo 1 (vermelho, movimento lateral)");
        System.out.println("Fase 2: Chefe tipo 2 (rosa, movimento circular)");
        System.out.println("Use WASD para mover, CTRL para atirar, ESC para sair");

        while (running) {
            delta = System.currentTimeMillis() - currentTime;
            currentTime = System.currentTimeMillis();

            // Processar eventos da fase
            if (currentPhase < phases.size() && !phaseComplete) {
                PhaseConfig config = phases.get(currentPhase);
                long phaseTime = currentTime - phaseStartTime;

                Iterator<PhaseConfig.BossSpawn> it = config.bosses.iterator();
                while (it.hasNext()) {
                    PhaseConfig.BossSpawn spawn = it.next();
                    if (phaseTime >= spawn.when) {
                        bosses.add(new Boss(spawn.x, spawn.y, spawn.health, spawn.type));
                        System.out.println("Chefe tipo " + spawn.type + " apareceu! Vida: " + spawn.health);
                        it.remove();
                    }
                }
            }

            // Atualizar jogador
            player.update(delta, currentTime);

            if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
                player.shoot(currentTime, playerProjectiles);
            }

            // Atualizar projéteis do jogador
            Iterator<Entity> projIt = playerProjectiles.iterator();
            while (projIt.hasNext()) {
                Entity proj = projIt.next();
                proj.y += proj.vy * delta;
                if (proj.y < 0) {
                    projIt.remove();
                }
            }

            // Atualizar projéteis dos inimigos
            Iterator<Entity> enemyProjIt = enemyProjectiles.iterator();
            while (enemyProjIt.hasNext()) {
                Entity proj = enemyProjIt.next();
                proj.x += proj.vx * delta;
                proj.y += proj.vy * delta;
                if (proj.y > GameLib.HEIGHT || proj.x < 0 || proj.x > GameLib.WIDTH) {
                    enemyProjIt.remove();
                }
            }

            // Atualizar chefes
            for (Boss boss : bosses) {
                boss.update(delta, currentTime);
                boss.shoot(currentTime, enemyProjectiles);
            }

            // Verificar colisões
            if (player.state == ACTIVE) {
                // Jogador vs projéteis inimigos
                for (Entity proj : enemyProjectiles) {
                    if (player.isColliding(proj)) {
                        player.takeDamage();
                        proj.state = INACTIVE;
                        System.out.println("Jogador atingido! Vida restante: " + player.health);
                    }
                }

                // Jogador vs chefes
                for (Boss boss : bosses) {
                    if (player.isColliding(boss)) {
                        player.takeDamage();
                        System.out.println("Jogador colidiu com chefe! Vida restante: " + player.health);
                    }
                }
            }

            // Projéteis do jogador vs chefes
            for (Entity proj : playerProjectiles) {
                for (Boss boss : bosses) {
                    if (proj.isColliding(boss)) {
                        boss.takeDamage();
                        proj.state = INACTIVE;
                        System.out.println("Chefe atingido! Vida restante: " + boss.health);
                    }
                }
            }

            // Remover entidades inativas
            playerProjectiles.removeIf(p -> p.state == INACTIVE);
            enemyProjectiles.removeIf(p -> p.state == INACTIVE);

            // Verificar se fase foi completada
            if (!phaseComplete) {
                boolean allBossesDefeated = true;
                for (Boss boss : bosses) {
                    if (boss.state == ACTIVE || boss.state == EXPLODING) {
                        allBossesDefeated = false;
                        break;
                    }
                }

                if (allBossesDefeated && !bosses.isEmpty()) {
                    phaseComplete = true;
                    System.out.println("Fase " + (currentPhase + 1) + " completa!");

                    if (currentPhase + 1 < phases.size()) {
                        currentPhase++;
                        phaseStartTime = currentTime;
                        phaseComplete = false;
                        bosses.clear();
                        enemyProjectiles.clear();
                        System.out.println("Iniciando fase " + (currentPhase + 1));
                    } else {
                        System.out.println("Parabéns! Você completou todas as fases!");
                        running = false;
                    }
                }
            }

            // Verificar game over
            if (player.state == INACTIVE && player.health <= 0) {
                System.out.println("Game Over!");
                running = false;
            }

            if (GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) running = false;

            // Desenhar

            // Background
            GameLib.setColor(Color.GRAY);
            bg1Count += 0.07 * delta;
            for (int i = 0; i < bg1X.length; i++) {
                GameLib.fillRect(bg1X[i], (bg1Y[i] + bg1Count) % GameLib.HEIGHT, 3, 3);
            }

            // Jogador
            player.draw();

            // Projéteis do jogador
            GameLib.setColor(Color.GREEN);
            for (Entity proj : playerProjectiles) {
                GameLib.drawLine(proj.x, proj.y - 5, proj.x, proj.y + 5);
            }

            // Projéteis dos inimigos
            GameLib.setColor(Color.RED);
            for (Entity proj : enemyProjectiles) {
                GameLib.drawCircle(proj.x, proj.y, proj.radius);
            }

            // Chefes
            for (Boss boss : bosses) {
                boss.draw();
            }

            // HUD
            GameLib.setColor(Color.WHITE);
            for (int i = 0; i < player.health; i++) {
                GameLib.fillRect(20 + i * 15, GameLib.HEIGHT - 30, 10, 10);
            }

            // Indicador de fase
            GameLib.setColor(Color.YELLOW);
            GameLib.fillRect(GameLib.WIDTH - 100, 20, 80, 15);

            GameLib.display();
            busyWait(currentTime + 3);
        }

        System.exit(0);
    }
}