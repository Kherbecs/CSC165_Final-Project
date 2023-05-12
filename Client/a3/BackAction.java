package a3;
import tage.*;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;
import tage.physics.PhysicsEngine;
import tage.physics.PhysicsObject;
import tage.physics.PhysicsEngineFactory;
import tage.physics.JBullet.*;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.collision.dispatch.CollisionObject;

class BackAction extends AbstractInputAction {
	private MyGame game;
	private Camera cam;
	private GameObject avatar;
	private PhysicsObject avatarP;
	private Vector3f loc;
	//private Vector3f oldPosition, newPosition, fwdDirection3f, currU, currV, currN;
	//private Matrix3f rotation;
	//private Vector4f backDirection;
	public BackAction(MyGame g) {
		game = g;
	}
	/*@Override
	public void performAction(float time, Event e) {
		avatar = game.getAvatar();
		oldPosition = avatar.getLocalLocation();
		backDirection = new Vector4f(-1f, 0f, 0f, 1f);
		backDirection.mul(avatar.getWorldRotation());
		backDirection.mul(-0.005f*(float)game.getTimeSinceLastFrame());
		newPosition = oldPosition.add(backDirection.x(), backDirection.y(), backDirection.z());
		avatar.setLocalLocation(newPosition);
		game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
	}*/
	@Override
	public void performAction(float time, Event e) {
		avatar = game.getAvatar();
		avatarP = game.getAvatarP();
		loc = avatar.getLocalLocation();
		avatarP.applyForce(0f, 0f, -1f*(float)game.getTimeSinceLastFrame(), loc.x(), loc.y(), loc.z());
		game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
	}
}