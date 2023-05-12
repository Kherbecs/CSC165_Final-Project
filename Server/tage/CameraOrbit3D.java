package tage;
import tage.*;
import java.lang.Math;
import org.joml.*;
import tage.input.*; 
import tage.input.action.*; 
import net.java.games.input.*; 
import net.java.games.input.Component.Identifier.*;
import net.java.games.input.Event;
import tage.input.action.AbstractInputAction;

/** A CameraOrbit3D class. This class orbits the camera around the avatar. It has
* fields for the engine, a camera, a GameObject, the orbit around the GameObject, the 
* camera's elevation, and the distance from the GameObject. It has a constructor
* a function to set up inputs, and update the camera position. It has private classes to
* help with the movement of the camera.
*/
public class CameraOrbit3D {
    private Engine engine;
    private Camera camera;
    private GameObject avatar;
    private float cameraAzimuth;
    private float cameraElevation;
    private float cameraRadius;

    /** creates a CameraOrbit3D */
    public CameraOrbit3D(Camera cam, GameObject av, Engine e, float azimuth, float elevation, float radius) {
        engine = e;
        camera = cam;
        avatar = av;
        cameraAzimuth = azimuth;
        cameraElevation = elevation;
        cameraRadius = radius;
        setupInputs();
        updateCameraPosition();
    }
    /** sets up the inputs for the camera */
    private void setupInputs() {
        OrbitAzimuthAction azmAction = new OrbitAzimuthAction();
        OrbitElevationAction elevAction = new OrbitElevationAction();
        OrbitAzimuthActionKBLeft orbitAzimuthActionKBLeft = new OrbitAzimuthActionKBLeft();
        OrbitAzimuthActionKBRight orbitAzimuthActionKBRight = new OrbitAzimuthActionKBRight();
        OrbitElevationActionKBUp orbitElevationActionKBUp = new OrbitElevationActionKBUp();
        OrbitElevationActionKBDown orbitElevationActionKBDown = new OrbitElevationActionKBDown();
        OrbitRadiusActionKBIn orbitRadiusActionKBIn = new OrbitRadiusActionKBIn();
        OrbitRadiusActionKBOut orbitRadiusActionKBOut = new OrbitRadiusActionKBOut();
        InputManager im = engine.getInputManager();
        im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RX, azmAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Axis.RY, elevAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._6, orbitRadiusActionKBIn, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllGamepads(net.java.games.input.Component.Identifier.Button._7, orbitRadiusActionKBOut, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.LEFT, orbitAzimuthActionKBLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.RIGHT, orbitAzimuthActionKBRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.UP, orbitElevationActionKBUp, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.DOWN, orbitElevationActionKBDown, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.N, orbitRadiusActionKBIn, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        im.associateActionWithAllKeyboards(net.java.games.input.Component.Identifier.Key.M, orbitRadiusActionKBOut, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    }
    /** moves the camera to the correct position */
    public void updateCameraPosition() {
        Vector3f avatarRot = avatar.getWorldForwardVector();
        double avatarAngle = Math.toDegrees((double)avatarRot.angleSigned(new Vector3f(0, 0, -1), new Vector3f(0, 1, 0)));
        float totalAz = cameraAzimuth - (float)avatarAngle;
        double theta = Math.toRadians(cameraAzimuth);
        double phi = Math.toRadians(cameraElevation);
        float x = cameraRadius * (float)(Math.cos(phi)*Math.sin(theta));
        float y = cameraRadius * (float)(Math.sin(phi));
        float z = cameraRadius * (float)(Math.cos(phi)*Math.cos(theta));
        camera.setLocation(new Vector3f(x, y, z).add(avatar.getWorldLocation()));
        camera.lookAt(avatar);
    }
    /** controller input class, moves the camera "horizontally" */
    private class OrbitAzimuthAction extends AbstractInputAction {
        public void performAction(float time, Event e) {
            float rotAmount;
            if (e.getValue() < -0.2) {
                rotAmount = 1f; 
            } else {
                if (e.getValue() > 0.2) {
                    rotAmount = -0.5f;
                } else {
                    rotAmount = 1f;
                }
            }
            cameraAzimuth += rotAmount;
            cameraAzimuth = cameraAzimuth % 360;
            updateCameraPosition();
        }
    }
    /** controller input class, moves camera "vertically" */
    private class OrbitElevationAction extends AbstractInputAction {
        public void performAction(float time, Event e) {
            float elevAmt;
            if (e.getValue() < -0.2) {
                elevAmt = 1f;
            } else {
                if (e.getValue() > 0.2) {
                    elevAmt = -1f;
                } else {
                    elevAmt = 0.0f;
                }
            }
            cameraElevation += elevAmt;
            if (cameraElevation > 85f) {
                cameraElevation = 85f;
            }
            if (cameraElevation < 5f) {
                cameraElevation = 5f;
            }
            updateCameraPosition();
        }
    }
    /** keyboard class, moves camera left */
    private class OrbitAzimuthActionKBLeft extends AbstractInputAction {
        public void performAction(float time, Event e) {
            float rotAmount;
            rotAmount = 1f;
            cameraAzimuth += rotAmount;
            cameraAzimuth = cameraAzimuth % 360;
            updateCameraPosition();
        }
    }
    /** keyboard class, moves camera right */
    private class OrbitAzimuthActionKBRight extends AbstractInputAction {
        public void performAction(float time, Event e) {
            float rotAmount;
            rotAmount = -1f;
            cameraAzimuth += rotAmount;
            cameraAzimuth = cameraAzimuth % 360;
            updateCameraPosition();
        }
    }
    /** keyboard class, moves camera up */
    private class OrbitElevationActionKBUp extends AbstractInputAction {
        public void performAction(float time, Event e) {
            float elevAmt;
            elevAmt = 1f;
            cameraElevation += elevAmt;
            if (cameraElevation > 85f) {
                cameraElevation = 85f;
            }
            updateCameraPosition();
        }
    }
    /** keyboard class, moves camera down */
    private class OrbitElevationActionKBDown extends AbstractInputAction {
        public void performAction(float time, Event e) {
            float elevAmt;
            elevAmt = -1f;
            cameraElevation += elevAmt;
            if (cameraElevation < 5f) {
                cameraElevation = 5f;
            }
            updateCameraPosition();
        }
    }
    /** keyboard class, zooms camera in */
    private class OrbitRadiusActionKBIn extends AbstractInputAction {
        public void performAction(float time, Event e) {
            float radiusAmt;
            radiusAmt = -0.05f;
            cameraRadius += radiusAmt;
            if (cameraRadius < 10f) {
                cameraRadius = 10f;
            }
            updateCameraPosition();
        }
    }
    /** keyboard class, zooms camera out */
    private class OrbitRadiusActionKBOut extends AbstractInputAction {
        public void performAction(float time, Event e) {
            float radiusAmt;
            radiusAmt = 0.05f;
            cameraRadius += radiusAmt;
            if (cameraRadius > 40f) {
                cameraRadius = 40f;
            }
            updateCameraPosition();
        }
    }
}