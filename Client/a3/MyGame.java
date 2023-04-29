package a3;

import tage.*;
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

import net.java.games.input.*;
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;
import tage.input.action.*; 
import tage.networking.IGameConnection.ProtocolType;

import javax.script.*;

public class MyGame extends VariableFrameRateGame
{
	private static Engine engine;
	private InputManager im;
	private GhostManager gm;

	private int counter=0;
	private Vector3f currentPosition;
	private Matrix4f initialTranslation, initialRotation, initialScale;
	private double startTime, elapsedTime, amt, timeSinceLastFrame, lastFrameTime, currFrameTime;
	private double prevTime = 0;

	private Vector3f globalXAxis = new Vector3f(1f, 0f, 0f);
	private Vector3f globalYAxis = new Vector3f(0f, 1f, 0f);
	private Vector3f globalZAxis = new Vector3f(0f, 0f, 1f);
	private GameObject tor, avatar, x, y, z;
	private ObjShape torS, ghostS, dolS, linxS, linyS, linzS;
	private TextureImage doltx, ghostT;
	private Light light;
	private int fluffyClouds, lakeIslands;

	// SIMPLE CHARACTER
	private GameObject simpleCharacter;
	private ObjShape simpleCharS;
	private TextureImage simpleCharX;

	// CREATURE VARIABLES
	private GameObject creature;
	private TextureImage creaturetx;
	private ObjShape creatureS;

	// PHYSICS 
	private PhysicsEngine physicsEngine;
	private PhysicsObject avatarP, creatureP, terrainP;
	private boolean running = true;
	private float creatureVals[] = new float[16];
	private float avatarVals[] = new float[16];
	private float terrainVals[] = new float[16];

	// TERRAIN
	private GameObject terrain;
	private ObjShape terrainS;
	private TextureImage hills, bricks;

	private Viewport leftVP;
	private Viewport rightVP;
	private Camera leftCamera;
	private Camera rightCamera;
	private CameraOrbit3D orbitController;

	// SCRIPT STUFF
	private File scriptFile1, scriptFileLoadShapes, scriptFileBuildObjects;
	private long fileLastModifiedTime = 0;
	ScriptEngine jsEngine;

	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;

	public MyGame(String serverAddress, int serverPort, String protocol)
	{	super();
		gm = new GhostManager(this);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		if (protocol.toUpperCase().compareTo("TCP") == 0)
			this.serverProtocol = ProtocolType.TCP;
		else
			this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args)
	{	MyGame game = new MyGame(args[0], Integer.parseInt(args[1]), args[2]);
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

		torS = new Torus(0.5f, 0.2f, 48);
		ghostS = new Sphere();
		dolS = new ImportedModel("dolphinHighPoly.obj");
		simpleCharS = new ImportedModel("simplecharacter.obj");
		terrainS = new TerrainPlane(1000);

		creatureS = new ImportedModel("creature.obj");

		linxS = (ObjShape)jsEngine.get("linxS");
		linyS = (ObjShape)jsEngine.get("linyS");
		linzS = (ObjShape)jsEngine.get("linzS");
	}

	@Override
	public void loadTextures()
	{	doltx = new TextureImage("Dolphin_HighPolyUV.png");
		ghostT = new TextureImage("redDolphin.jpg");
		simpleCharX = new TextureImage("simplecharactertesttex.png");
		hills = new TextureImage("hmaptest.jpg");
		bricks = new TextureImage("grass.png");

		//creaturetx =  new TextureImage("creatureTx.jpg")
	}

