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

class JumpAction extends AbstractInputAction {
    private MyGame game;
	private Camera cam;
	private GameObject avatar;
	private PhysicsObject avatarP;
	private Vector3f loc;
    public JumpAction(MyGame g) {
        game = g;
    }
    @Override
	public void performAction(float time, Event e) {
		avatar = game.getAvatar();
		avatarP = game.getAvatarP();
		loc = avatar.getLocalLocation();
		avatarP.applyForce(0f, 100f*(float)game.getTimeSinceLastFrame(), 0f, loc.x(), loc.y(), loc.z());
	}
}
