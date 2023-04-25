package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class TurnActionRight extends AbstractInputAction {
	private MyGame game;
	private GameObject avatar;
	private Matrix4f newRotation;
	private float rollAmount = -.001f;
	private Vector3f currU, currV, currN;
	private Matrix3f rotation;
	private Camera cam;
	private Vector3f fwd, up, right;
	public TurnActionRight(MyGame g) {
		game = g;
	}
	@Override
	public void performAction(float time, Event e) {
		avatar = game.getDolphin();
		avatar.setLocalRotation(avatar.yawObject(rollAmount*(float)game.getTimeSinceLastFrame(), newRotation));
		//game.getProtClient().sendMoveMessage(avatar.getWorldRotation());
	}
}