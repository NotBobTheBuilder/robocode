package net.sf.robocode.roborumble.structures;

import robocode.control.RobotResults;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class BattleResult extends ServerObject {
    /*
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    battle  INTEGER REFERENCES battles,
    bot     INTEGER REFERENCES bots,
    rank    INTEGER NOT NULL,
    score   INTEGER NOT NULL,
    damage  INTEGER NOT NULL,
    firsts  INTEGER NOT NULL
     */
    private String bot;
    private int rank;
    private int score;
    private int damage;
    private int firsts;

    public BattleResult() {

    }

    public BattleResult(String bot, int rank, int score, int damage, int firsts) {
        this.bot = bot;
        this.rank = rank;
        this.score = score;
        this.damage = damage;
        this.firsts = firsts;
    }

    public BattleResult(RobotResults roboResult) {
        this(roboResult.getRobot().getName(),
                roboResult.getRank(),
                roboResult.getScore(),
                roboResult.getBulletDamage(),
                roboResult.getFirsts()
        );
    }
}
