package eu.bebanana.data;

import eu.bebanana.models.Live;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataProvider implements DataManager.DataManagerListener {

    private Map<String, List<Live>> indexedLives = new HashMap<>();

    @Override
    public void onNewData(final Map<String, List<Live>> indexedLives) {
        this.indexedLives = indexedLives;
    }

    @Override
    public void onError() {
        this.indexedLives = new HashMap<>();
    }

}
