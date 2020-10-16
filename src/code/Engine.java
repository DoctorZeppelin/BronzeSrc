package code;

import code.game.Main;
import code.audio.AudioEngine;
import code.game.world.entities.Player;
import code.utils.Keys;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Scanner;
import javax.imageio.ImageIO;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author Roman Lahin
 */
public class Engine {
    
    public static Main main;
    public static long window;
    public static boolean hideCursor;
    
    public static int w, h;

    public static KeyInput keyInputCallback;
    public static MouseCallback mouseCallback;
    public static ScrollCallback scrollCallback;
    public static ResizeCallback resizeCallback;
    
    public static void main(String[] args) {
        System.out.println("ultra wacky\n"
                + "UHHHHHHHHHHHHHHHHHHHHHHH.....\n"
                + "engine\n\n"
                + "(UWUE 0.0)");
        
        Scanner sn = new Scanner(System.in);
        
        /*Globals globals = JsePlatform.standardGlobals();
        LuaTable gameVals = new LuaTable();
        gameVals.set("hp", LuaValue.valueOf(100));
        globals.set("game", gameVals);
        
        OneArgFunction func = new OneArgFunction() {
            public LuaValue call(LuaValue arg) {
                return LuaValue.valueOf(arg.toint() + 10);
            }
        };
        gameVals.set("testFunc", func);
        
        while(true) {
            String code = sn.next();
            if(code.equals("exit")) break;
            try {
                LuaValue chunk = globals.load(code);
                chunk.call();
                System.out.println(gameVals.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        
        Keys.UP = Keys.addKeyToBinding(Keys.UP, GLFW.GLFW_KEY_UP);
        Keys.DOWN = Keys.addKeyToBinding(Keys.DOWN, GLFW.GLFW_KEY_DOWN);
        Keys.LEFT = Keys.addKeyToBinding(Keys.LEFT, GLFW.GLFW_KEY_LEFT);
        Keys.RIGHT = Keys.addKeyToBinding(Keys.RIGHT, GLFW.GLFW_KEY_RIGHT);
        Keys.OK = Keys.addKeyToBinding(Keys.OK, GLFW.GLFW_KEY_ENTER);
        Keys.ESC = Keys.addKeyToBinding(Keys.ESC, GLFW.GLFW_KEY_ESCAPE);
        
        Player.initKeys(GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_SPACE);
        Main.TILDE = Keys.addKeyToBinding(Main.TILDE, GLFW.GLFW_KEY_GRAVE_ACCENT);
        
        main = new Main();
        
        keyInputCallback = new KeyInput(main);
        mouseCallback = new MouseCallback(main);
        scrollCallback = new ScrollCallback(main);
        resizeCallback = new ResizeCallback(main);
        
        init();
        AudioEngine.init();
        
        main.init();
    }
    
    static void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

        w = vidmode.width(); h = vidmode.height();

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 1);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);
        
        window = GLFW.glfwCreateWindow(w, h,
                "UWUE", GLFW.glfwGetPrimaryMonitor(), NULL);
        
        if(window == NULL) {
            throw new IllegalStateException("Unable to create GLFW Window");
        }

        GLFW.glfwMakeContextCurrent(window);
        /*
        I have weird issues with vsync in windowed mode. 
        For some reason fps drops to 30 frames, so i disabled vsync
        */
        GLFW.glfwSwapInterval(0); //vsync
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();
        
        GLFW.glfwSetKeyCallback(window, keyInputCallback);
        GLFW.glfwSetWindowSizeCallback(window, resizeCallback);
        GLFW.glfwSetMouseButtonCallback(window, mouseCallback);
        GLFW.glfwSetScrollCallback(window, scrollCallback);
    }
    
    public static void setTitle(String title) {
        if(title != null) GLFW.glfwSetWindowTitle(window, title);
    }

    public static void hideCursor(boolean hide) {
        if(!hide) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        } else {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
            GLFW.glfwSetCursorPos(window, w >> 1, h >> 1);
        }

        hideCursor = hide;
    }
    
    public static void destroy() {
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();

        AudioEngine.close();
    }
    
    public static void takeScreenshot() {
        GL11.glReadBuffer(GL11.GL_FRONT);
        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(w * h * bpp);
        GL11.glReadPixels(0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        
        try {
            File file = new File("screenshots/");
            if(!file.exists()) file.mkdir();
            
            Calendar cal = Calendar.getInstance();
            String data = cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-"
                    + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + "." 
                    + cal.get(Calendar.MINUTE) + "." + cal.get(Calendar.SECOND);
            file = new File("screenshots/"+data+".png");
            
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for(int x = 0; x < w; x++) {
                for(int y = 0; y < h; y++) {
                    int i = (x + (w * y)) * bpp;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    image.setRGB(x, h - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
                }
            }

            ImageIO.write(image, "PNG", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
