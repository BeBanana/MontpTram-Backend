package eu.bebanana.data;

import eu.bebanana.models.Live;

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
        return indexedLives.get(stop + "-" + line);
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
