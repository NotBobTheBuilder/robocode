/*******************************************************************************
 * Copyright (c) 2003-2014 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package roborumble;


import net.sf.robocode.roborumble.battlesengine.BattlesRunner;
import net.sf.robocode.roborumble.netengine.DownloadFailedException;
import net.sf.robocode.roborumble.structures.Battle;
import net.sf.robocode.roborumble.structures.Game;
import net.sf.robocode.roborumble.structures.Tournament;
import net.sf.robocode.roborumble.structures.builders.ServerObjectBuilder;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Implements the client side of RoboRumble@Home.
 * Controlled by properties files.
 *
 * @author Albert Pérez (original)
 * @author Flemming N. Larsen (contributor)
 * @author Jerome Lavigne (contributor)
 */
public class RoboRumbleAtHome {

    public RoboRumbleAtHome(Game game) {

        final BattlesRunner engine = new BattlesRunner();

        for (Battle battle : game) {
            try {
                game.downloadBots();
            } catch (DownloadFailedException e) {
                continue;
            }
            engine.runBattle(battle);
        }

    }

	public static void main(String args[]) throws InterruptedException {
        // Disable the -DPRARALLEL and -DRANDOMSEED options
        System.setProperty("PARALLEL", "false"); // TODO: Remove when robot thread CPU time can be measured
        System.setProperty("RANDOMSEED", "none"); // In tournaments, robots should not be deterministic!

        URL url;
        Tournament tournament;
        try {
              String host = args[0];
              url = new URL(host);
              tournament = ServerObjectBuilder.getTournament(url);
              System.out.println(tournament);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ERROR: Host left unspecified. Please specify on command line");
            System.exit(1);
            return;
        } catch (MalformedURLException e) {
            System.err.println("ERROR: URL malformed - check roborumble.txt settings");
            System.exit(2);
            return;
        } catch (DownloadFailedException e) {
            System.err.println("Download failed - check your internet connection, the server details & retry");
            System.exit(3);
            return;
        }

        if (tournament.getDefaultGame() == null) {
            System.err.println("No games exist in tournament");
            System.exit(4);
            return;
        }

        new RoboRumbleAtHome(tournament.getDefaultGame());

	}
}
