package a3;

import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class SwitchGameStateAction extends AbstractInputAction{
    
    private MyGame game;

    public SwitchGameStateAction(MyGame g) {
        game = g;
    }

    @Override
	public void performAction(float time, Event e) {
        if (game.getGameState() == 0) {
            game.setGameState(1);
        }
        else if (game.getGameState() == 1) {
            game.setGameState(2);
        }
	}
}
