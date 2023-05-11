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

class RightAction extends AbstractInputAction {
    private MyGame game;
	private Camera cam;
	private GameObject avatar;
	private PhysicsObject avatarP;
	private Vector3f loc;
	//private Vector3f oldPosition, newPosition, oldLocation, newTranslation, fwdDirection3f, currU, currV, currN;
	//private Vector4f rightDirection;
	//private Matrix3f rotation;
	public RightAction(MyGame g) {
		game = g;
	}
	/*@Override
	public void performAction(float time, Event e) {
		avatar = game.getAvatar();
		//avatarP = game.getAvatarP();
		oldPosition = avatar.getLocalLocation();
		//avatarP.applyForce(0f, 0f, 1f, oldPosition.x(), oldPosition.y(), oldPosition.z);
		rightDirection = new Vector4f(0f, 0f, -1f, 1f);
		rightDirection.mul(avatar.getWorldRotation());
		rightDirection.mul(0.005f*(float)game.getTimeSinceLastFrame());
		newPosition = oldPosition.add(rightDirection.x(), rightDirection.y(), rightDirection.z());
		avatar.setLocalLocation(newPosition);
		game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
	}*/
	@Override
	public void performAction(float time, Event e) {
		avatar = game.getAvatar();
		avatarP = game.getAvatarP();
		loc = avatar.getLocalLocation();
		avatarP.applyForce(0f, 0f, 1f*(float)game.getTimeSinceLastFrame(), loc.x(), loc.y(), loc.z());
	}
}