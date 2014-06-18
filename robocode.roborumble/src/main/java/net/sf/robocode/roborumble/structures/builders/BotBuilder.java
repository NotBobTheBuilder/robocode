package net.sf.robocode.roborumble.structures.builders;

import com.google.gson.*;
import net.sf.robocode.roborumble.structures.Bot;

import java.lang.reflect.Type;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class BotBuilder implements JsonDeserializer<Bot> {

    private final String repo;
    private final String tempDir;

    public BotBuilder(String tempDir, String repo) {
        this.tempDir = tempDir;
        this.repo = repo;
    }
    @Override
    public Bot deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject bot = jsonElement.getAsJsonObject();

        Bot b = Bot.getInstance(bot);
        b.setTempDir(tempDir);
        b.setBotsRepo(repo);
        return b;
    }
}
