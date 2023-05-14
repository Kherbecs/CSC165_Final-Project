package a3;

import tage.*;

class Coin {
    GameObject coin;
    public Coin(GameObject root, ObjShape coinS, TextureImage coinX) {
        coin = new GameObject(root, coinS, coinX);
    }
    public Coin getCoin() {
        return this;
    }
    public GameObject getCoinObj() {
        return this.coin;
    }
}
