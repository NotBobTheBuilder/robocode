package net.sf.robocode.roborumble.structures.builders;

import com.google.gson.*;
import net.sf.robocode.roborumble.structures.Battle;
import net.sf.robocode.roborumble.structures.Bot;

import java.lang.reflect.Type;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class BattleBuilder implements JsonDeserializer<Battle>, JsonSerializer<Battle> {

    @Override
    public Battle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Battle(ServerObjectBuilder.getGson().fromJson(jsonElement.getAsJsonArray(), Bot[].class));
    }

    @Override
    public JsonElement serialize(Battle battle, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonBattle = new JsonObject();
        jsonBattle.addProperty("numbots", battle.getBots().length);
        jsonBattle.addProperty("game", battle.getGame().getName());
        jsonBattle.add("results", ServerObjectBuilder.getGson().toJsonTree(battle.getResults()));
        return null;
    }
}
