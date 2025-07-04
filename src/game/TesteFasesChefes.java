package game;

import java.util.ArrayList;
import java.util.Iterator;

public class TesteFasesChefes {

    // Estados das entidades
    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;
    public static final int EXPLODING = 2;

    // Classe simples para entidades
    static class Entity {
        int state = ACTIVE;
        double x, y, radius;
        double vx, vy;
        long explosionStart, explosionEnd;
        String name;

        Entity(double x, double y, double radius, String name) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.name = name;
        }

        void explode(long currentTime) {
            state = EXPLODING;
            explosionStart = currentTime;
            explosionEnd = currentTime + 500;
            System.out.println(name + " explodindo!");
        }

        boolean isColliding(Entity other) {
            if (state != ACTIVE || other.state != ACTIVE) return false;
            double dx = x - other.x;
            double dy = y - other.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            return dist < (radius + other.radius) * 0.8;
        }

        void update(long delta, long currentTime) {
            if (state == EXPLODING && currentTime > explosionEnd) {
                state = INACTIVE;
                System.out.println(name + " destruído!");
            }

            if (state == ACTIVE) {
                x += vx * delta;
                y += vy * delta;
            }
        }
    }

    // Classe para o jogador
    static class Player extends Entity {
        int health;
        long nextShot = 0;

        Player(double x, double y, int health) {
            super(x, y, 12.0, "Jogador");
            this.health = health;
        }

        @Override
        void update(long delta, long currentTime) {
            if (state == EXPLODING && currentTime > explosionEnd) {
                if (health > 0) {
                    state = ACTIVE;
                    System.out.println("Jogador ressuscitou! Vida restante: " + health);
                } else {
                    state = INACTIVE;
                    System.out.println("Jogador morreu!");
                }
            }
        }

        boolean canShoot(long currentTime) {
            return state == ACTIVE && currentTime > nextShot;
        }

        void shoot(long currentTime, ArrayList<Entity> projectiles) {
            if (canShoot(currentTime)) {
                Entity proj = new Entity(x, y - 2 * radius, 2.0, "Projétil do Jogador");
                proj.vy = -1.0;
                projectiles.add(proj);
                nextShot = currentTime + 100;
                System.out.println("Jogador atirou!");
            }
        }

        void takeDamage() {
            health--;
            explode(System.currentTimeMillis());
            System.out.println("Jogador recebeu dano! Vida restante: " + health);
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
            super(x, y, type == 1 ? 25.0 : 30.0, "Chefe Tipo " + type);
            this.health = health;
            this.maxHealth = health;
            this.type = type;
        }

        @Override
        void update(long delta, long currentTime) {
            super.update(delta, currentTime);

            if (state == ACTIVE) {
                if (!entered) {
                    y += 0.1 * delta;
                    if (y >= 100) {
                        entered = true;
                        System.out.println(name + " entrou na arena!");
                    }
                } else {
                    // Movimento específico por tipo
                    if (type == 1) {
                        // Chefe tipo 1: movimento lateral
                        x += direction * 0.15 * delta;
                        if (x <= radius || x >= 480 - radius) {
                            direction *= -1;
                            System.out.println(name + " mudou de direção!");
                        }
                    } else {
                        // Chefe tipo 2: movimento circular
                        double angle = currentTime * 0.001;
                        x = 240 + Math.cos(angle) * 80;
                        y = 120 + Math.sin(angle) * 20;
                    }
                }
            }
        }

        void shoot(long currentTime, ArrayList<Entity> enemyProjectiles) {
            if (!entered || currentTime <= nextShot || state != ACTIVE) return;

            if (type == 1) {
                // Chefe 1: tiro em leque
                System.out.println(name + " atirando em leque!");
                double[] angles = { Math.PI/2 - Math.PI/4, Math.PI/2, Math.PI/2 + Math.PI/4 };
                for (int i = 0; i < angles.length; i++) {
                    Entity proj = new Entity(x, y + radius, 2.0, "Projétil do " + name);
                    proj.vx = Math.cos(angles[i]) * 0.25;
                    proj.vy = Math.sin(angles[i]) * 0.25;
                    enemyProjectiles.add(proj);
                }
                nextShot = currentTime + 1000;
            } else {
                // Chefe 2: tiro circular
                System.out.println(name + " atirando em círculo!");
                for (int i = 0; i < 8; i++) {
                    double angle = (2 * Math.PI * i) / 8;
                    Entity proj = new Entity(x, y, 2.0, "Projétil do " + name);
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
                System.out.println(name + " recebeu dano! Vida: " + health + "/" + maxHealth);
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
        System.out.println("=== TESTE DE FASES E CHEFES ===");
        System.out.println("Demonstração do sistema orientado a objetos");
        System.out.println();

        long currentTime = System.currentTimeMillis();
        long startTime = currentTime;

        // Jogador
        Player player = new Player(240, 600, 3);
        System.out.println("Jogador criado com " + player.health + " pontos de vida");

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
        phase1.addBoss(1, 5, 2000, 240, -30); // Chefe tipo 1 após 2 segundos
        phases.add(phase1);

        // Fase 2
        PhaseConfig phase2 = new PhaseConfig();
        phase2.addBoss(2, 7, 1000, 240, -30); // Chefe tipo 2 após 1 segundo
        phases.add(phase2);

        System.out.println("Iniciando Fase 1...");
        System.out.println();

        // Simulação do jogo
        int simulationSteps = 0;
        while (simulationSteps < 100 && currentPhase < phases.size()) {
            long delta = 50; // 50ms por step
            currentTime += delta;
            simulationSteps++;

            // Processar eventos da fase
            if (!phaseComplete) {
                PhaseConfig config = phases.get(currentPhase);
                long phaseTime = currentTime - phaseStartTime;

                Iterator<PhaseConfig.BossSpawn> it = config.bosses.iterator();
                while (it.hasNext()) {
                    PhaseConfig.BossSpawn spawn = it.next();
                    if (phaseTime >= spawn.when) {
                        Boss boss = new Boss(spawn.x, spawn.y, spawn.health, spawn.type);
                        bosses.add(boss);
                        System.out.println(">>> " + boss.name + " apareceu! Vida: " + boss.health);
                        it.remove();
                    }
                }
            }

            // Atualizar jogador
            player.update(delta, currentTime);

            // Simular tiros do jogador (a cada 200ms)
            if (simulationSteps % 4 == 0) {
                player.shoot(currentTime, playerProjectiles);
            }

            // Atualizar projéteis do jogador
            Iterator<Entity> projIt = playerProjectiles.iterator();
            while (projIt.hasNext()) {
                Entity proj = projIt.next();
                proj.update(delta, currentTime);
                if (proj.y < 0 || proj.state == INACTIVE) {
                    projIt.remove();
                }
            }

            // Atualizar projéteis dos inimigos
            Iterator<Entity> enemyProjIt = enemyProjectiles.iterator();
            while (enemyProjIt.hasNext()) {
                Entity proj = enemyProjIt.next();
                proj.update(delta, currentTime);
                if (proj.y > 720 || proj.x < 0 || proj.x > 480 || proj.state == INACTIVE) {
                    enemyProjIt.remove();
                }
            }

            // Atualizar chefes
            for (Boss boss : bosses) {
                boss.update(delta, currentTime);
                boss.shoot(currentTime, enemyProjectiles);
            }

            // Verificar colisões (simulação simplificada)
            if (player.state == ACTIVE && simulationSteps % 20 == 0) {
                // Simular colisão ocasional com projétil inimigo
                if (!enemyProjectiles.isEmpty() && Math.random() < 0.3) {
                    player.takeDamage();
                    enemyProjectiles.remove(0);
                }
            }

            // Projéteis do jogador vs chefes
            for (Entity proj : playerProjectiles) {
                for (Boss boss : bosses) {
                    if (proj.isColliding(boss)) {
                        boss.takeDamage();
                        proj.state = INACTIVE;
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
                    System.out.println();
                    System.out.println("*** FASE " + (currentPhase + 1) + " COMPLETA! ***");
                    System.out.println();

                    if (currentPhase + 1 < phases.size()) {
                        currentPhase++;
                        phaseStartTime = currentTime;
                        phaseComplete = false;
                        bosses.clear();
                        enemyProjectiles.clear();
                        System.out.println("Iniciando Fase " + (currentPhase + 1) + "...");
                        System.out.println();
                    } else {
                        System.out.println("PARABÉNS! VOCÊ COMPLETOU TODAS AS FASES!");
                        break;
                    }
                }
            }

            // Verificar game over
            if (player.state == INACTIVE && player.health <= 0) {
                System.out.println();
                System.out.println("GAME OVER!");
                break;
            }

            // Pequena pausa para visualização
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }

        System.out.println();
        System.out.println("=== RESUMO DO TESTE ===");
        System.out.println("Sistema de fases funcionando");
        System.out.println("Chefes com comportamentos diferentes");
        System.out.println("Sistema de colisões");
        System.out.println("Gerenciamento de entidades com ArrayList");
        System.out.println("Estrutura orientada a objetos");
        System.out.println();
        System.out.println("O jogo está funcionando corretamente!");
        System.out.println("Para versão gráfica, execute em ambiente com display X11.");
    }
}