	@Override
	public void buildObjects() {	
		Matrix4f initialTranslation, initialRotation, initialScale;

		ScriptEngineManager factory = new ScriptEngineManager();
		jsEngine = factory.getEngineByName("js");

		scriptFileBuildObjects = new File("assets/scripts/BuildObjects.js");
		this.runScript(scriptFileBuildObjects);

		// build player avatar
		avatar = new GameObject(GameObject.root(), simpleCharS, simpleCharX);
		//initialTranslation = (Matrix4f)jsEngine.get("initPlayerTranslation");
		avatar.setLocalTranslation((Matrix4f)jsEngine.get("initAvatarTranslation"));
		//initialRotation = (new Matrix4f()).rotationY((float)java.lang.Math.toRadians(135.0f));
		avatar.setLocalRotation((Matrix4f)jsEngine.get("initPlayerRotation"));
		//initialScale = (new Matrix4f()).scaling(0.25f, 0.25f, 0.25f);
		avatar.setLocalScale((Matrix4f)jsEngine.get("initAvatarScale"));
		avatar.getRenderStates().setModelOrientationCorrection((new Matrix4f()).rotationY((float)java.lang.Math.toRadians(90.0f)));

		//build creature model
		creature = new GameObject(GameObject.root(), creatureS);
		initialTranslation = (new Matrix4f()).translation(-10f, 3f, 0f);
		creature.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(0.50f, 0.50f, 0.50f);
		creature.setLocalScale(initialScale);
		creature.getRenderStates().setModelOrientationCorrection((new Matrix4f()).rotationY((float)java.lang.Math.toRadians(90.0f)));

		// build torus along X axis
		tor = new GameObject(GameObject.root(), torS);
		initialTranslation = (new Matrix4f()).translation(1,0,0);
		tor.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(0.25f);
		tor.setLocalScale(initialScale);


		// add X,Y,-Z axes
		x = new GameObject(GameObject.root(), linxS);
		y = new GameObject(GameObject.root(), linyS);
		z = new GameObject(GameObject.root(), linzS);
		(x.getRenderStates()).setColor(new Vector3f(1f,0f,0f));
		(y.getRenderStates()).setColor(new Vector3f(0f,1f,0f));
		(z.getRenderStates()).setColor(new Vector3f(0f,0f,1f));

		terrain = new GameObject(GameObject.root(), terrainS, bricks);
		initialTranslation = (new Matrix4f()).translation(0f, 0f, 0f);
		terrain.setLocalTranslation(initialTranslation);
		initialScale = (new Matrix4f()).scaling(100.0f, 10.0f, 100.0f);
		terrain.setLocalScale(initialScale);
		terrain.setHeightMap(hills);
	}

	@Override
	public void initializeLights() {
		/*light = new Light();
		light.setLocation(new Vector3f(0f, 5f, 0f));
		(engine.getSceneGraph()).addLight(light);*/
		Light.setGlobalAmbient(.5f, .5f, .5f);
		ScriptEngineManager factory = new ScriptEngineManager();
		jsEngine = factory.getEngineByName("js");

		scriptFile1 = new File("assets/scripts/CreateLight.js");
		this.runScript(scriptFile1);
		(engine.getSceneGraph()).addLight((Light)jsEngine.get("light"));

		Light.setGlobalAmbient(.5f, .5f, .5f);
	}

	@Override
	public void loadSkyBoxes() {
		fluffyClouds = (engine.getSceneGraph()).loadCubeMap("fluffyClouds");
		lakeIslands = (engine.getSceneGraph()).loadCubeMap("lakeIslands");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(fluffyClouds);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}
	@Override
	public void initializeGame()
	{	
		prevTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		(engine.getRenderSystem()).setWindowDimensions(1900,1000);

		// ----------------- initialize camera ----------------
		//positionCameraBehindAvatar();

		// ----------------- INPUTS SECTION -----------------------------
		im = engine.getInputManager();

		// build some action objects for doing things in response to user input

		// attach the action objects to keyboard and gamepad components
		createViewports();
		Camera c = leftCamera;
		orbitController = new CameraOrbit3D(c, avatar, engine);
		// keyboard inputs
		FwdAction fwdAction = new FwdAction(this);
		BackAction backAction = new BackAction(this);
		TurnActionRight turnActionRight = new TurnActionRight(this);
		TurnActionLeft turnActionLeft = new TurnActionLeft(this);
		//TurnActionUp turnActionUp = new TurnActionUp(this);
		//TurnActionDown turnActionDown = new TurnActionDown(this);
		//RollActionLeft rollActionLeft = new RollActionLeft(this);
		//RollActionRight rollActionRight = new RollActionRight(this);
		ToggleWorldAxis toggleWorldAxis = new ToggleWorldAxis(this);
		PanCameraFwd panCameraFwd = new PanCameraFwd(this);
		PanCameraBack panCameraBack = new PanCameraBack(this);
		PanCameraLeft panCameraLeft = new PanCameraLeft(this);
		PanCameraRight panCameraRight = new PanCameraRight(this);
		ZoomCameraIn zoomCameraIn = new ZoomCameraIn(this);
		ZoomCameraOut zoomCameraOut = new ZoomCameraOut(this);
		// controller inputs
		MoveActionController moveActionController = new MoveActionController(this);
		TurnActionControllerX turnActionControllerX = new TurnActionControllerX(this);
		PanCameraFwdBwdController panCameraFwdBwdController = new PanCameraFwdBwdController(this);
		PanCameraLeftRightController panCameraLeftRightController = new PanCameraLeftRightController(this);
		//PitchActionController pitchActionController = new PitchActionController(this);
		//RollActionController rollActionController = new RollActionController(this);
		// associate kb inputs
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.W, fwdAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.S, backAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.D, turnActionRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.A, turnActionLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.UP, turnActionUp, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.DOWN, turnActionDown, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.LEFT, rollActionLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.RIGHT, rollActionRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.V, toggleWorldAxis, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.I, panCameraFwd, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.K, panCameraBack, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.J, panCameraLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.L, panCameraRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.O, zoomCameraIn, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.P, zoomCameraOut, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		// associate gamepad inputs
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.Y, moveActionController, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.X, turnActionControllerX, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RY, panCameraFwdBwdController, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		//im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RX, panCameraLeftRightController, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._0, panCameraBack, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._1, panCameraRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._2, panCameraLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._3, panCameraFwd, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._4, zoomCameraIn, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._5, zoomCameraOut, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

		// --- initialize physics system ---
		String engine = "tage.physics.JBullet.JBulletPhysicsEngine";
		float[] gravity = {0f, -5f, 0f};
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		physicsEngine.setGravity(gravity);

		// --- create physics world ---
		float mass = 1.0f;
		float up[] = {0,1,0};
		double[] creatureTempTransform, avatarTempTransform, terrainTempTransform;
		float playerSize[] = {1, 5, 1};

		Matrix4f creatureTranslation = new Matrix4f(creature.getLocalTranslation());
		Matrix4f avatarTranslation = new Matrix4f(avatar.getLocalTranslation());
		Matrix4f terrainTranslation = new Matrix4f(terrain.getLocalTranslation());

		creatureTempTransform = toDoubleArray(creatureTranslation.get(creatureVals));
		avatarTempTransform = toDoubleArray(avatarTranslation.get(avatarVals));
		terrainTempTransform = toDoubleArray(terrainTranslation.get(terrainVals));
		creatureP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, creatureTempTransform, 0.75f);
		creatureP.setBounciness(1.0f);
		creature.setPhysicsObject(creatureP);

