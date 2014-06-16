package net.sf.robocode.roborumble.structures;

import com.google.gson.JsonObject;
import net.sf.robocode.roborumble.netengine.DownloadFailedException;
import net.sf.robocode.roborumble.netengine.FileTransfer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    // URL -> bot
    private static HashMap<String, Bot> INSTANCES = new HashMap<String, Bot>();
    private String tempDir;
    private String botsRepo;

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

    public boolean ensureBotDownloaded() throws DownloadFailedException {
        String tempFileName = tempDir + this.getFileName();
        String repositoryFileName = botsRepo + this.getFileName();

        URL url;
        try {
            url = new URL(this.getURL(Tournament.getInstance().url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new DownloadFailedException();
        }

        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownloadFailedException();
        }

        conn.addRequestProperty("accept", "application/java-archive");

        FileOutputStream f;
        try {
            f = new FileOutputStream(this.tempDir);
            BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            int bit;
            while ((bit = r.read()) != -1) {
                f.write(bit);
            }
        } catch (IOException e) {
            throw new DownloadFailedException();
        }

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
        bot += ".properties";

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

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public static Bot getInstance(JsonObject bot) {
        Bot b = getInstance(bot.get("url").getAsString());
        if (bot.has("name")) {
            b.setName(bot.get("name").getAsString());
        }
        return b;
    }

    public static Bot getInstance(String url) {
        if (INSTANCES.get(url) == null) {
            Bot b = new Bot();

            b.setUri(url);
            INSTANCES.put(url, b);
        }
        return INSTANCES.get(url);
    }

    private void setUri(String uri) {
        this.uri = uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Bot instance: " + this.getName();
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public void setBotsRepo(String botsRepo) {
        this.botsRepo = botsRepo;
    }
}
