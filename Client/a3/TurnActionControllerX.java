package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class TurnActionControllerX extends AbstractInputAction {
	private MyGame game;
	private GameObject dol;
	private Matrix4f newRotation;
	private float rollAmount = .001f;
	private Camera cam;
	private Vector3f fwd, up, right;
	public TurnActionControllerX(MyGame g) {
		game = g;
	}
	@Override 
  	public void performAction(float time, Event e) { 
		float keyValue = e.getValue();
   		if (keyValue > -.2 && keyValue < .2) return;  // deadzone
		if (keyValue < -.2) {
			dol = game.getDolphin();
			dol.setLocalRotation(dol.yawObject(rollAmount*(float)game.getTimeSinceLastFrame(), newRotation));
		}
		else if (keyValue > .2) {
			dol = game.getDolphin();
			dol.setLocalRotation(dol.yawObject(-rollAmount*(float)game.getTimeSinceLastFrame(), newRotation));
		}
	}
}