package Oleadas;

import java.util.*;
import globos.Bloon;
import GamePane.GamePanel;

public class WaveManager {

    private GamePanel game;
    private Queue<SpawnEvent> spawnQueue = new LinkedList<>();
    private long lastSpawn = 0;
    private boolean waveActive = false;

    private static class SpawnEvent {

        Bloon.Type type;
        long delay;

        SpawnEvent(Bloon.Type type, long delay) {
            this.type = type;
            this.delay = delay;
        }
    }

    public WaveManager(GamePanel game) {
        this.game = game;
    }

    public void startWave(int round) {
        spawnQueue.clear();
        generateWave(round);
        waveActive = true;
        lastSpawn = System.currentTimeMillis();
    }

    private void generateWave(int round) {
        if (round <= 3) {
            for (int i = 0; i < round * 5 + 5; i++) {
                spawnQueue.add(new SpawnEvent(Bloon.Type.RED, 600));
            }
            for (int i = 0; i < round * 2; i++) {
                spawnQueue.add(new SpawnEvent(Bloon.Type.BLUE, 500));
            }
        } else if (round <= 8) {
            for (int i = 0; i < round * 3; i++) {
                spawnQueue.add(new SpawnEvent(Bloon.Type.BLUE, 400));
                spawnQueue.add(new SpawnEvent(Bloon.Type.GREEN, 450));
            }
        } else {
            for (int i = 0; i < round; i++) {
                spawnQueue.add(new SpawnEvent(Bloon.Type.YELLOW, 350));
                spawnQueue.add(new SpawnEvent(Bloon.Type.GREEN, 300));
            }
        }
    }

    public void update() {
        if (!waveActive) {
            return;
        }

        if (spawnQueue.isEmpty()) {
            if (game.getBloons().isEmpty()) {
                waveActive = false;
            }
            return;
        }

        long now = System.currentTimeMillis();
        SpawnEvent nextEvent = spawnQueue.peek();

        if (now - lastSpawn >= nextEvent.delay) {
            spawnQueue.poll();
            Bloon newBloon = new Bloon(nextEvent.type, null);
            game.spawnBloon(newBloon);
            lastSpawn = now;
        }
    }

    public boolean isWaveActive() {
        return waveActive;
    }
}
