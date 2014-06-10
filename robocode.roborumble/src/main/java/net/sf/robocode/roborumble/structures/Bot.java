package net.sf.robocode.roborumble.structures;

import net.sf.robocode.roborumble.netengine.FileTransfer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static net.sf.robocode.roborumble.util.PropertiesUtil.getProperties;

/**
 * @author Jack Wearden <jack@jackwearden.co.uk>
 */
public class Bot extends ServerObject {
    /*
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    name    TEXT NOT NULL,
    path    TEXT,
    size    INTEGER,
    owner   INTEGER REFERENCES owners

     */

    private String name;
    private String path; // This is a different "path" to that on the server - here it is the path to the local jar.
    private int size;

    private static HashMap<String, Bot> INSTANCES = new HashMap<String, Bot>();

    public Bot() {

    }

    public String getName() {
        return this.name;
    }

    public String getFileName() {
        return this.name.replace(" ", "_");
    }

    public String getURL(String s) {
        return s + "bots/" + this.name;
    }

    public int getSize() {
        return 2000;
    }

    public static Bot fromName(String botName) {
        return INSTANCES.get(botName);
    }

    public boolean ensureBotDownloaded(String tempDir, String botsRepo) {
        String tempFileName = tempDir + this.getFileName();
        String repositoryFileName = botsRepo + this.getFileName();

        FileTransfer.DownloadStatus downloadStatus = FileTransfer.download(this.getURL(this.uri), tempFileName, null);

        if (downloadStatus == FileTransfer.DownloadStatus.FILE_NOT_FOUND) {
            System.out.println("Could not find " + this.getName() + " from " + this.uri);
            return false;
        } else if (downloadStatus == FileTransfer.DownloadStatus.COULD_NOT_CONNECT) {
            System.out.println("Could not connect to " + this.uri);
            return false;
        }

        // Check the bot and save it into the repository

        if (checkJarFile(tempFileName, this.getFileName())) {
            if (!FileTransfer.copy(tempFileName, repositoryFileName)) {
                System.out.println("Unable to copy " + tempFileName + " into the repository");
                return false;
            }
        } else {
            System.out.println("Downloaded file is wrong or corrupted: " + this.getFileName());
            return false;
        }

        System.out.println("Downloaded " + this.getName() + " into " + repositoryFileName);
        return true;
    }

    private boolean checkJarFile(String file, String botName) {
        if (!botName.contains(" ")) {
            System.out.println("Are you sure " + botName + " is a bot/team? Can't download it.");
            return false;
        }

        String bot = botName.substring(0, botName.indexOf(" "));

        bot = bot.replace('.', '/');
//		if (isteams.equals("YES")) {
//			bot += ".team";
//		} else {
        bot += ".properties";
//		}

        try {
            JarFile jarf = new JarFile(file);
            ZipEntry zipe = jarf.getJarEntry(bot);

            if (zipe == null) {
                System.out.println("Not able to read properties");
                return false;
            }
            InputStream properties = jarf.getInputStream(zipe);

            Properties parameters = getProperties(properties);

            String classname = parameters.getProperty("robot.classname", "");
            String version = parameters.getProperty("robot.version", "");

            return (botName.equals(classname + " " + version));

//          For Teams
//			String teamVersion = parameters.getProperty("team.version", "");
//			return (botname.equals(botname.substring(0, botname.indexOf(" ")) + " " + teamVersion));
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

}
