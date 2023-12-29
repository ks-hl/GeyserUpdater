package com.projectg.geyserupdater.standalone;

import com.projectg.geyserupdater.common.logger.JavaUtilUpdaterLogger;
import com.projectg.geyserupdater.common.logger.UpdaterLogger;
import com.projectg.geyserupdater.standalone.utils.ConfigLoader;
import com.projectg.geyserupdater.standalone.utils.FileUtil;
import com.projectg.geyserupdater.standalone.utils.GeyserStandaloneDownloader;
import com.projectg.geyserupdater.standalone.utils.ShaUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class StandaloneUpdater {
    private final ConfigLoader config;

    public String getGeyserJarPath() {
        return geyser_jar;
    }

    public boolean doKill() {
        return kill;
    }

    public String getKillScript() {
        return kill_script;
    }

    public String getDownloadURL() {
        return download_url;
    }

    private String geyser_jar = "geyser.jar";
    private boolean kill = false;
    private String kill_script = "pkill -f geyser.jar";
    private String branch = "master";
    private String download_url = "https://download.geysermc.org/v2/projects/geyser/versions/latest/builds/latest/downloads/standalone";
    private static StandaloneUpdater instance;

    public StandaloneUpdater(Path config) {
        instance = this;
        UpdaterLogger.setLogger(new JavaUtilUpdaterLogger(Logger.getLogger("GeyserUpdater")));
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("geyser-jar", geyser_jar);
        defaults.put("kill", kill);
        defaults.put("kill-script", kill_script);
        defaults.put("download-url", download_url);
        defaults.put("branch", branch);
        this.config = new ConfigLoader(config, defaults);
    }

    public static StandaloneUpdater getInstance() {
        return instance;
    }

    public void init() throws IOException {
        this.config.load();
        geyser_jar = config.getString("geyser-jar");
        kill = config.getBoolean("kill");
        kill_script = config.getString("kill-script");
        download_url = config.getString("download-url");
        branch = config.getString("branch");
        System.out.println(config.getValues().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((a, b) -> a + '\n' + b).orElse(null));
    }

    public void checkForUpdate() throws IOException {
        String newHash = GeyserStandaloneDownloader.getNewSha256();
        File jarFile = new File(getRootDirectory(), geyser_jar);
        String currentVersion = FileUtil.getCommitID(jarFile);
        String currentHash = ShaUtils.sha256(jarFile.getAbsolutePath());
        if (Objects.equals(newHash, currentHash)) {
            System.out.println("Latest version already installed!");
            return;
        }
        System.out.println("Current version: " + currentVersion);
        System.out.println("Current SHA256 hash: " + currentHash);
        System.out.println("New SHA256 hash: " + newHash);
        GeyserStandaloneDownloader.updateGeyser();
    }

    public static File getRootDirectory() {
        try {
            return new File(StandaloneUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
