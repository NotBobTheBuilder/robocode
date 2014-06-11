package net.sf.robocode.roborumble.structures;

import com.google.gson.JsonElement;
import net.sf.robocode.roborumble.netengine.DownloadFailedException;
import net.sf.robocode.roborumble.structures.builders.ServerObjectBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class Game extends ServerObject implements Iterator<Battle>, Iterable<Battle> {

    private String name = null;
    private String type;
    private int fieldWidth;
    private int fieldHeight;
    private Bot[] bots = null;
    private Queue<Battle> battleQueue = new LinkedList<Battle>();
    private int rounds;

    private Tournament tournament;

    private static HashMap<String, Game> GAMES = new HashMap<String, Game>();

    private Game(String uri, String name) {
        this.uri = uri;
        this.name = name;
        this.downloaded = false;

        GAMES.put(uri, this);
    }

    public static Game getInstance(String uri, String name) {
        if (GAMES.get(uri) == null) {
            GAMES.put(uri, new Game(uri, name));
        }
        return GAMES.get(uri);
    }

    public void update(String type, int fieldWidth, int fieldHeight, int roundCount) {
        this.type = type;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        this.rounds = roundCount;

        this.downloaded = true;
    }

    public int getFieldHeight() {
        return this.fieldHeight;
    }

    public int getFieldWidth() {
        return this.fieldWidth;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDefault() {
        return true;
    }

    public Bot[] getBots() throws DownloadFailedException {
        if (bots == null) {
            try {
                ServerObjectBuilder.getJSONFromServer(new URL(this.tournament.url + this.uri) );
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return this.bots;
    }

    public int getRounds() {
        return this.rounds;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public void downloadBots() throws DownloadFailedException {
        downloadBots("./roborumble/temp/", "./robots");
    }

    public void downloadBots(String tempDir, String botsRepo) throws DownloadFailedException {
        for (Bot b : this.getBots()) {
            boolean downloaded = b.ensureBotDownloaded(tempDir, botsRepo);
            if (!downloaded) {
                System.err.println("Could not download " + b.getName());
            }
        }
    }

    @Override
    public boolean hasNext() {
        if (battleQueue.size() == 0) {
            JsonElement queue;
            try {
                queue = ServerObjectBuilder.getJSONFromServer(new URL(tournament.url + this.uri + "battlequeue"));
                for (Battle b : ServerObjectBuilder.getGson().fromJson(queue.getAsJsonArray(), Battle[].class)) {
                    battleQueue.add(b);
                }
            } catch (DownloadFailedException e) {
                return false;
            } catch (MalformedURLException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Battle next() {
        return battleQueue.remove();
    }

    @Override
    public void remove() {

    }

    @Override
    public Iterator<Battle> iterator() {
        return this;
    }

}
