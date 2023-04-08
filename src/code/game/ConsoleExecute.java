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


        //Check if map exists
        if(AssetManager.load("/maps/"+Map+"/map.ini") == null){
            System.out.println("Map not found!");

            //Just in case a map doesn't exist to stop it from crashing
            if(game.currentMap == null){
                game.loadMap(main.gamecfg.get("game", "start_map"));
            }
            return;
        }

        //Load Map
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
