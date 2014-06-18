package net.sf.robocode.roborumble.structures.builders;

import com.google.gson.*;
import net.sf.robocode.roborumble.netengine.DownloadFailedException;
import net.sf.robocode.roborumble.netengine.FileTransfer;
import net.sf.robocode.roborumble.structures.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public abstract class ServerObjectBuilder<T extends ServerObject> implements JsonDeserializer<T>, JsonSerializer {
 // TODO: JSON API transfers in this class
 // TODO: Singleton pattern for parsing objects (only one bot exists per ID/URL)
 // TODO: If-Changed-Since support

    private static Gson gson = null;

    public static JsonElement getJSONFromServer(URL url) throws DownloadFailedException {
        InputStream in;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();

            // Allow both GZip and Deflate (ZLib) encodings
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "RoboRumble@Home - gzip, deflate");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("No HTTP OK in ServerObjectBuilder.getJSONFromServer");
                System.err.println(conn.getResponseCode());
                System.err.println(url.toString());
                throw new DownloadFailedException("in ServerObjectBuilder::getJSONFromServer: got non-200 HTTP code");
            }

            in = FileTransfer.getInputStream(conn);
            return new JsonParser().parse(new BufferedReader(new InputStreamReader(in)).readLine());
        } catch (IOException e) {
         e.printStackTrace(System.err);
            throw new DownloadFailedException("in ServerObjectBuilder::getJSONFromServer: Error downloading JSON");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static Tournament getTournament(URL url) throws DownloadFailedException {
        return getGson().fromJson(getJSONFromServer(url), Tournament.class);
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Tournament.class, new TournamentBuilder())
                    .registerTypeAdapter(Game.class, new GameBuilder())
                    .registerTypeAdapter(Bot.class, new BotBuilder("./roborumble/temp/", "./robots/"))
                    .registerTypeAdapter(Battle.class, new BattleBuilder())
                    .registerTypeAdapter(BattleResult.class, new BattleResultBuilder())
                    .create();
        }
        return gson;
    }
}
