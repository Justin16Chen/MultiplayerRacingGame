package gameplay;

import java.util.ArrayList;

public class SpawnManager {

    private static int playerSpawnRadius = 100;

    public static ArrayList<Double[]> getSpawnPositions(int playerCount) {
        double angleStep = Math.toRadians(360 / playerCount);
        ArrayList<Double[]> spawnPositions = new ArrayList<Double[]>();
        for (int i=0; i<playerCount; i++) {
            Double[] position = {
                Math.cos(angleStep) * playerSpawnRadius,
                Math.sin(angleStep) * playerSpawnRadius
            };
            spawnPositions.add(position);
        }
        return spawnPositions;
    }
}
