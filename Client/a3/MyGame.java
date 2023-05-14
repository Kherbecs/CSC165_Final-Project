/* REQUIREMENTS
 * EXTERNAL MODELS (MED PRIORITY): Create walking animation(s) for player, create some kind of animation for creature. Maybe the nose can move around, and/or the body can expand in and out as it breathes?
 * For the creature, fix the faces, make the texture look less random. Maybe make the creature a little more complex of a model. The ghost avatar will just be a differently textured player
 * unless we have more time.
 * NETWORKED MULTIPLAYER (LOW PRIORITY): Currently functional, players will only be going forward so no need for a rotation message (add if more time). Modify protocol to send animation updates.
 * May need further modification when physics are implemented.
 * SCRIPTING (LOW PRIORITY): Do more initialization with scripting. Maybe some in-game parameters can be put to a script. 
 * SKYBOX AND TERRAIN (LOW PRIORITY): Get physics to work with height map. Otherwise, done.
 * LIGHTS (LOW PRIORITY): Need 2, technically done, but not effective use. Currently a spotlight1 on player spawn (1), probably going to try and add a spotlight1 to player that can be
 * toggled on and off (2).
 * HUD (LOW PRIORITY): There will be a string displayed top or bottom left that reads "Score: (integer score_variable)". At the moment, score will be gained when a player lands on a new platform
 * (is completing the platformer). Shouldn't be difficult to implement.
 * 3D SOUND (MED PRIORITY): We have background sound, but need some action-specific game sounds. Thinking one platform can have a bush that rustles when the player is near it.
 * The player themselves can make sounds maybe? The creature can also have some kind of sound (either some kind of breathing or vocalization when near a player).
 * HIERARCHICAL SCENEGRAPH (MED PRIORITY): As the player progresses, they will pick up items that boost their score. This items will hover around the player.
 * ANIMATION (MED PRIORITY): Create animations for when player moves in the 4 main directions. Create diagonal animations if time. Create jumping animation if time.
 * NPCS (HIGH PRIORITY): Add behavior for the creature so that it attemps to collide with a player. If it does, deduct player's score. Unsure how complex this pathfinding has to be.
 * PHYSICS (HIGH PRIORITY): Current plan is that player will be controlled by physics. Since they cannot rotate, should be simple to push them in certain directions.
 * Gravity should do most of the work when it comes to finding a way for the player to jump (push upwards, grav does its thing?). Platforms will be static physics objects that the
 * player physics object can collide with. Hopefully we can implement this without a ton of difficulty.
 * 
 * PLEASE DON'T DELETE THIS TOP PART. It will be our guide for now. It would be helpful to document major changes we make, can add below.
 *  
 */
package a3;

import tage.*;
import tage.nodeControllers.*;
import tage.Light.LightType;
import tage.shapes.*;
import tage.input.*;
import tage.input.action.*;

import java.lang.Math;
import java.awt.*;

import java.awt.event.*;

import java.io.*;
import java.util.*;
import java.util.UUID;
import java.net.InetAddress;

import java.net.UnknownHostException;

import org.joml.*;

// PHYSICS ENGINE //
import tage.physics.PhysicsEngine;
import tage.physics.PhysicsObject;
import tage.physics.PhysicsEngineFactory;
import tage.physics.JBullet.*;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.collision.dispatch.CollisionObject;

// AUDIO ENGINE //
import tage.audio.*;

import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;
import tage.input.action.*;
import tage.networking.IGameConnection.ProtocolType;
import javax.script.*;

public class MyGame extends VariableFrameRateGame {

	private static Engine engine;
	private InputManager im;
	private GhostManager gm;

	// 0 - choose avatar, 1 - "lobby", 2 - mid-game/running, 3 - victory screen?
	private int gameState = 0;
	private int avatarChosen = 0;
	private boolean isFlying = false;
	private int coinCounter = 0;
	private Vector3f currentPosition, loc, fwd, up, right;
	private Matrix4f initialTranslation, initialRotation, initialScale;
	private double startTime, elapsedTime, amt, timeSinceLastFrame, lastFrameTime, currFrameTime;
	private double prevTime = 0;
	private float distanceBetween;

	private Vector3f globalXAxis = new Vector3f(1f, 0f, 0f);
	private Vector3f globalYAxis = new Vector3f(0f, 1f, 0f);
	private Vector3f globalZAxis = new Vector3f(0f, 0f, 1f);
	private GameObject avatar, x, y, z, platform;
	private ObjShape linxS, linyS, linzS;
	private AnimatedShape ghostS;
	private TextureImage ghostT, creatureX;
	private Light lobbyLight, spotlight1, spotlight2;
	private int fluffyClouds, lakeIslands, nightSky;

