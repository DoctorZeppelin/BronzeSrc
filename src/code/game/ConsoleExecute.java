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

    public static void Map(Game game, String Map, Main main){


        //Load Map
        if(AssetManager.load("/maps/"+Map+"/map.ini") == null){
            System.out.println("Map not found!");
            if(game.currentMap == null){
                System.out.println(game.currentMap);
                game.loadMap(main.gamecfg.get("game", "start_map"));
            }
            return;
        }

        game.loadMap(Map);
        System.out.println("Loading "+Map+"...");


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
