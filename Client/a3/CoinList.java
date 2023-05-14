package a3;

import tage.*;
import java.util.ArrayList;
import java.util.List;

class CoinList {
    final int NUMBER_OF_COINS = 10;
    private final List<Coin> coins = new ArrayList<>();

    public CoinList(GameObject root, ObjShape coinS, TextureImage coinX) {
        for (int i = 0; i < NUMBER_OF_COINS; i++) {
            coins.add(new Coin(root, coinS, coinX));
        }
    }
    public List<Coin> getCoinList() {
        return this.coins;
    }
}
