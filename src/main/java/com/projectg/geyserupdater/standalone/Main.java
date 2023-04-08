package com.projectg.geyserupdater.standalone;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        StandaloneUpdater standaloneUpdater = new StandaloneUpdater(new File(StandaloneUpdater.getRootDirectory(), "updater_config.txt").toPath());
        try {
            standaloneUpdater.init();
            standaloneUpdater.checkForUpdate();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
