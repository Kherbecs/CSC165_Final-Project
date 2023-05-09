package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class BackAction extends AbstractInputAction {
	private MyGame game;
	private Camera cam;
	private GameObject avatar;
	private Vector3f oldPosition, newPosition, fwdDirection3f, currU, currV, currN;
	private Matrix3f rotation;
	private Vector4f backDirection;
	public BackAction(MyGame g) {
		game = g;
	}
	@Override
	public void performAction(float time, Event e) {
		avatar = game.getDolphin();
		oldPosition = avatar.getLocalLocation();
		backDirection = new Vector4f(-1f, 0f, 0f, 1f);
		backDirection.mul(avatar.getWorldRotation());
		backDirection.mul(-0.005f*(float)game.getTimeSinceLastFrame());
		newPosition = oldPosition.add(backDirection.x(), backDirection.y(), backDirection.z());
		avatar.setLocalLocation(newPosition);
		game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
	}
}