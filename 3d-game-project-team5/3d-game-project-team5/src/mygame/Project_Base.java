package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Project_Base extends SimpleApplication implements ActionListener {
    private Node sceneNode;
    private BulletAppState bulletAppState;
    private RigidBodyControl scenePhy;
    private Node playerNode;
    private BetterCharacterControl playerControl;
    private CameraNode camNode;
    private final Vector3f walkDirection = new Vector3f(0,0,0);
    private final Vector3f viewDirection = new Vector3f(0,0,1);
    private boolean rotateLeft = false, rotateRight = false,
    forward = false, backward = false;
    private final float speed=8;
    
    public static void main(String[] args) {
        Project_Base app = new Project_Base();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        inputManager.addMapping("Forward",
        new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Back",
        new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Rotate Left",
        new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Rotate Right",
        new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump",
        new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Rotate Left",
        "Rotate Right");
        inputManager.addListener(this, "Forward", "Back", "Jump");
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        assetManager.registerLocator("town.zip", ZipLocator.class);
        sceneNode = (Node)assetManager.loadModel("main.scene");
        sceneNode.scale(1.5f);
        scenePhy = new RigidBodyControl(0f);
        sceneNode.addControl(scenePhy);
        bulletAppState.getPhysicsSpace().add(sceneNode);

        rootNode.attachChild(sceneNode);
        
        AmbientLight ambient = new AmbientLight();
        rootNode.addLight(ambient);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1.4f, -1.4f, -1.4f));
        rootNode.addLight(sun);
        viewPort.setBackgroundColor(ColorRGBA.Cyan);
        
        playerNode = new Node("the player");
        playerNode.setLocalTranslation(new Vector3f(0, 6, 0));
        rootNode.attachChild(playerNode);
        
        playerControl = new BetterCharacterControl(1.5f, 4, 30f);
        playerControl.setJumpForce(new Vector3f(0, 200, 0));
        playerControl.setGravity(new Vector3f(0, -10, 0));
        playerNode.addControl(playerControl);
        bulletAppState.getPhysicsSpace().add(playerControl);
    }
   

    @Override
    public void simpleUpdate(float tpf) {
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.
        ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 4, -6));
        Quaternion quat = new Quaternion();
        quat.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
        camNode.setLocalRotation(quat);
        playerNode.attachChild(camNode);
        camNode.setEnabled(true);
        flyCam.setEnabled(false);
        // Get current forward and left vectors of the playerNode:
        Vector3f modelForwardDir =
        playerNode.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDir =
        playerNode.getWorldRotation().mult(Vector3f.UNIT_X);
        // Determine the change in direction
        walkDirection.set(0, 0, 0);
        if (forward) {
            walkDirection.addLocal(modelForwardDir.mult(speed));
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.mult(speed).
            negate());
        }
        playerControl.setWalkDirection(walkDirection); // walk!
        // Determine the change in rotation
        if (rotateLeft) {
            Quaternion rotateL = new Quaternion().
            fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDirection);
        } else if (rotateRight) {
            Quaternion rotateR = new Quaternion().
            fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDirection);
        }
        playerControl.setViewDirection(viewDirection); // turn!
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        switch (binding) {
            case "Rotate Left" -> rotateLeft = isPressed;
            case "Rotate Right" -> rotateRight = isPressed;
            case "Forward" -> forward = isPressed;
            case "Back" -> backward = isPressed;
            case "Jump" -> playerControl.jump();
            default -> {
            }
        }
    }
}
