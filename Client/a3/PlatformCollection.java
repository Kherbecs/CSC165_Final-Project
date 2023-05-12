package a3;

import tage.*;
import java.util.ArrayList;
import java.util.List;

class PlatformCollection {
    final int NUMBER_OF_PLATFORMS = 10;
    private final List<Platform> platforms = new ArrayList<>();

    public PlatformCollection(GameObject root, ObjShape platformS) {
        for (int i = 0; i < NUMBER_OF_PLATFORMS; i++) {
            platforms.add(new Platform(root, platformS));
        }
    }
    public List<Platform> getPlatformList() {
        return this.platforms;
    }
}
