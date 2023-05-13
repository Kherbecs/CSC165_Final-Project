package a3;

import java.util.UUID;

import tage.*;
import tage.shapes.AnimatedShape;

import org.joml.*;

// A ghost MUST be connected as a child of the root,
// so that it will be rendered, and for future removal.
// The ObjShape and TextureImage associated with the ghost
// must have already been created during loadShapes() and
// loadTextures(), before the game loop is started.

public class GhostAvatar extends GameObject
{
	UUID uuid;
	AnimatedShape s;

	public GhostAvatar(UUID id, AnimatedShape s, TextureImage t, Vector3f p) 
	{	super(GameObject.root(), s, t);
		uuid = id;
		setPosition(p);
		this.s = s;
	}
	
	public UUID getID() { return uuid; }
	public void setPosition(Vector3f m) { setLocalLocation(m); }
	public Vector3f getPosition() { return getWorldLocation(); }
	public AnimatedShape getAnimatedShape() { return this.s; }
}
