package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

class RollActionLeft extends AbstractInputAction {
	private MyGame game;
	private GameObject dol;
	private Matrix4f newRotation;
	private Matrix3f rotation;
	private Vector3f currU, currV, currN;
	private Vector3f fwd, up, right;
	private Camera cam;
	private float rollAmount = -.001f;
	public RollActionLeft(MyGame g) {
		game = g;
	}
	@Override
	public void performAction(float time, Event e) {
		dol = game.getAvatar();
		dol.setLocalRotation(dol.rollObject(rollAmount*(float)game.getTimeSinceLastFrame(), newRotation));
	}
}