package a3;

import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class ToggleAvatarSelectAction extends AbstractInputAction {
    private MyGame game;
    public ToggleAvatarSelectAction(MyGame g) {
        game = g;
    }
    @Override
    public void performAction(float time, Event e) {
        if (game.getGameState() == 0) {
            if (game.getAvatarChosen() == 0 || game.getAvatarChosen() == 2) {
                game.setAvatarChosen(1);
                game.setPlayerAvatarTextureGreen();
                game.getAvatarPickSound().play();

            } else {
                game.setAvatarChosen(2);
                game.setPlayerAvatarTexturePurple();
                game.getAvatarPickSound().play();
            }
        }
    }
}
