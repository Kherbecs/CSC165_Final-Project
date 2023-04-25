package tage.nodeControllers;
import tage.*;
import org.joml.*;

/**
* A BobController controls how much the targeted object moves up and down.
* It moves the targeted object up and down on a cycle.
* A BobController instance has fields for the direction, the speed, 
* cycleTime, totalTime. It also includes Vector3fs for oldPosiiton, newPosition,
* and direction3f. It also has a field for the engine. By default, the cycle time is
* 2 seconds.
*/
public class BobController extends NodeController {
    private float direction = 1.0f;
    private float bobSpeed = 0.01f;
    private float cycleTime = 2000.0f;
    private float totalTime = 0.0f;
    private Vector3f oldPosition, newPosition, direction3f;
    private Engine engine;
    
    /** creates a BobController */
    public BobController(Engine e, float cycleTime) {
        super();
        this.cycleTime = cycleTime;
        engine = e;
    }
    /** applies the BobController to the given GameObject */
    public void apply(GameObject go) {
        float elapsedTime = super.getElapsedTime();
        totalTime += elapsedTime/1000.0f;
        if (totalTime > cycleTime) {
            direction = -direction;
            totalTime = 0.0f;
        }
        oldPosition = go.getLocalLocation();
        direction3f = new Vector3f(0.0f, direction, 0.0f);
        direction3f.mul(0.01f * (float)elapsedTime);
        newPosition = oldPosition.add(direction3f.x(), direction3f.y(), direction3f.z());
        go.setLocalLocation(newPosition);
    }
}