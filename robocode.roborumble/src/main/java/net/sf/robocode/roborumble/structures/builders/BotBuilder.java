package net.sf.robocode.roborumble.structures.builders;

import com.google.gson.*;
import net.sf.robocode.roborumble.structures.Bot;

import java.lang.reflect.Type;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class BotBuilder implements JsonDeserializer<Bot> {
    @Override
    public Bot deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject bot = jsonElement.getAsJsonObject();
        return Bot.getInstance(bot);
    }
}
