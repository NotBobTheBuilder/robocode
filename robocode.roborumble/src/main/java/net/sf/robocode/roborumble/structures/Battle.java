package net.sf.robocode.roborumble.structures;

import robocode.BattleResults;
import robocode.control.RobotResults;

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

    public void save() {

    }
}