	// SIMPLE CHARACTER
	private GameObject simpleCharacter, menuAvatar1, menuAvatar2;
	private TextureImage simpleCharX, menuAvatar1X, menuAvatar2X;
	private AnimatedShape simpleCharS;

	// CREATURE VARIABLES
	private GameObject creature, creatureInvis;
	private TextureImage creaturetx;
	private ObjShape creatureS;

	// BUSH
	private GameObject bush;
	private TextureImage bushtX;
	private ObjShape bushS;

	// PHYSICS
	private PhysicsEngine physicsEngine;
	private PhysicsObject creatureP, terrainP, avatarP;
	private boolean running = true;
	private float creatureVals[] = new float[16];
	private float avatarVals[] = new float[16];
	private float terrainVals[] = new float[16];

	// AUDIO
	private IAudioManager audioMgr;
	private Sound backgroundMusic, creatureSound, avatarPickSound;

	// TERRAIN
	private GameObject terrain;
	private GameObject ground;
	private ObjShape groundS;
	private ObjShape terrainS;
	private TextureImage hills, grass;

	// VIEWPORTS AND CAMERAS
	private Viewport leftVP;
	private Viewport rightVP;
	private Camera leftCamera;
	private Camera rightCamera;
	private CameraOrbit3D orbitController;
	private Vector3f initLeftCamLoc = new Vector3f(0f, 50f, -150f);

	// SCRIPT
	private File scriptFile1, scriptFileLoadShapes, scriptFileBuildObjects;
	private long fileLastModifiedTime = 0;
	ScriptEngine jsEngine;

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;

	// HUD
	Vector3f hud1Color = new Vector3f(1f, 0, 0);

	// PLATFORMS
	private CoinList coinList1;

	// walls
	private GameObject wall1, wall2, wall3, wall4;
	private ObjShape wallS;
	private TextureImage brickS;

	// coinList1
	private GameObject coin, coinMini;
	private ObjShape coinS;
	private TextureImage coinX;

	// rotation controller
	private NodeController rcCoin;

