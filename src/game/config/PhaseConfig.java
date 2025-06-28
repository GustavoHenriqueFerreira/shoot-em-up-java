package game.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhaseConfig {

    private List<EnemySpawn> enemySpawns;
    private BossSpawn bossSpawn;
    private List<PowerUpSpawn> powerUpSpawns;

    public PhaseConfig(String phaseFile) throws IOException {
        enemySpawns = new ArrayList<>();
        powerUpSpawns = new ArrayList<>();
        readPhaseFile(phaseFile);
    }

    private void readPhaseFile(String phaseFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(phaseFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String type = parts[0];

                if (type.equals("ENEMY")) {
                    int enemyType = Integer.parseInt(parts[1]);
                    long when = Long.parseLong(parts[2]);
                    double x = Double.parseDouble(parts[3]);
                    double y = Double.parseDouble(parts[4]);
                    enemySpawns.add(new EnemySpawn(enemyType, when, x, y));
                } else if (type.equals("BOSS")) {
                    int bossType = Integer.parseInt(parts[1]);
                    int hp = Integer.parseInt(parts[2]);
                    long when = Long.parseLong(parts[3]);
                    double x = Double.parseDouble(parts[4]);
                    double y = Double.parseDouble(parts[5]);
                    bossSpawn = new BossSpawn(bossType, hp, when, x, y);
                } else if (type.equals("POWERUP")) {
                    int powerUpType = Integer.parseInt(parts[1]);
                    long when = Long.parseLong(parts[2]);
                    double x = Double.parseDouble(parts[3]);
                    double y = Double.parseDouble(parts[4]);
                    powerUpSpawns.add(new PowerUpSpawn(powerUpType, when, x, y));
                }
            }
        }
    }

    public List<EnemySpawn> getEnemySpawns() {
        return enemySpawns;
    }

    public BossSpawn getBossSpawn() {
        return bossSpawn;
    }

    public List<PowerUpSpawn> getPowerUpSpawns() {
        return powerUpSpawns;
    }

    public static class EnemySpawn {
        public int type;
        public long when;
        public double x;
        public double y;

        public EnemySpawn(int type, long when, double x, double y) {
            this.type = type;
            this.when = when;
            this.x = x;
            this.y = y;
        }
    }

    public static class BossSpawn {
        public int type;
        public int hp;
        public long when;
        public double x;
        public double y;

        public BossSpawn(int type, int hp, long when, double x, double y) {
            this.type = type;
            this.hp = hp;
            this.when = when;
            this.x = x;
            this.y = y;
        }
    }

    public static class PowerUpSpawn {
        public int type;
        public long when;
        public double x;
        public double y;

        public PowerUpSpawn(int type, long when, double x, double y) {
            this.type = type;
            this.when = when;
            this.x = x;
            this.y = y;
        }
    }
}


