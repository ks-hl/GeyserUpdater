package com.projectg.geyserupdater.standalone.utils;

import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.common.util.FileUtils;
import com.projectg.geyserupdater.standalone.StandaloneUpdater;
import dev.kshl.kshlib.net.NetUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class GeyserStandaloneDownloader {

    /**
     * Download the latest build of Geyser from Jenkins CI for the currently used branch.
     * If enabled in the config, the server will also attempt to restart.
     */
    public static void updateGeyser() {
        UpdaterLogger.getLogger().debug("Attempting to download a new build of Geyser.");

        // New task so that we don't block the main thread. All new tasks on bungeecord are async.

        // Download the newest geyser build
        if (downloadGeyser()) {
            String successMsg = "The latest build of Geyser has been downloaded! A restart must occur in order for changes to take effect.";
            System.out.println(successMsg);
            if (StandaloneUpdater.getInstance().doKill()) {
                restartServer();
            }
        } else {
            // fail messages are already sent to the logger in downloadGeyser()
            String failMsg = "A severe error occurred when download a new build of Geyser. Please check the server console for further information!";
            System.err.println(failMsg);
        }
    }

    /**
     * Internal code for downloading the latest build of Geyser from Jenkins CI for the currently used branch.
     *
     * @return true if the download was successful, false if not.
     */
    private static boolean downloadGeyser() {
        String outputPath = StandaloneUpdater.getInstance().getGeyserJarPath();
        File out = new File(StandaloneUpdater.getRootDirectory(), outputPath);
        try {
            if (!out.exists()) {
                boolean ignored = out.createNewFile();
                out = new File(outputPath);
            }
            FileUtils.downloadFile(StandaloneUpdater.getInstance().getDownloadURL(), out.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to download the newest build of Geyser");
            e.printStackTrace();
            return false;
        }

        if (!FileUtils.checkFile(outputPath, false)) {
            System.err.println("Failed to find the downloaded Geyser build!");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Attempt to restart the server
     */
    private static void restartServer() {
        try {
            Runtime.getRuntime().exec(StandaloneUpdater.getInstance().getKillScript());
        } catch (IOException e) {
            System.err.println("Failed to restart server");
            e.printStackTrace();
        }
    }

    public static String getNewSha256() throws IOException {
        JSONObject json = NetUtil.getResponse("https://download.geysermc.org/v2/projects/geyser/versions/latest/builds/latest", true).json();
        if (json == null) return null;
        json = json.optJSONObject("downloads");
        if (json == null) return null;
        json = json.optJSONObject("standalone");
        if (json == null) return null;
        return json.optString("sha256");
    }
}