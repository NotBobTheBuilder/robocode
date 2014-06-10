package net.sf.robocode.roborumble.structures;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class Tournament {

    private static Tournament TOURNAMENT = null;

    public Game[] games;
    public String url;

    public Tournament() {
    }

    public void setGames(Game[] games) {
        this.games = games;
        for (Game g : this.games) {
            g.setTournament(this);
        }
    }

    public Game getDefaultGame() {
        for (Game g : this.games) {
            if (g.isDefault()) {
                return g;
            }
        }
        return null;
    }

    public static Tournament getInstance() {
        if (TOURNAMENT == null) {
            TOURNAMENT = new Tournament();
        }
        return TOURNAMENT;
    }

    @Override
    public String toString() {
        return "Tournament " + this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
