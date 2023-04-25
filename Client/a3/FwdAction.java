package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class FwdAction extends AbstractInputAction {
    private MyGame game;
	private Camera cam;
	private GameObject avatar;
	private Vector3f oldPosition, newPosition, oldLocation, newTranslation, fwdDirection3f, currU, currV, currN;
	private Vector4f fwdDirection;
	private Matrix3f rotation;
	public FwdAction(MyGame g) {
		game = g;
	}
	@Override
	public void performAction(float time, Event e) {
		avatar = game.getDolphin();
		oldPosition = avatar.getLocalLocation();
		fwdDirection = new Vector4f(0f, 0f, 1f, 1f);
		fwdDirection.mul(avatar.getWorldRotation());
		fwdDirection.mul(0.005f*(float)game.getTimeSinceLastFrame());
		newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
		avatar.setLocalLocation(newPosition);
		game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
	}
}