package eu.bebanana.data;

import eu.bebanana.Logger;
import eu.bebanana.models.Line;
import eu.bebanana.models.Live;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataProvider implements DataManager.DataManagerListener {

    private Map<String, List<Live>> indexedLives = new HashMap<>();
    private DataManager dataManager;

    public DataProvider() {
        this.dataManager = new DataManager(this);
    }

    public List<Live> getLivesFor(String stop, String line) {
        String key = stop.toLowerCase() + "-" + line.toLowerCase();
        if(indexedLives.containsKey(key)) {
            return indexedLives.get(key);
        } else {
            Logger.log("List indexedlives does not contain key: " + key + ".\nHere is the list of keys available: " + indexedLives.keySet());
            return new ArrayList<>();
        }
    }

    public List<Line> getLinesAt(String stop) {
        String key = stop.toLowerCase();
        if(indexedLives.containsKey(key)) {
            return indexedLives.get(key).stream().map(Live::getLine).distinct().collect(Collectors.toList());
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
