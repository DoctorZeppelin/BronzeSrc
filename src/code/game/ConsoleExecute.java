package code.game;

import code.game.scripting.Scripting;
import code.math.Vector3D;
import code.utils.IniFile;
import code.utils.StringTools;
import code.utils.assetManager.AssetManager;
import org.luaj.vm2.LuaValue;

import java.util.Hashtable;

public class ConsoleExecute {
    public static void Lua(Main main, String ConsoleText){

        LuaValue val = Scripting.runScript(main, ConsoleText);
        if(!val.isnil()) {
            System.out.println("bool " + val.toboolean());
            System.out.println("int " + val.toint());
            System.out.println("num " + val.todouble());
            System.out.println("str " + val.tojstring());
        }

    }

    public static void Map(Game game, String Map){

        String[] lines = AssetManager.loadLines("/maps/"+Map+"/map.ini");
        IniFile lvl = new IniFile(new Hashtable());
        lvl.set(lines, true);

        if(lvl.groupExists("player")) {
            String tmp = lvl.get("player", "pos");
            if(tmp != null) {
                float[] pPos = StringTools.cutOnFloats(tmp, ',');
                game.player.pos.set(pPos[0], pPos[1], pPos[2]);
            }

            game.player.rotX = lvl.getFloat("player", "rot_x", 0);
            game.player.rotY = lvl.getFloat("player", "rot_y", 0);
        }
        game.loadMap(Map);
    }

    public static void Reload(Game game){
        if(game != null) {
            game.loadMap(game.currentMap,
                    new Vector3D(game.player.pos),
                    game.player.rotX,
                    game.player.rotY);
        }
    }
}
