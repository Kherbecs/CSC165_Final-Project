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

class MoveActionController extends AbstractInputAction {
	private MyGame game;
	private GameObject avatar;
	private PhysicsObject avatarP;
	private Vector3f loc;
	public MoveActionController(MyGame g) {
		game = g;
	}
	@Override
	public void performAction(float time, Event e) {
		float keyValue = e.getValue();
		if (keyValue > -.2 && keyValue < .2) return;  // deadzone
		if (keyValue < -.2) {
			avatar = game.getAvatar();
			avatarP = game.getAvatarP();
			loc = avatar.getLocalLocation();
			avatarP.applyForce(0f, 0f, 0.5f*(float)game.getTimeSinceLastFrame(), loc.x(), loc.y(), loc.z());
			game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
		}
		else if (keyValue > .2) {
			avatar = game.getAvatar();
			avatarP = game.getAvatarP();
			loc = avatar.getLocalLocation();
			avatarP.applyForce(0f, 0f, -0.5f*(float)game.getTimeSinceLastFrame(), loc.x(), loc.y(), loc.z());
			game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
		}
	}
}