	public MyGame(String serverAddress, int serverPort, String protocol) {
		super();
		gm = new GhostManager(this);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		if (protocol.toUpperCase().compareTo("TCP") == 0)
			this.serverProtocol = ProtocolType.TCP;
		else
			this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args) {
		MyGame game = new MyGame(args[0], Integer.parseInt(args[1]), args[2]);
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes() {
		ScriptEngineManager factory = new ScriptEngineManager();
		jsEngine = factory.getEngineByName("js");

		scriptFileLoadShapes = new File("assets/scripts/LoadShapes.js");
		this.runScript(scriptFileLoadShapes);

		ghostS = new AnimatedShape("simpleCharFinal.rkm", "simpleCharFinal.rks");
		ghostS.loadAnimation("idle", "idle_1.rka");
		ghostS.loadAnimation("flight1", "flight01.rka");
		ghostS.loadAnimation("flight2", "flight02.rka");

		// simpleCharS = new ImportedModel("simpleCharV3.obj");
		simpleCharS = new AnimatedShape("simpleCharFinal.rkm", "simpleCharFinal.rks");
		simpleCharS.loadAnimation("idle", "idle_1.rka");
		simpleCharS.loadAnimation("flight1", "flight01.rka");
		simpleCharS.loadAnimation("flight2", "flight02.rka");

		simpleCharS.playAnimation("idle", 0.25f, AnimatedShape.EndType.LOOP, 0);

		terrainS = new TerrainPlane(1000);
		groundS = new Plane();

		creatureS = new ImportedModel("creature2.obj");

		wallS = new Cube();

		linxS = (ObjShape) jsEngine.get("linxS");
		linyS = (ObjShape) jsEngine.get("linyS");
		linzS = (ObjShape) jsEngine.get("linzS");

		// platform section
		coinS = new ImportedModel("coin.obj");
	}

	@Override
	public void loadTextures() {
		ghostT = new TextureImage("simpleCharFinal.png");
		simpleCharX = new TextureImage("simpleCharFinal.png");
		menuAvatar1X = new TextureImage("simpleCharFinalgreen.png");
		menuAvatar2X = new TextureImage("simpleCharFinalpurple.png");
		creatureX = new TextureImage("creature2.png");
		hills = new TextureImage("hmaphills2.jpg");
		grass = new TextureImage("grass.png");
		brickS = new TextureImage("brick1.jpg");
		coinX = new TextureImage("coin.png");
	}

	@Override
	public void buildObjects() {
		Matrix4f initialTranslation, initialRotation, initialScale;

		ScriptEngineManager factory = new ScriptEngineManager();
		jsEngine = factory.getEngineByName("js");

		scriptFileBuildObjects = new File("assets/scripts/BuildObjects.js");
		this.runScript(scriptFileBuildObjects);

		menuAvatar1 = new GameObject(GameObject.root(), simpleCharS, menuAvatar1X);
		menuAvatar1.setLocalTranslation((Matrix4f) jsEngine.get("menuAvatar1Trans"));
		menuAvatar1.setLocalScale((Matrix4f) jsEngine.get("menuAvatarScale"));
		menuAvatar1.getRenderStates()
				.setModelOrientationCorrection((new Matrix4f()).rotationY((float) java.lang.Math.toRadians(90.0f)));

		menuAvatar2 = new GameObject(GameObject.root(), simpleCharS, menuAvatar2X);
		menuAvatar2.setLocalTranslation((Matrix4f) jsEngine.get("menuAvatar2Trans"));
		menuAvatar2.setLocalScale((Matrix4f) jsEngine.get("menuAvatarScale"));
		menuAvatar2.getRenderStates()
				.setModelOrientationCorrection((new Matrix4f()).rotationY((float) java.lang.Math.toRadians(90.0f)));

		// build player avatar
		avatar = new GameObject(GameObject.root(), simpleCharS, simpleCharX);
		avatar.setLocalTranslation((Matrix4f) jsEngine.get("initAvatarTranslation"));
		// avatar.setLocalRotation((Matrix4f)jsEngine.get("initPlayerRotation"));
		avatar.setLocalScale((Matrix4f) jsEngine.get("initAvatarScale"));
		avatar.getRenderStates()
				.setModelOrientationCorrection((new Matrix4f()).rotationY((float) java.lang.Math.toRadians(-90.0f)));
		avatar.getRenderStates().hasLighting(true);

		// build creature model
		creature = new GameObject(GameObject.root(), creatureS, creatureX);
		initialTranslation = (new Matrix4f()).translation(0f, 3f, 250f);
		creature.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(1f, 1f, 1f);
		creature.setLocalScale(initialScale);
		creature.getRenderStates()
				.setModelOrientationCorrection((new Matrix4f()).rotationY((float) java.lang.Math.toRadians(-90.0f)));

		// add X,Y,Z axes
		x = new GameObject(GameObject.root(), linxS);
		y = new GameObject(GameObject.root(), linyS);
		z = new GameObject(GameObject.root(), linzS);
		(x.getRenderStates()).setColor(new Vector3f(1f, 0f, 0f));
		(y.getRenderStates()).setColor(new Vector3f(0f, 1f, 0f));
		(z.getRenderStates()).setColor(new Vector3f(0f, 0f, 1f));

		terrain = new GameObject(GameObject.root(), terrainS, grass);
		initialTranslation = (new Matrix4f()).translation(0f, 0f, 0f);
		terrain.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(100.0f, 10.0f, 100.0f);
		terrain.setLocalScale(initialScale);
		terrain.setHeightMap(hills);

		ground = new GameObject(GameObject.root(), groundS, grass);
		initialTranslation = (new Matrix4f()).translation(0f, -1f, 0f);
		ground.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(100000.0f, 0.0f, 100000.0f);
		ground.setLocalScale(initialScale);

		createLobbyWalls(); // creates the lobby walls

		coinList1 = new CoinList(GameObject.root(), coinS, coinX);
		float zTranslation = 600f;
		float xPosTranslation = 50f;
		float xNegTranslation = -50f;
		float zScale = 10f;
		float xScale = 5f;
		int count = 0;
		for (Coin c : coinList1.getCoinList()) {
			initialScale = (new Matrix4f()).scaling(5f, 5f, 5f);
			initialRotation = (new Matrix4f());
			c.getCoinObj().setLocalScale(initialScale);
			c.getCoinObj().pitchObject(90f, initialRotation);
			c.getCoinObj().setLocalTranslation(initialTranslation);
			zTranslation += 200f;
			if (count % 2 == 0) {
				initialTranslation = (new Matrix4f()).translation(xPosTranslation, 10f, zTranslation);
				xPosTranslation = (float)Math.random() * 100;
				xNegTranslation = (float)Math.random() * 100 * -1;
			} else if (count % 2 != 0) {
				initialTranslation = (new Matrix4f()).translation(xNegTranslation, 10f, zTranslation);
				xPosTranslation = (float)Math.random() * 100;
				xNegTranslation = (float)Math.random() * 100 * -1;
			}
			c.getCoinObj().setLocalTranslation(initialTranslation);
			count++;
		}

		coinMini = new GameObject(GameObject.root(), coinS, coinX);
		initialTranslation = (new Matrix4f()).translation(0, 8f, 0);
		initialScale = (new Matrix4f()).scaling(0.5f);
		coinMini.setLocalScale(initialScale);
		coinMini.setLocalTranslation(initialTranslation);
		coinMini.setParent(avatar);
		coinMini.propagateTranslation(true);
		coinMini.propagateRotation(false);
		coinMini.getRenderStates().disableRendering();
	}

	@Override
	public void initializeLights() {
		// Light.setGlobalAmbient(.5f, .5f, .5f);
		ScriptEngineManager factory = new ScriptEngineManager();
		jsEngine = factory.getEngineByName("js");

		scriptFile1 = new File("assets/scripts/CreateLight.js");
		this.runScript(scriptFile1);
		(engine.getSceneGraph()).addLight((Light)jsEngine.get("light"));

		spotlight1 = new Light();
		spotlight1.setType(LightType.SPOTLIGHT);
		spotlight1.setAmbient(.5f, .5f, .5f);
		spotlight1.setDiffuse(.7f, .7f, .7f);
		spotlight1.setSpecular(1.0f, 1.0f, 1.0f);
		spotlight1.setLocation(new Vector3f(18f, 50.0f, 100.0f));
		spotlight1.setRange(5f);
		spotlight1.setDirection(new Vector3f(0f, 0f, -1f));
		spotlight1.setCutoffAngle(5.0f);
		spotlight1.setOffAxisExponent(5.0f);
		(engine.getSceneGraph()).addLight(spotlight1);

		spotlight2 = new Light();
		spotlight2.setType(LightType.SPOTLIGHT);
		spotlight2.setAmbient(.5f, .5f, .5f);
		spotlight2.setDiffuse(.7f, .7f, .7f);
		spotlight2.setSpecular(1.0f, 1.0f, 1.0f);
		spotlight2.setLocation(new Vector3f(-18f, 50.0f, 100.0f));
		spotlight2.setRange(5f);
		spotlight2.setDirection(new Vector3f(0f, 0f, -1f));
		spotlight2.setCutoffAngle(5.0f);
		spotlight2.setOffAxisExponent(5.0f);
		(engine.getSceneGraph()).addLight(spotlight2);
		
	}
	public Light getSpotlight1() {
		return spotlight1;
	}
	public Light getSpotLight2() {
		return spotlight2;
	}
	@Override
	public void loadSkyBoxes() {
		fluffyClouds = (engine.getSceneGraph()).loadCubeMap("fluffyClouds");
		lakeIslands = (engine.getSceneGraph()).loadCubeMap("lakeIslands");
		nightSky = (engine.getSceneGraph()).loadCubeMap("nightSky");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(nightSky);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	@Override
	public void initializeGame() {
		prevTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		(engine.getRenderSystem()).setWindowDimensions(1900, 1000);

		// ----------------- initialize camera ----------------
		// positionCameraBehindAvatar();

		// ----------------- INPUTS SECTION -----------------------------
		im = engine.getInputManager();

		// build some action objects for doing things in response to user input
		// attach the action objects to keyboard and gamepad components
		createViewports();
		orbitController = new CameraOrbit3D(leftCamera, avatar, engine, 180f, 20f, 20f);
		rcCoin = new RotationController(engine, new Vector3f(0f, 1f, 0f), 0.001f);
		for (Coin c : coinList1.getCoinList()) {
			rcCoin.addTarget(c.getCoinObj());
		}
		rcCoin.addTarget(coinMini);
		(engine.getSceneGraph()).addNodeController(rcCoin);
		rcCoin.toggle();
		// keyboard inputs
		FwdAction fwdAction = new FwdAction(this);
		BackAction backAction = new BackAction(this);
		LeftAction leftAction = new LeftAction(this);
		RightAction rightAction = new RightAction(this);
		SwitchGameStateAction switchGameStateAction = new SwitchGameStateAction(this);
		ToggleAvatarSelectAction toggleAvatarSelectAction = new ToggleAvatarSelectAction(this);
		ToggleWorldAxis toggleWorldAxis = new ToggleWorldAxis(this);
		ToggleLights toggleLights = new ToggleLights(this);
		// controller inputs
		MoveActionController moveActionController = new MoveActionController(this);
		TurnActionControllerX turnActionControllerX = new TurnActionControllerX(this);
		// associate kb inputs
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.W, fwdAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.S, backAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.D, rightAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.A, leftAction,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.SPACE, toggleLights,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.GRAVE, switchGameStateAction,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key._1, toggleAvatarSelectAction,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.V, toggleWorldAxis,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		// associate gamepad inputs
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.Y, moveActionController,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.X, turnActionControllerX,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._0, toggleAvatarSelectAction,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._1, switchGameStateAction,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);

		// --- initialize physics system ---
		String engine = "tage.physics.JBullet.JBulletPhysicsEngine";
		float[] gravity = { 0f, -5f, 0f };
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		physicsEngine.setGravity(gravity);

		// --- create physics world ---
		float mass = 1.0f;
		float up[] = { 0, 1, 0 };
		double[] creatureTempTransform, terrainTempTransform, avatarTempTransform;
		float playerSize = 7f;

		Matrix4f creatureTranslation = new Matrix4f(creature.getLocalTranslation());
		Matrix4f avatarTranslation = new Matrix4f(avatar.getLocalTranslation());
		Matrix4f terrainTranslation = new Matrix4f(terrain.getLocalTranslation());

		creatureTempTransform = toDoubleArray(creatureTranslation.get(creatureVals));
		avatarTempTransform = toDoubleArray(avatarTranslation.get(avatarVals));
		terrainTempTransform = toDoubleArray(terrainTranslation.get(terrainVals));

		// this works as expected
		creatureP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, creatureTempTransform, 3f);
		creatureP.setBounciness(0.0f);
		creature.setPhysicsObject(creatureP);

		// find out what plane_constant (fourth argument) does, maybe some mismatch
		// between heightmap and physics object?
		terrainP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), terrainTempTransform, up, 0.0f);
		terrainP.setBounciness(1.0f);
		terrain.setPhysicsObject(terrainP);

		// player, spherical hit box does not seem to have issues
		avatarP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, avatarTempTransform, playerSize);
		avatarP.setBounciness(0.0f);
		avatar.setPhysicsObject(avatarP);
		avatarP.getRigidBody().setActivationState(CollisionObject.DISABLE_DEACTIVATION);

		setupNetworking();
		// setup audio
		initAudio();
	}

	public GameObject getAvatar() {
		return avatar;
	}

	public GameObject getCreature() {
		return creature;
	}

	@Override
	public void update() {
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		timeSinceLastFrame = currFrameTime - lastFrameTime;
		elapsedTime += (currFrameTime - lastFrameTime);
		im.update((float) elapsedTime);
		if (gameState == 0) { // character selection
			// System.out.println("in state 0");
			positionCameraAvatarSelect(); // keep camera in place
			if (avatarChosen == 0) {
				String dispStr1 = "Choose your avatar! Press 1 to toggle your choice. 1 is Green, 2 is Purple.";
				(engine.getHUDmanager()).setHUD2(dispStr1, hud1Color, (int) leftVP.getRelativeLeft(),
						(int) leftVP.getRelativeBottom());
			} else {
				String dispStr1 = "Avatar chosen: " + avatarChosen +". Press the GRAVE key to continue.";
				(engine.getHUDmanager()).setHUD2(dispStr1, hud1Color, (int) leftVP.getRelativeLeft(),
						(int) leftVP.getRelativeBottom());
			}
			processNetworking((float) elapsedTime);
		} else if (gameState == 1) { // "lobby"
			String dispStr1 = "When you're ready, press GRAVE to move outside.";
			(engine.getHUDmanager()).setHUD2(dispStr1, hud1Color, (int) leftVP.getRelativeLeft(),
					(int) leftVP.getRelativeBottom());
			positionCameraBehindAvatar();
			// orbitController.updateCameraPosition();
			processNetworking((float) elapsedTime);
			protClient.sendMoveMessage(avatar.getWorldLocation());
			lobbyKeepInBounds();
			if (running) { // update physics
				Matrix4f mat = new Matrix4f();
				Matrix4f mat2 = new Matrix4f().identity();
				checkForCollisions();
				physicsEngine.update((float) elapsedTime);
				for (GameObject go : engine.getSceneGraph().getGameObjects()) {
					if (go.getPhysicsObject() != null) {
						mat.set(toFloatArray(go.getPhysicsObject().getTransform()));
						mat2.set(3, 0, mat.m30());
						mat2.set(3, 1, mat.m31());
						mat2.set(3, 2, mat.m32());
						go.setLocalTranslation(mat2);
					}
				}
			}
			Vector3f loc = avatar.getWorldLocation();
			float height = terrain.getHeight(loc.x(), loc.z());
			avatar.setLocalLocation(new Vector3f(loc.x(), height + 7f, loc.z()));
			// update audio
			backgroundMusic.setLocation(avatar.getWorldLocation());
			creatureSound.setLocation(creature.getWorldLocation());
			setEarParameters();
			simpleCharS.updateAnimation();
			ghostS.updateAnimation();
			// System.out.println("in state 1");
		} else if (gameState == 2) { // racing
			if (isFlying == false) {
				simpleCharS.playAnimation("flight1", 0.25f, AnimatedShape.EndType.NONE, 0);
				simpleCharS.playAnimation("flight2", 0.25f, AnimatedShape.EndType.LOOP, 0);
				ghostS.playAnimation("flight1", 0.25f, AnimatedShape.EndType.NONE, 0);
				ghostS.playAnimation("flight2", 0.25f, AnimatedShape.EndType.LOOP, 0);
				avatar.setLocalLocation(new Vector3f(0, 7, 350f));
				Matrix4f transform = new Matrix4f(avatar.getLocalTranslation());
				double[] transformDouble;
				transformDouble = toDoubleArray(transform.get(avatarVals));
				avatarP.setTransform(transformDouble);
			}
			creature.lookAt(avatar);
			isFlying = true;
			String dispStr1 = "Coins collected: " + coinCounter;
			(engine.getHUDmanager()).setHUD2(dispStr1, hud1Color, (int) leftVP.getRelativeLeft(),
					(int) leftVP.getRelativeBottom());
			positionCameraBehindAvatar();
			// orbitController.updateCameraPosition();
			processNetworking((float) elapsedTime);
			protClient.sendMoveMessage(avatar.getWorldLocation());
			for (Coin c : coinList1.getCoinList()) {
				checkAvatarCoinDistance(avatar, c.getCoinObj());
			}
			if (running) { // update physics
				Matrix4f mat = new Matrix4f();
				Matrix4f mat2 = new Matrix4f().identity();
				checkForCollisions();
				physicsEngine.update((float) elapsedTime);
				for (GameObject go : engine.getSceneGraph().getGameObjects()) {
					if (go.getPhysicsObject() != null) {
						mat.set(toFloatArray(go.getPhysicsObject().getTransform()));
						mat2.set(3, 0, mat.m30());
						mat2.set(3, 1, mat.m31());
						mat2.set(3, 2, mat.m32());
						go.setLocalTranslation(mat2);
					}
				}
			}
			Vector3f loc = avatar.getWorldLocation();
			float height = terrain.getHeight(loc.x(), loc.z());
			avatar.setLocalLocation(new Vector3f(loc.x(), height + 7f, loc.z()));
			// update audio
			backgroundMusic.setLocation(avatar.getWorldLocation());
			creatureSound.setLocation(creature.getWorldLocation());
			setEarParameters();
			simpleCharS.updateAnimation();
			ghostS.updateAnimation();
			// System.out.println("in state 2");
		}
	}
	public void positionCameraAvatarSelect() {
		Vector3f fwd, up, right;
		leftCamera.setLocation(initLeftCamLoc);
		fwd = avatar.getWorldForwardVector();
		up = avatar.getWorldUpVector();
		right = avatar.getWorldRightVector();
		leftCamera.setU(right);
		leftCamera.setV(up);
		leftCamera.setN(fwd);
	}
	public void positionCameraBehindAvatar() {
		Vector3f loc, fwd, up, right;
		loc = avatar.getWorldLocation();
		fwd = avatar.getWorldForwardVector();
		up = avatar.getWorldUpVector();
		right = avatar.getWorldRightVector();
		leftCamera.setU(right);
		leftCamera.setV(up);
		leftCamera.setN(fwd);
		leftCamera.setLocation(loc.add(up.mul(10f)).add(fwd.mul(-30f)));
	}
	private float distanceBetween(GameObject obj1, GameObject obj2) {
		Vector3f obj2Loc = obj2.getLocalLocation();
		Vector3f obj1Loc = obj1.getLocalLocation();
		return distanceBetween = obj1Loc.distance(obj2Loc);
	}
	public void checkAvatarCoinDistance(GameObject avatar, GameObject coin) {
		if (distanceBetween(avatar, coin) < 7f) {
			coin.setLocalLocation(new Vector3f(0f, -10f, 0f));
			coin.getRenderStates().disableRendering();
			engine.getSceneGraph().removeGameObject(coin);
			coinCounter++;
			coinMini.getRenderStates().enableRendering();
		}
	}
	public void lobbyKeepInBounds() {
		if (avatar.getWorldLocation().z >= 95) {
			avatarP.applyForce(0f, 0f, -100f, avatar.getWorldLocation().x(), avatar.getWorldLocation().y(), avatar.getWorldLocation().z());
		}
		if (avatar.getWorldLocation().z <= -95) {
			avatarP.applyForce(0f, 0f, 100f, avatar.getWorldLocation().x(), avatar.getWorldLocation().y(), avatar.getWorldLocation().z());
		}
		if (avatar.getWorldLocation().x >= 95) {
			avatarP.applyForce(-100f, 0f, 0f, avatar.getWorldLocation().x(), avatar.getWorldLocation().y(), avatar.getWorldLocation().z());
		}
		if (avatar.getWorldLocation().x <= -95) {
			avatarP.applyForce(100f, 0f, 0f, avatar.getWorldLocation().x(), avatar.getWorldLocation().y(), avatar.getWorldLocation().z());
		}
	}
	public PhysicsObject getAvatarP() {
		return avatarP;
	}

	public double getTimeSinceLastFrame() {
		return timeSinceLastFrame;
	}

	@Override
	public void createViewports() {
		(engine.getRenderSystem()).addViewport("LEFT", 0, 0, 1f, 1f);
		leftVP = (engine.getRenderSystem()).getViewport("LEFT");
		leftCamera = leftVP.getCamera();
		leftCamera.setLocation(new Vector3f(0, 0, 0));
		leftCamera.setU(new Vector3f(1, 0, 0));
		leftCamera.setV(new Vector3f(0, 1, 0));
		leftCamera.setN(new Vector3f(0, 0, -1));
	}

	public Camera getRightCamera() {
		return rightVP.getCamera();
	}

	public Camera getLeftCamera() {
		return leftVP.getCamera();
	}

	public Vector3f getGlobalYAxis() {
		return globalYAxis;
	}

	public Vector3f getGlobalXAxis() {
		return globalXAxis;
	}

	public Vector3f getGlobalZAxis() {
		return globalZAxis;
	}

	public GameObject getXAxisObject() {
		return x;
	}

	public GameObject getYAxisObject() {
		return y;
	}

	public GameObject getZAxisObject() {
		return z;
	}

	public ProtocolClient getProtClient() {
		return protClient;
	}

	public int getGameState() {
		return gameState;
	}

	public void setGameState(int n) {
		gameState = n;
	}

	public int getAvatarChosen() {
		return avatarChosen;
	}

	public void setAvatarChosen(int n) {
		avatarChosen = n;
	}

	public void setPlayerAvatarTextureGreen() {
		avatar.setTextureImage(menuAvatar1X);
	}

	public void setPlayerAvatarTexturePurple() {
		avatar.setTextureImage(menuAvatar2X);
	}

	private void createLobbyWalls() {
		Matrix4f initialTranslation, initialScale, initialRotation;

		wall1 = new GameObject(GameObject.root(), wallS, brickS);
		initialTranslation = (new Matrix4f()).translation(0, 60, -100);
		wall1.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(100, 100, 1);
		wall1.setLocalScale(initialScale);

		wall2 = new GameObject(GameObject.root(), wallS, brickS);
		initialTranslation = (new Matrix4f()).translation(0, 60, 100);
		wall2.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(100, 100, 1);
		wall2.setLocalScale(initialScale);

		wall3 = new GameObject(GameObject.root(), wallS, brickS);
		initialScale = (new Matrix4f()).scaling(1, 100, 100);
		wall3.setLocalScale(initialScale);
		initialTranslation = (new Matrix4f()).translation(-100, 60, 0);
		wall3.setLocalTranslation(initialTranslation);

		wall4 = new GameObject(GameObject.root(), wallS, brickS);
		initialScale = (new Matrix4f()).scaling(1, 100, 100);
		wall4.setLocalScale(initialScale);
		initialTranslation = (new Matrix4f()).translation(100, 60, 0);
		wall4.setLocalTranslation(initialTranslation);
	}

	private void runScript(File scriptFile) {
		try {
			FileReader fileReader = new FileReader(scriptFile);
			jsEngine.eval(fileReader);
			fileReader.close();
		} catch (FileNotFoundException e1) {
			System.out.println(scriptFile + " not found " + e1);
		} catch (IOException e2) {
			System.out.println("IO problem with " + scriptFile + e2);
		} catch (ScriptException e3) {
			System.out.println("ScriptException in " + scriptFile + e3);
		} catch (NullPointerException e4) {
			System.out.println("Null pointer exception reading " + scriptFile + e4);
		}
	}

	private void checkForCollisions() {
		com.bulletphysics.dynamics.DynamicsWorld dynamicsWorld;
		com.bulletphysics.collision.broadphase.Dispatcher dispatcher;
		com.bulletphysics.collision.narrowphase.PersistentManifold manifold;
		com.bulletphysics.dynamics.RigidBody object1, object2;
		com.bulletphysics.collision.narrowphase.ManifoldPoint contactPoint;
		dynamicsWorld = ((JBulletPhysicsEngine) physicsEngine).getDynamicsWorld();
		dispatcher = dynamicsWorld.getDispatcher();
		int manifoldCount = dispatcher.getNumManifolds();
		for (int i = 0; i < manifoldCount; i++) {
			manifold = dispatcher.getManifoldByIndexInternal(i);
			object1 = (com.bulletphysics.dynamics.RigidBody) manifold.getBody0();
			object2 = (com.bulletphysics.dynamics.RigidBody) manifold.getBody1();
			JBulletPhysicsObject obj1 = JBulletPhysicsObject.getJBulletPhysicsObject(object1);
			JBulletPhysicsObject obj2 = JBulletPhysicsObject.getJBulletPhysicsObject(object2);
			for (int j = 0; j < manifold.getNumContacts(); j++) {
				contactPoint = manifold.getContactPoint(j);
				if (contactPoint.getDistance() < 0.0f) {
					// System.out.println("---- hit between " + obj1 + " and " + obj2);
					break;
				}
			}
		}
	}

	public float[] toFloatArray(double[] arr) {
		if (arr == null) {
			return null;
		}
		int n = arr.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++) {
			ret[i] = (float) arr[i];
		}
		return ret;
	}

	private double[] toDoubleArray(float[] arr) {
		if (arr == null) {
			return null;
		}
		int n = arr.length;
		double[] ret = new double[n];
		for (int i = 0; i < n; i++) {
			ret[i] = (double) arr[i];
		}
		return ret;
	}

	public void initAudio() {
		AudioResource resource1;
		AudioResource resource2;
		AudioResource resource3;
		audioMgr = AudioManagerFactory.createAudioManager("tage.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize()) {
			System.out.println("Audio Manager failed to initialize!");
			return;
		}
		resource1 = audioMgr.createAudioResource("assets/sounds/signal_pursuit.wav", AudioResourceType.AUDIO_STREAM);
		resource2 = audioMgr.createAudioResource("assets/sounds/Goblin_01.wav", AudioResourceType.AUDIO_SAMPLE);
		resource3 = audioMgr.createAudioResource("assets/sounds/MENU_Pick.wav", AudioResourceType.AUDIO_SAMPLE);
		backgroundMusic = new Sound(resource1, SoundType.SOUND_MUSIC, 20, true);
		creatureSound = new Sound(resource2, SoundType.SOUND_EFFECT, 100, true);
		avatarPickSound = new Sound(resource3, SoundType.SOUND_EFFECT, 100, false);

		backgroundMusic.initialize(audioMgr);
		backgroundMusic.setMaxDistance(10.0f);
		backgroundMusic.setMinDistance(0.5f);
		backgroundMusic.setRollOff(5.0f);
		backgroundMusic.setLocation(avatar.getWorldLocation());

		creatureSound.initialize(audioMgr);
		creatureSound.setMaxDistance(100f);
		creatureSound.setMinDistance(1f);
		creatureSound.setRollOff(1.1f);
		creatureSound.setLocation(creature.getWorldLocation());

		avatarPickSound.initialize(audioMgr);
		avatarPickSound.setLocation(leftCamera.getLocation());

		setEarParameters();
		backgroundMusic.play();
		creatureSound.play();
	}

	public void setEarParameters() {
		Camera camera = leftCamera;
		audioMgr.getEar().setLocation(avatar.getLocalLocation());
		audioMgr.getEar().setOrientation(camera.getN(), new Vector3f(0.0f, 1.0f, 0.0f));
	}

	public Sound getAvatarPickSound() {
		return avatarPickSound;
	}
	// ---------- NETWORKING SECTION ----------------

	public AnimatedShape getGhostShape() {
		return ghostS;
	}

	public TextureImage getGhostTexture() {
		return ghostT;
	}

	public GhostManager getGhostManager() {
		return gm;
	}

	public Engine getEngine() {
		return engine;
	}

	private void setupNetworking() {
		isClientConnected = false;
		try {
			protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (protClient == null) {
			System.out.println("missing protocol host");
		} else { // Send the initial join message with a unique identifier for this client
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
		}
	}

	protected void processNetworking(float elapsTime) { // Process packets received by the client from the server
		if (protClient != null)
			protClient.processPackets();
	}

	public Vector3f getPlayerPosition() {
		return avatar.getWorldLocation();
	}

	public void setIsConnected(boolean value) {
		this.isClientConnected = value;
	}

	private class SendCloseConnectionPacketAction extends AbstractInputAction {
		@Override
		public void performAction(float time, net.java.games.input.Event evt) {
			if (protClient != null && isClientConnected == true) {
				protClient.sendByeMessage();
			}
		}
	}
}