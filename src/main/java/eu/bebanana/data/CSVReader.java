package eu.bebanana.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
