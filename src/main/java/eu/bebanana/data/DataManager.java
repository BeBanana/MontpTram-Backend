package eu.bebanana.data;

import eu.bebanana.Logger;
import eu.bebanana.models.Line;
import eu.bebanana.models.Live;
import eu.bebanana.models.Stop;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class DataManager {

    private List<Stop> stopList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();

    private final CSVReader csvReader;
    private final LiveFileDownloader liveFileDownloader;

    private static final String STOPS_TXT = "stops.txt";
    private static final String LINES_TXT = "routes.txt";

    private DataManagerListener listener;

    DataManager(DataManagerListener listener) {

        this.listener = listener;

        csvReader = new CSVReader();
        csvReader.readFromResource(STOPS_TXT, new CSVReader.Listener() {
            @Override
            public void onNewLine(String[] fields) {
                stopList.add(new Stop(fields[1], fields[2], Double.valueOf(fields[3]), Double.valueOf(fields[4])));
            }

            @Override
            public void onError(Exception e) {
                throw new RuntimeException(e);
            }
        });

        csvReader.readFromResource(LINES_TXT, new CSVReader.Listener() {
            @Override
            public void onNewLine(String[] fields) {
                lineList.add(new Line(fields[0], fields[2], Integer.valueOf(fields[4])));
            }

            @Override
            public void onError(Exception e) {
                throw new RuntimeException(e);
            }
        });

        liveFileDownloader = new LiveFileDownloader(new LiveFileDownloader.Listener() {
            @Override
            public void onNewFile(Path path) {
                updateLivesFromFile(path);
            }

            @Override
            public void onError(Exception e) {
                Logger.report("Error while downloading the live data file", e);
            }
        });
    }

    private void updateLivesFromFile(Path filePath) {
        Map<String, List<Live>> tmpIndexedMap = new HashMap<>();

        csvReader.readFromFile(new File(filePath.toString()), new CSVReader.Listener() {
            @Override
            public void onNewLine(String[] fields) {
                Optional<Stop> optStop = getStopById(stopList, fields[2]);
                if(!optStop.isPresent()) {
                    Logger.log("Stop : " + fields[2] + " does not exist");
                    return;
                }
                Optional<Line> optLine = getLine(lineList, fields[4]);
                if(!optLine.isPresent()) {
                    Logger.log("Line : " + fields[4] + " does not exist");
                    return;
                }

                Live live = new Live(optStop.get(), optLine.get(), fields[5], Integer.valueOf(fields[9]), Integer.valueOf(fields[6]));

                // Index by stop
                if(!tmpIndexedMap.containsKey(live.stop.name)) {
                    tmpIndexedMap.put(live.stop.name, new ArrayList<>());
                }
                tmpIndexedMap.get(live.stop.name).add(live);

                // Index by line
                if(!tmpIndexedMap.containsKey(live.line.name)){
                    tmpIndexedMap.put(live.line.name, new ArrayList<>());
                }
                tmpIndexedMap.get(live.line.name).add(live);

                // Index by stop and line
                String id = live.stop.name + "-" + live.line.name;
                if(!tmpIndexedMap.containsKey(id)){
                    tmpIndexedMap.put(id, new ArrayList<>());
                }
                tmpIndexedMap.get(id).add(live);
            }

            @Override
            public void onFinished() {
                listener.onNewData(tmpIndexedMap);
            }

            @Override
            public void onError(Exception e) {
                Logger.report("Error on updating live from file: ", e);
                listener.onError();
            }
        });
    }

    private Optional<Stop> getStopById(List<Stop> stops, String stopId) {
        for(Stop s : stops) {
            if(Integer.valueOf(s.id).equals(Integer.valueOf(stopId))) {
                return Optional.of(s);
            }
        }

        return Optional.empty();
    }

    private Optional<Line> getLine(List<Line> lines, String lineId) {
        for(Line l : lines) {
            if(l.id.toLowerCase().equals(lineId.toLowerCase())) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    interface DataManagerListener {
        void onNewData(Map<String, List<Live>> indexedLives);
        void onError();
    }

}
