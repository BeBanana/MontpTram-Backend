package eu.bebanana;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LiveFileDownloader {

    private ScheduledExecutorService schedulerService;
    private static final Path directory = Paths.get("/tmp/");

    private Listener listener;

    public LiveFileDownloader(Listener listener) {
        this.listener = listener;
        update();
    }

    private Optional<Path> downloadFile() {
        System.out.println("Downloading live data file");
        HttpGet httpGet = null;
        Path filePath = directory.resolve("liveData.txt");
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            httpGet = new HttpGet(Env.getInstance().liveDataUrl);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity imageEntity = httpResponse.getEntity();

            if (imageEntity != null) {
                FileUtils.copyInputStreamToFile(imageEntity.getContent(), filePath.toFile());
            }
        } catch (IOException e) {
            return Optional.empty();
        } finally {
            if(httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return Optional.of(filePath);
    }

    private void reschedule() {
        System.out.println("Scheduling new download");

        schedulerService = Executors.newSingleThreadScheduledExecutor();
        schedulerService.schedule(this::update, 60, TimeUnit.SECONDS);
    }

    private void update() {
        try {
            Path path = downloadFile().orElseThrow(Exception::new);
            listener.onNewFile(path);
        } catch (Exception e) {
            listener.onError(e);
            e.printStackTrace();
        } finally {
            reschedule();
        }
    }

    public interface Listener {
        void onNewFile(Path path);
        void onError(Exception e);
    }
}
