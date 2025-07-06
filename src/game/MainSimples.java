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
        double velocidade; 
        boolean faseDoisAtivada = false;
        long nextPulseShot = 0; // <--- ADICIONE
        long pulseShotDelay = 2200;
        long shotDelay;

        Boss(double x, double y, int health, int type) {
            super(x, y, type == 1 ? 25.0 : 30.0);
            this.health = health;
            this.maxHealth = health;
            this.type = type;
            this.velocidade = (type == 1) ? 0.15 : 0.20; // Aumentando a vel. base do Chefe 2
            this.shotDelay = (type == 1) ? 1000 : 1200; // Chefe 2 já começa atirando mais rápido
        }

        void update(long delta, long currentTime) {
            if (state == EXPLODING) {
                if (currentTime > explosionEnd) {
                    state = INACTIVE;
                }
                return;
            }

            if (state == ACTIVE) {
                // --- INÍCIO DO NOVO BLOCO DE FASE 2 ---
                double vidaPercentual = (double) health / maxHealth;
                if (vidaPercentual <= 0.5 && !faseDoisAtivada) {
                    System.out.println("CHEFE DE TESTE ENTROU NA FASE 2!");
                    faseDoisAtivada = true;
                    this.velocidade *= 1.5; // Aumenta a velocidade em 50%
                    this.shotDelay *= 0.4; // Reduz o delay do tiro principal em 60%
                    this.pulseShotDelay *= 0.6; // Reduz o delay do novo ataque em 40%
                }
                // --- FIM DO NOVO BLOCO DE FASE 2 ---

                if (!entered) {
                    y += 0.1 * delta; // Velocidade de entrada pode ser mantida fixa
                    if (y >= 100) entered = true;
                } else {
                    // Movimento específico por tipo
                    if (type == 1) {
                        // Chefe tipo 1: movimento lateral
                        x += direction * this.velocidade * delta; // <--- MUDANÇA AQUI
                        if (x <= radius || x >= GameLib.WIDTH - radius) {
                            direction *= -1;
                        }
                    } else {
                        // Chefe tipo 2: movimento circular
                        // A velocidade aqui afeta a rotação
                        double angle = currentTime * 0.001 * (faseDoisAtivada ? 1.5 : 1.0);
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
            if (!entered) return;

            // A lógica do tiro principal é controlada pelo 'nextShot'
            if (currentTime > nextShot) {
                if (type == 1) {
                    // --- ATAQUE DO CHEFE 1 ---
                    double[] angulos;
                    if (faseDoisAtivada) {
                        // Leque maior na Fase 2
                        angulos = new double[]{ 
                            Math.PI/2 - Math.PI/4, Math.PI/2 - Math.PI/6, Math.PI/2 - Math.PI/12,
                            Math.PI/2
                        };
                    } else {
                        // Leque normal na Fase 1
                        angulos = new double[]{ Math.PI/2 - Math.PI/4, Math.PI/2, Math.PI/2 + Math.PI/4 };
                    }

                    for (double angle : angulos) {
                        Entity proj = new Entity(x, y + radius, 2.0);
                        proj.vx = Math.cos(angle) * 0.25;
                        proj.vy = Math.sin(angle) * 0.25;
                        enemyProjectiles.add(proj);
                    }
                } else { // type == 2
                    // --- ATAQUE CIRCULAR DO CHEFE 2 ---
                    int numProjeteis = faseDoisAtivada ? 12 : 8;
                    double velocidadeProj = faseDoisAtivada ? 0.25 : 0.2;

                    for (int i = 0; i < numProjeteis; i++) {
                        double angle = (2 * Math.PI * i) / numProjeteis;
                        Entity proj = new Entity(x, y, 2.0);
                        proj.vx = Math.cos(angle) * velocidadeProj;
                        proj.vy = Math.sin(angle) * velocidadeProj;
                        enemyProjectiles.add(proj);
                    }
                }
                
                // Reseta o timer do tiro principal para ambos os chefes
                nextShot = currentTime + this.shotDelay;
            }

            // --- LÓGICA DE ATAQUES ADICIONAIS (APENAS FASE 2, APENAS CHEFE 2) ---
            if (faseDoisAtivada && type == 2) {
                if (currentTime > nextPulseShot) {
                    // Lógica do "Pulso Duplo" que já implementamos...
                    // Anel externo lento
                    for (int i = 0; i < 12; i++) {
                        double angle = (2 * Math.PI * i) / 12;
                        Entity proj = new Entity(x, y, 3.0);
                        proj.vx = Math.cos(angle) * 0.18;
                        proj.vy = Math.sin(angle) * 0.18;
                        enemyProjectiles.add(proj);
                    }
                    // Anel interno rápido
                    for (int i = 0; i < 8; i++) {
                        double angle = (2 * Math.PI * i) / 8 + 0.2;
                        Entity proj = new Entity(x, y, 2.0);
                        proj.vx = Math.cos(angle) * 0.35;
                        proj.vy = Math.sin(angle) * 0.35;
                        enemyProjectiles.add(proj);
                    }
                    nextPulseShot = currentTime + this.pulseShotDelay;
                }
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

    public static void main(String[] args) {
        boolean running = true;

        // --- INICIALIZAÇÃO ---
        Player player = new Player(GameLib.WIDTH / 2, GameLib.HEIGHT * 0.90, 3);
        ArrayList<Entity> playerProjectiles = new ArrayList<>();
        ArrayList<Entity> enemyProjectiles = new ArrayList<>();
        ArrayList<Boss> bosses = new ArrayList<>();

        // --- CONFIGURAÇÃO DAS FASES ---
        int currentPhase = 0;
        long phaseStartTime = System.currentTimeMillis();
        boolean phaseComplete = false;
        ArrayList<PhaseConfig> phases = new ArrayList<>();

        // Fase 1
        PhaseConfig phase1 = new PhaseConfig();
        phase1.addBoss(1, 150, 5000, GameLib.WIDTH / 2, -30); // Vida 150
        phases.add(phase1);

        // Fase 2
        PhaseConfig phase2 = new PhaseConfig();
        phase2.addBoss(2, 250, 3000, GameLib.WIDTH / 2, -30); // Vida 250
        phases.add(phase2);

        // --- BACKGROUND ---
        double[] bg1X = new double[20];
        double[] bg1Y = new double[20];
        double bg1Count = 0;
        for (int i = 0; i < bg1X.length; i++) {
            bg1X[i] = Math.random() * GameLib.WIDTH;
            bg1Y[i] = Math.random() * GameLib.HEIGHT;
        }

        // --- INICIALIZAÇÃO GRÁFICA ---
        GameLib.initGraphics();
        System.out.println("=== TESTE DE FASES E CHEFES ===");
        System.out.println("Use as setas para mover, CTRL para atirar, ESC para sair");

        // --- CONTROLE DE TEMPO ---
        long lastUpdateTime = System.currentTimeMillis();

        // =================================================================================
        // --- LOOP PRINCIPAL DO JOGO (A ÚNICA VERSÃO CORRETA) ---
        // =================================================================================
        while (running) {
            long frameStartTime = System.currentTimeMillis();
            long delta = frameStartTime - lastUpdateTime;
            lastUpdateTime = frameStartTime;

            // --- LÓGICA DE ATUALIZAÇÃO ---

            // Spawn dos chefes
            if (currentPhase < phases.size() && !phaseComplete) {
                PhaseConfig config = phases.get(currentPhase);
                long phaseTime = frameStartTime - phaseStartTime;
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
            player.update(delta, frameStartTime);
            if (GameLib.iskeyPressed(GameLib.KEY_CONTROL)) {
                player.shoot(frameStartTime, playerProjectiles);
            }

            // Atualizar projéteis do jogador
            for (Iterator<Entity> it = playerProjectiles.iterator(); it.hasNext();) {
                Entity proj = it.next();
                proj.y += proj.vy * delta;
                if (proj.y < 0 || proj.state == INACTIVE) {
                    it.remove();
                }
            }

            // Atualizar projéteis dos inimigos
            for (Iterator<Entity> it = enemyProjectiles.iterator(); it.hasNext();) {
                Entity proj = it.next();
                proj.x += proj.vx * delta;
                proj.y += proj.vy * delta;
                if (proj.y > GameLib.HEIGHT || proj.x < 0 || proj.x > GameLib.WIDTH || proj.state == INACTIVE) {
                    it.remove();
                }
            }

            // Atualizar chefes
            for (Boss boss : bosses) {
                boss.update(delta, frameStartTime);
                boss.shoot(frameStartTime, enemyProjectiles);
            }

            // --- LÓGICA DE COLISÃO ---
            if (player.state == ACTIVE) {
                // Colisão Jogador vs Projéteis Inimigos
                for (Entity proj : enemyProjectiles) {
                    if (player.isColliding(proj)) {
                        player.takeDamage();
                        proj.state = INACTIVE;
                        System.out.println("Jogador atingido! Vida restante: " + player.health);
                    }
                }
                // Colisão Jogador vs Chefes
                for (Boss boss : bosses) {
                    if (player.isColliding(boss)) {
                        player.takeDamage();
                        System.out.println("Jogador colidiu com chefe! Vida restante: " + player.health);
                    }
                }
            }

            // Colisão Projéteis do Jogador vs Chefes
            for (Entity proj : playerProjectiles) {
                for (Boss boss : bosses) {
                    if (proj.isColliding(boss)) {
                        boss.takeDamage();
                        proj.state = INACTIVE;
                        System.out.println("Chefe atingido! Vida restante: " + boss.health);
                    }
                }
            }

            // --- LÓGICA DE ESTADO DO JOGO ---
            // Verificar se fase foi completada
            if (!phaseComplete && !bosses.isEmpty()) {
                boolean allBossesDefeated = true;
                for (Boss boss : bosses) {
                    if (boss.state != INACTIVE) {
                        allBossesDefeated = false;
                        break;
                    }
                }
                if (allBossesDefeated) {
                    phaseComplete = true;
                    System.out.println("Fase " + (currentPhase + 1) + " completa!");
                    if (currentPhase + 1 < phases.size()) {
                        currentPhase++;
                        phaseStartTime = frameStartTime + 3000; // Pausa de 3s antes da próxima fase
                        phaseComplete = false;
                        bosses.clear();
                        enemyProjectiles.clear();
                        System.out.println("Iniciando fase " + (currentPhase + 1) + " em 3 segundos...");
                    } else {
                        System.out.println("Parabéns! Você completou todas as fases!");
                        running = false;
                    }
                }
            }
            
            // Verificar game over e ESC
            if (player.state == INACTIVE && player.health <= 0) {
                System.out.println("Game Over!");
                running = false;
            }
            if (GameLib.iskeyPressed(GameLib.KEY_ESCAPE)) running = false;

            // --- LÓGICA DE DESENHO ---
            GameLib.setColor(Color.GRAY);
            bg1Count += 0.07 * delta;
            for (int i = 0; i < bg1X.length; i++) {
                GameLib.fillRect(bg1X[i], (bg1Y[i] + bg1Count) % GameLib.HEIGHT, 3, 3);
            }
            
            // Entidades
            player.draw();
            
            GameLib.setColor(Color.GREEN); // Cor definida ANTES do loop
            for (Entity proj : playerProjectiles) {
                GameLib.drawLine(proj.x, proj.y - 5, proj.x, proj.y + 5);
            }
            
            GameLib.setColor(Color.RED); // Cor definida ANTES do loop
            for (Entity proj : enemyProjectiles) {
                GameLib.drawCircle(proj.x, proj.y, proj.radius);
            }
            
            for (Boss boss : bosses) {
                boss.draw();
            }
            
            // HUD
            GameLib.setColor(Color.WHITE);
            for (int i = 0; i < player.health; i++) {
                GameLib.fillRect(20 + i * 15, GameLib.HEIGHT - 30, 10, 10);
            }
            
            GameLib.setColor(Color.YELLOW);
            GameLib.fillRect(GameLib.WIDTH - 100, 20, 80, 15);
            
            GameLib.display();
            
            // --- CONTROLE DE TEMPO ROBUSTO ---
            long frameEndTime = System.currentTimeMillis();
            long frameDuration = frameEndTime - frameStartTime;
            long sleepTime = 16 - frameDuration; // 16ms = ~60 FPS

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.err.println("Thread de jogo interrompida.");
                }
            }
        }
        System.exit(0);
    }
}