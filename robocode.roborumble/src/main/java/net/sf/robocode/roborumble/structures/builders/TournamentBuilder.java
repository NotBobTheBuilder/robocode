package net.sf.robocode.roborumble.structures.builders;

import com.google.gson.*;
import net.sf.robocode.roborumble.structures.Game;
import net.sf.robocode.roborumble.structures.Tournament;

import java.lang.reflect.Type;

public class TournamentBuilder implements JsonDeserializer<Tournament> {

    @Override
    public Tournament deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject tournament = jsonElement.getAsJsonObject();
        JsonArray gameArray = tournament.get("games").getAsJsonArray();

        Tournament t = Tournament.getInstance();
        t.setUrl(tournament.get("url").getAsString());
        t.setGames(ServerObjectBuilder.getGson().fromJson(gameArray, Game[].class));
        return t;
    }

}