package net.sf.robocode.roborumble.structures;

import net.sf.robocode.roborumble.battlesengine.BattlesRunner;
import net.sf.robocode.roborumble.netengine.DownloadFailedException;
import net.sf.robocode.roborumble.structures.builders.ServerObjectBuilder;
import robocode.BattleResults;
import robocode.control.RobotResults;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class Battle extends ServerObject {
    /*
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    game    INTEGER REFERENCES games,
    numbots INTEGER
     */

    private BattleResult[] results = null;
    private Bot[] bots;
    private int numbots;
    private Game game;

    public Battle() {

    }

    public Battle(Bot[] bots) {
        this.bots = bots;
        System.out.println(this);
    }

    public void setResults(RobotResults[] roboResults) {
        this.numbots = roboResults.length;
        results = new BattleResult[this.numbots];
        for (int i = 0; i < this.numbots; i++) {
            results[i] = new BattleResult(roboResults[i]);
        }
    }

    public Game getGame() {
        return game;
    }

    public Bot[] getBots() {
        return bots;
    }

    public boolean foughtYet() {
        return results != null;
    }

    public Battle setResults(BattleResults[] sortedResults) {
        System.out.println(sortedResults[0].getScore());
        return this;
    }

    public void save() throws UploadFailedException {
        try {
            URL url = new URL(this.game.getTournament().url + this.game.uri + "/battles");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.getOutputStream();

            ServerObjectBuilder.getGson().toJson(this, new OutputStreamWriter(conn.getOutputStream()));
            System.out.println("done, in theory!");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            throw new UploadFailedException();
        }
    }

    @Override
    public String toString(){
        return bots[0] + " Vs " + bots[1];
    }

    public void run(BattlesRunner r) throws DownloadFailedException{
        System.out.println("Running " + this.toString());
        for (Bot b : bots) {
            b.ensureBotDownloaded();
        }

        r.runBattle(this);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public BattleResult[] getResults() {
        return results;
    }
}