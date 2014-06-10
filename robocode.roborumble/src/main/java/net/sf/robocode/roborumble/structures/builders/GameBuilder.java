package net.sf.robocode.roborumble.structures.builders;

import com.google.gson.*;
import net.sf.robocode.roborumble.structures.Game;

import java.lang.reflect.Type;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class GameBuilder implements JsonDeserializer<Game> {
    @Override
    public Game deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject game = jsonElement.getAsJsonObject();

        String gameUrl = game.get("url").getAsString();
        String gameName = game.get("name").getAsString();

        if (game.has("type")) {
            String gameType = game.get("type").getAsString();
            int gameFieldWidth = game.get("fieldw").getAsInt();
            int gameFieldHeight = game.get("fieldh").getAsInt();
            int gameRoundCount = game.get("rounds").getAsInt();

            Game g = Game.getInstance(gameUrl, gameName);
            g.update(gameType, gameFieldWidth, gameFieldHeight, gameRoundCount);
            return g;
        } else {
            return Game.getInstance(gameUrl, gameName);
        }
    }
}
