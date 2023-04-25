package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class MoveActionController extends AbstractInputAction {
	private MyGame game;
	private GameObject dol;
	private Camera cam;
	private Vector3f oldPosition, newPosition, currU, currV, currN, fwdDirection3f;
	private Matrix3f rotation;
	private Vector4f fwdDirection, backDirection;
	public MoveActionController(MyGame g) {
		game = g;
	}
	@Override
	public void performAction(float time, Event e) {
		float keyValue = e.getValue();
		if (keyValue > -.2 && keyValue < .2) return;  // deadzone
		if (keyValue < -.2) {
			dol = game.getDolphin();
			oldPosition = dol.getWorldLocation();
			fwdDirection = new Vector4f(0f, 0f, 1f, 1f);
			fwdDirection.mul(dol.getWorldRotation());
			fwdDirection.mul(0.005f*(float)game.getTimeSinceLastFrame());
			newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
			dol.setLocalLocation(newPosition);
		}
		else if (keyValue > .2) {
			dol = game.getDolphin();
			oldPosition = dol.getLocalLocation();
			backDirection = new Vector4f(0f, 0f, 1f, 1f);
			backDirection.mul(dol.getWorldRotation());
			backDirection.mul(-0.001f*(float)game.getTimeSinceLastFrame());
			newPosition = oldPosition.add(backDirection.x(), backDirection.y(), backDirection.z());
			dol.setLocalLocation(newPosition);
		}
	}
}