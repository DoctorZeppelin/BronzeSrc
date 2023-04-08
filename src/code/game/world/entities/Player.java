package code.game.world.entities;

import code.engine3d.HudRender;
import code.engine.Screen;
import code.game.Main;
import code.game.world.World;

import code.utils.FPS;
import code.utils.Keys;
import code.game.Game;

import java.awt.geom.Rectangle2D;

//import code.game.Main;

/**
 *
 * @author Roman Lahin
 */
public class Player extends PhysEntity {

    public static int WALK_FORWARD, WALK_BACKWARD, STRAFE_LEFT, STRAFE_RIGHT, JUMP, RUN, INVENTORY, DEBUG,
            INTERACT;
    public static String[] inventorySlots = new String[6];

    public float eyeHeight;
    public float rotX;



    public Player() {
        name = "player";
        setSize(25, 180);
        eyeHeight = 180-15;
        inventorySlots[1] = "Slot 1";
    }

    public void update(World world) {
        float speed = Keys.isPressed(RUN) ? 25.5f : 19;
		//40 and 30 is release speed
        //35 is pre-release speed
        
        walk(
            ((Keys.isPressed(WALK_FORWARD)?1:0) - (Keys.isPressed(WALK_BACKWARD)?1:0)) * speed,
            ((Keys.isPressed(STRAFE_RIGHT)?1:0) - (Keys.isPressed(STRAFE_LEFT)?1:0)) * speed
		); 
        
        if(Keys.isPressed(JUMP)) jump(50);
        
        super.update(world);

        if(Keys.isPressed((INVENTORY)))
        {
            System.out.println(Player.inventorySlots[1]);
        }

    }
    
    public static void initKeys(int w, int s, int a, int d, int space, int shift, int e, int tab, int F3) {
        WALK_FORWARD = Keys.addKeyToBinding(WALK_FORWARD, w);
        WALK_BACKWARD = Keys.addKeyToBinding(WALK_BACKWARD, s);
        STRAFE_LEFT = Keys.addKeyToBinding(STRAFE_LEFT, a);
        STRAFE_RIGHT = Keys.addKeyToBinding(STRAFE_RIGHT, d);
        JUMP = Keys.addKeyToBinding(JUMP, space);
        RUN = Keys.addKeyToBinding(RUN, shift);
        INVENTORY = Keys.addKeyToBinding(INVENTORY, tab);

        DEBUG = Keys.addKeyToBinding(DEBUG, F3);
        INTERACT = Keys.addKeyToBinding(INTERACT, e);
    }

}