		terrainP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), terrainTempTransform, up, 0.0f);
		terrainP.setBounciness(1.0f);
		terrain.setPhysicsObject(terrainP);

		//avatarP = physicsEngine.addBoxObject(counter, mass, avatarTempTransform, playerSize);
		//avatarP.setBounciness(0.0f);
		//avatar.setPhysicsObject(avatarP);



		setupNetworking();
	}

	public GameObject getAvatar() { return avatar; }
	public GameObject getCreature() { return creature; }

	@Override
	public void update() {
		elapsedTime = System.currentTimeMillis() - prevTime;
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		timeSinceLastFrame = currFrameTime - lastFrameTime;
		elapsedTime += (currFrameTime - lastFrameTime);
		//Camera c = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		
		// build and set HUD
		/*String dispStr2 = "camera position = "
			+ (c.getLocation()).x()
			+ ", " + (c.getLocation()).y()
			+ ", " + (c.getLocation()).z();*/
		//(engine.getHUDmanager()).setHUD2(dispStr2, hud2Color, 500, 15);

		// update inputs and camera
		im.update((float)elapsedTime);
		orbitController.updateCameraPosition();
		Vector3f loc = avatar.getWorldLocation();
		float height = terrain.getHeight(loc.x(), loc.z());
		avatar.setLocalLocation(new Vector3f(loc.x(), height+5f, loc.z()));
		processNetworking((float)elapsedTime);

		// update physics
		if (running)
		{
			Matrix4f mat = new Matrix4f();
			Matrix4f mat2 = new Matrix4f().identity();
			checkForCollisions();
			physicsEngine.update((float)elapsedTime);
			for (GameObject go:engine.getSceneGraph().getGameObjects())
			{ 
			if (go.getPhysicsObject() != null)
			{ 
				mat.set(toFloatArray(go.getPhysicsObject().getTransform()));
				mat2.set(3,0,mat.m30());
				mat2.set(3,1,mat.m31());
				mat2.set(3,2,mat.m32());
				go.setLocalTranslation(mat2);
			}
		} 
	}
}

	public GameObject getDolphin() {
		return avatar;
	}
	public double getTimeSinceLastFrame() {
		return timeSinceLastFrame;
	}
	@Override
	public void createViewports() {
		(engine.getRenderSystem()).addViewport("LEFT",0,0,1f,1f);
		(engine.getRenderSystem()).addViewport("RIGHT",.75f,0,.25f,.25f);
		leftVP = (engine.getRenderSystem()).getViewport("LEFT");
		rightVP = (engine.getRenderSystem()).getViewport("RIGHT");
		leftCamera = leftVP.getCamera();
		rightCamera = rightVP.getCamera();
		rightVP.setHasBorder(true);
		rightVP.setBorderWidth(1);
		rightVP.setBorderColor(0.0f, 1.0f, 0.0f);

		leftCamera.setLocation(new Vector3f(-2,0,2));
		leftCamera.setU(new Vector3f(1,0,0));
		leftCamera.setV(new Vector3f(0,1,0));
		leftCamera.setN(new Vector3f(0,0,-1));

		rightCamera.setLocation(new Vector3f(0,2,0));
		rightCamera.setU(new Vector3f(1,0,0));
		rightCamera.setV(new Vector3f(0,0,-1));
		rightCamera.setN(new Vector3f(0,-1,0));
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
	private void runScript(File scriptFile) {
		try {
			FileReader fileReader = new FileReader(scriptFile);
			jsEngine.eval(fileReader);
			fileReader.close();
		}
		catch (FileNotFoundException e1) {
			System.out.println(scriptFile + " not found " + e1);
		}
		catch (IOException e2) {
			System.out.println("IO problem with " + scriptFile + e2);
		}
		catch (ScriptException e3) {
			System.out.println("ScriptException in " + scriptFile + e3);
		}
		catch (NullPointerException e4) {
			System.out.println("Null pointer exception reading " + scriptFile + e4);
		}
	}

	private void checkForCollisions()
	{
		com.bulletphysics.dynamics.DynamicsWorld dynamicsWorld;
		com.bulletphysics.collision.broadphase.Dispatcher dispatcher;
		com.bulletphysics.collision.narrowphase.PersistentManifold manifold;
		com.bulletphysics.dynamics.RigidBody object1, object2;
		com.bulletphysics.collision.narrowphase.ManifoldPoint contactPoint;
		dynamicsWorld = ((JBulletPhysicsEngine)physicsEngine).getDynamicsWorld();
		dispatcher = dynamicsWorld.getDispatcher();
		int manifoldCount = dispatcher.getNumManifolds();
		for (int i=0; i<manifoldCount; i++)
		{ 
			manifold = dispatcher.getManifoldByIndexInternal(i);
			object1 = (com.bulletphysics.dynamics.RigidBody)manifold.getBody0();
			object2 = (com.bulletphysics.dynamics.RigidBody)manifold.getBody1();
			JBulletPhysicsObject obj1 = JBulletPhysicsObject.getJBulletPhysicsObject(object1);
			JBulletPhysicsObject obj2 = JBulletPhysicsObject.getJBulletPhysicsObject(object2);
			for (int j = 0; j < manifold.getNumContacts(); j++)
			{ 
				contactPoint = manifold.getContactPoint(j);
				if (contactPoint.getDistance() < 0.0f)
				{ 
					System.out.println("---- hit between " + obj1 + " and " + obj2);
					break;
				}
			}
		} 
	}


	private float[] toFloatArray(double[] arr)
	{ 
		if (arr == null)
		{
			return null;
		}
		int n = arr.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++)
		{ 
			ret[i] = (float)arr[i];
		}
		return ret;
	}
	private double[] toDoubleArray(float[] arr)
	{
		if (arr == null)
		{
			return null;
		}
		int n = arr.length;
		double[] ret = new double[n];
		for (int i = 0; i < n; i++)
		{ 
			ret[i] = (double)arr[i];
		}
		return ret;
	}


	// ---------- NETWORKING SECTION ----------------

	public ObjShape getGhostShape() { return ghostS; }
	public TextureImage getGhostTexture() { return ghostT; }
	public GhostManager getGhostManager() { return gm; }
	public Engine getEngine() { return engine; }
	
	private void setupNetworking()
	{	isClientConnected = false;	
		try 
		{	protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, this);
		} 	catch (UnknownHostException e) 
		{	e.printStackTrace();
		}	catch (IOException e) 
		{	e.printStackTrace();
		}
		if (protClient == null)
		{	System.out.println("missing protocol host");
		}
		else
		{	// Send the initial join message with a unique identifier for this client
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
		}
	}
	
	protected void processNetworking(float elapsTime)
	{	// Process packets received by the client from the server
		if (protClient != null)
			protClient.processPackets();
	}

	public Vector3f getPlayerPosition() { return avatar.getWorldLocation(); }

	public void setIsConnected(boolean value) { this.isClientConnected = value; }
	
	private class SendCloseConnectionPacketAction extends AbstractInputAction
	{	@Override
		public void performAction(float time, net.java.games.input.Event evt) 
		{	if(protClient != null && isClientConnected == true)
			{	protClient.sendByeMessage();
			}
		}
	}
}