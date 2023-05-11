package a3;

import tage.*;

class Platform {
    GameObject platform;
    public Platform(GameObject root, ObjShape platformS) {
        platform = new GameObject(root, platformS);
    }
    public Platform getPlatform() {
        return this;
    }
    public GameObject getPlatformObj() {
        return this.platform;
    }
}
