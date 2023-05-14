package a3;

import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class ToggleLights extends AbstractInputAction {
    private MyGame game;
    private boolean on = true;
    private Light light1;
    private Light light2;
    public ToggleLights(MyGame g) {
        game = g;
    }
    @Override
    public void performAction(float time, Event e) {
        if (on) {
            light1 = game.getSpotlight1();
            light2 = game.getSpotLight2();
            light1.setLocation(new Vector3f(0, -10, 0));
            light2.setLocation(new Vector3f(0, -10, 0));
            light1.setDirection(new Vector3f(0f, -1f, 0f));
            light2.setDirection(new Vector3f(0f, -1f, 0f));
            on = false;
        } else if (!on) {
            light1 = game.getSpotlight1();
            light2 = game.getSpotLight2();
            light1.setLocation(new Vector3f(18, 50, 100));
            light2.setLocation(new Vector3f(-18, 50, 100));
            light1.setDirection(new Vector3f(0f, 0f, -1f));
            light2.setDirection(new Vector3f(0f, 0f, -1f));
            on = true;
        }

    }
}
