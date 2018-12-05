package eu.bebanana;

import java.nio.file.Path;

public class DataManager {

    private final LiveFileDownloader liveFileDownloader;

    public DataManager() {
        liveFileDownloader = new LiveFileDownloader(new LiveFileDownloader.Listener() {
            @Override
            public void onNewFile(Path path) {

            }

            @Override
            public void onError(Exception e) {
                Logger.report("Error while downloading the live data file", e);
            }
        });
    }

}
