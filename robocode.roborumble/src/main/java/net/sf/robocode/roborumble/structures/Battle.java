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

    private BattleResult[] results;
    private int numbots;
    private Game game;

    public Battle() {

    }

    public Battle(RobotResults[] roboResults) {
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
        return null;
    }

    public Battle setResults(BattleResults[] sortedResults) {
        return this;
    }

    public void save() {

    }
}