package eu.bebanana.data;

import eu.bebanana.models.Line;
import eu.bebanana.models.Live;
import eu.bebanana.models.Stop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class CSVReader {

    private static final String COMMA_DELIMITER = ",";

    void readFromResource(String resName, Listener listener) {
        try(InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resName)) {
            try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                //Read to skip the header
                br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.replace("\"", "");
                    String[] fields = line.split(COMMA_DELIMITER);
                    if(fields.length > 0 ) {
                        listener.onNewLine(fields);
                    }
                }
            }
            listener.onFinished();
        } catch (Exception e) {
            listener.onError(e);
        }
    }

    void readFromFile(File file, Listener listener) {
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\"", "");
                String[] fields = line.split(";");
                if(fields.length > 0 ) {
                    listener.onNewLine(fields);
                }
            }
            listener.onFinished();
        } catch (Exception e) {
            listener.onError(e);
        }
    }


    abstract static class Listener {
        abstract void onNewLine(String[] fields);
        abstract void onError(Exception e);
        void onFinished(){}
    }

    List<Live> readLive(String liveUrl, List<Line> lines, List<Stop> stops) throws Exception {
        List<Live> lives = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(new File(liveUrl)))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replace("\"", "");
                String[] fields = line.split(";");
                if(fields.length > 0 ) {
                    Optional<Stop> optStop = getStop(stops, fields[2]);
                    if(!optStop.isPresent()) {
                        continue;
                    }
                    Optional<Line> optLine = getLine(lines, fields[4]);
                    if(!optLine.isPresent()) {
                        continue;
                    }
                    lives.add(new Live(optStop.get(), optLine.get(), fields[5], Integer.valueOf(fields[9]), Integer.valueOf(fields[6])));
                }
            }
            return lives;
        }
    }

    Optional<Stop> getStop(List<Stop> stops, String stopId) {
        for(Stop s : stops) {
            if(s.id.equals(stopId)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    Optional<Line> getLine(List<Line> lines, String lineName) {
        for(Line l : lines) {
            if(l.name.equals(lineName)) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }
}
