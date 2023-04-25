package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class ZoomCameraIn extends AbstractInputAction {
    private MyGame game;
    private Camera cam;
    private Vector3f oldPosition, newPosition, direction, defaultLoc;
    public ZoomCameraIn(MyGame g) {
        game = g;
    }
    @Override
    public void performAction(float time, Event e) {
        cam = game.getRightCamera();
        oldPosition = cam.getLocation();
        direction = (new Vector3f(0f, 1f, 0f));
        direction.mul(-0.005f*(float)game.getTimeSinceLastFrame());
        newPosition = oldPosition.add(direction.x(), direction.y(), direction.z());
        cam.setLocation(newPosition);
        if (cam.getLocation().y < 0) {
            Vector3f defaultLoc = new Vector3f(cam.getLocation().x, 1f, cam.getLocation().z);
            cam.setLocation(defaultLoc);
        }
    }
}