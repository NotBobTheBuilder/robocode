/*******************************************************************************
 * Copyright (c) 2003-2014 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.roborumble.battlesengine;


import net.sf.robocode.io.Logger;
import net.sf.robocode.roborumble.structures.Battle;
import net.sf.robocode.roborumble.structures.Bot;
import net.sf.robocode.roborumble.structures.UploadFailedException;
import robocode.control.*;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.BattleErrorEvent;


/**
 * The BattlesRunner is running battles.
 * Reads a file with the battles to be runned and outputs the results in another file.
 * Controlled by properties files.
 *
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 * @author Joachim Hofer (contributor)
 */
public class BattlesRunner {
	private static RobotResults[] lastResults;
	private IRobocodeEngine engine;

	public BattlesRunner() {
	}

	private IRobocodeEngine getEngine() {
		if (engine == null) {
			engine = new RobocodeEngine();
		}
        return engine;
	}

	public void runBattle(final Battle battle) {
        engine.addBattleListener(new BattleAdaptor() {

            @Override
            public void onBattleError(final BattleErrorEvent event) {
                Logger.realErr.println(event.getError());
                engine.removeBattleListener(this);
            }

            @Override
            public void onBattleCompleted(final BattleCompletedEvent event) {
                try {
                    battle.setResults(event.getSortedResults()).save();
                } catch (UploadFailedException e) {
                    e.printStackTrace();
                }
                System.out.println("Whoosh!");
                engine.removeBattleListener(this);
            }
        });
		// Initialize objects
		BattlefieldSpecification field = new BattlefieldSpecification(battle.getGame().getFieldWidth(), battle.getGame().getFieldHeight());
		BattleSpecification battleField = new BattleSpecification(battle.getGame().getRounds(), field, (new RobotSpecification[2]));

        int index = 0;

        String enemies = getEnemies(battle.getBots());

        System.out.println("Fighting battle " + index++ + " ... " + enemies);

        final RobotSpecification[] robotsList = engine.getLocalRepository(enemies);

        if (robotsList.length > 1) {
            final String team0 = robotsList[0].getTeamId();
            final String teamLast = robotsList[robotsList.length - 1].getTeamId();

            if (team0 == null || !team0.equals(teamLast)) {
                final BattleSpecification specification = new BattleSpecification(battleField.getNumRounds(),
                        battleField.getBattlefield(), robotsList);

                lastResults = null;
                engine.runBattle(specification, true);
            }
        } else {
            System.err.println("Skipping battle because can't load robots: " + enemies);
        }
	}

	private String getEnemies(Bot[] param) {
        StringBuilder eb = new StringBuilder();

        for (int i = 0; i < param.length; i++) {
            if (i > 0) {
                eb.append(',');
            }
            eb.append(param[i].getName());
        }
    	return eb.toString();
	}
}
