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

class FwdAction extends AbstractInputAction {
    private MyGame game;
	private Camera cam;
	private GameObject avatar;
	private PhysicsObject avatarP;
	private Vector3f loc;
	double[] transform = new double[16];
	//private Vector3f oldPosition, newPosition, oldLocation, newTranslation, fwdDirection3f, currU, currV, currN;
	//private Vector4f fwdDirection;
	//private Matrix3f rotation;
	public FwdAction(MyGame g) {
		game = g;
	}
	/*@Override
	public void performAction(float time, Event e) {
		avatar = game.getAvatar();
		//avatarP = game.getAvatarP();
		oldPosition = avatar.getLocalLocation();
		//avatarP.applyForce(0f, 0f, 1f, oldPosition.x(), oldPosition.y(), oldPosition.z);
		fwdDirection = new Vector4f(-1f, 0f, 0f, 1f);
		fwdDirection.mul(avatar.getWorldRotation());
		fwdDirection.mul(0.005f*(float)game.getTimeSinceLastFrame());
		newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
		avatar.setLocalLocation(newPosition);
		game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
	}*/
	@Override
	public void performAction(float time, Event e) {
		cam = game.getLeftCamera();
		avatar = game.getAvatar();
		avatarP = game.getAvatarP();
		loc = avatar.getLocalLocation();
		avatarP.applyForce(0f, 0f, 0.5f*(float)game.getTimeSinceLastFrame(), loc.x(), loc.y(), loc.z());
		game.getProtClient().sendMoveMessage(avatar.getWorldLocation());
	}
	/*@Override
	public void performAction(float time, Event e) {
		avatar = game.getAvatar();
		avatarP = game.getAvatarP();
		avatarP.getTransform()
	}*/
}