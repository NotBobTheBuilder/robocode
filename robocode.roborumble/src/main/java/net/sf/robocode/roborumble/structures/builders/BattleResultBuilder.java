package net.sf.robocode.roborumble.structures.builders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.sf.robocode.roborumble.structures.BattleResult;

import java.lang.reflect.Type;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class BattleResultBuilder implements JsonSerializer<BattleResult>{
    @Override
    public JsonElement serialize(BattleResult battleResult, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonResult = new JsonObject();

        jsonResult.addProperty("damage", battleResult.getDamage());
        jsonResult.addProperty("bot", battleResult.getBot());
        jsonResult.addProperty("score", battleResult.getScore());
        jsonResult.addProperty("rank", battleResult.getRank());
        jsonResult.addProperty("firsts", battleResult.getFirsts());

        return jsonResult;
    }
}
