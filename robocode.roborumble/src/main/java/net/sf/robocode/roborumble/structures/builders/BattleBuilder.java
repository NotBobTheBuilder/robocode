package net.sf.robocode.roborumble.structures.builders;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.sf.robocode.roborumble.structures.Battle;
import net.sf.robocode.roborumble.structures.Bot;

import java.lang.reflect.Type;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class BattleBuilder implements JsonDeserializer<Battle> {

    @Override
    public Battle deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Battle(ServerObjectBuilder.getGson().fromJson(jsonElement.getAsJsonArray(), Bot[].class));
    }
}
