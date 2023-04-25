package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class ToggleWorldAxis extends AbstractInputAction {
    private MyGame game;
    private Camera cam;
    private GameObject x, y, z;
    public ToggleWorldAxis(MyGame g) {
        game = g;
    }
    @Override
    public void performAction(float time, Event e) {
        x = game.getXAxisObject();
        y = game.getYAxisObject();
        z = game.getZAxisObject();
        if (x.getRenderStates().renderingEnabled()) {
            x.getRenderStates().disableRendering();
            y.getRenderStates().disableRendering();
            z.getRenderStates().disableRendering();
        } else {
            x.getRenderStates().enableRendering();
            y.getRenderStates().enableRendering();
            z.getRenderStates().enableRendering();
        }
    }
}