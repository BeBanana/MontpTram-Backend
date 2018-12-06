package eu.bebanana.data;

import eu.bebanana.Logger;
import eu.bebanana.models.Live;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataProvider implements DataManager.DataManagerListener {

    private Map<String, List<Live>> indexedLives = new HashMap<>();
    private DataManager dataManager;

    public DataProvider() {
        this.dataManager = new DataManager(this);
    }

    public List<Live> getLivesFor(String stop, String line) {
        String key = stop + "-" + line;
        if(indexedLives.containsKey(key)) {
            return indexedLives.get(key);
        } else {
            Logger.log("List indexedlives does not contain key: " + key + ".\nHere is the list of keys available: " + indexedLives.keySet());
            return new ArrayList<>();
        }
    }

    @Override
    public void onNewData(final Map<String, List<Live>> indexedLives) {
        this.indexedLives = indexedLives;
    }

    @Override
    public void onError() {
        this.indexedLives = new HashMap<>();
    }

}
