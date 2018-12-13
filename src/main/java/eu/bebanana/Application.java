package eu.bebanana;

import eu.bebanana.data.DataProvider;
import eu.bebanana.models.Live;
import lombok.Data;
import org.json.JSONObject;
import spark.Request;
import spark.Spark;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static spark.Spark.before;
import static spark.Spark.halt;
import static spark.Spark.path;
import static spark.Spark.post;

public class Application {

    public static void main(String[] args) {
        Logger.log("Starting application");
        Spark.secure(Env.getInstance().secureKeystore, Env.getInstance().secureKeystorePassword, null, Env.getInstance().secureKeystorePassword);
        Basics.init(Env.getInstance().slackHook);
        DataProvider dataProvider = new DataProvider();
        path("/api", () -> {
            before((request, response) -> securityCheck(request));

            post("/next", (req, res) -> {
                JSONObject body = new JSONObject(req.body());
                String stop = body.getString("stop");
                String line = body.getString("line");
                List<Live> lives = dataProvider.getLivesFor(stop,line);
                if(lives.isEmpty()) {
                    halt(204, "It looks like there is no tram for line " + line + " at " + stop);
                }
                return new JSONObject(new LivesDto(lives, stop));
            });

        });
    }

    private static void securityCheck(final Request request) {
        JSONObject body = new JSONObject(request.body());
        if(!body.getString("apiKey").equals(Env.getInstance().apiKey)) {
            halt(401, "You are not welcome here");
        }
    }

    @Data
    public static class LivesDto {
        private String station;
        private List<DirectionDto> directions = new ArrayList<>();

        public LivesDto(List<Live> lives, String stationName) {
            this.station = stationName;
            directions.add(getDirectionDto(lives, 0));
            directions.add(getDirectionDto(lives, 1));
        }

        private DirectionDto getDirectionDto(List<Live> lives, int directionId) {
            DirectionDto directionDto = new DirectionDto();
            List<Live> zeroLives = lives.stream().filter(l -> l.directionId == directionId).sorted().collect(Collectors.toList());
            if(zeroLives.size() > 0) {
                directionDto.setNextDirection(zeroLives.get(0).getDestination());
                directionDto.setNextDelay(zeroLives.get(0).getDelay());
            }
            if(zeroLives.size() > 1) {
                directionDto.setSecondNextDirection(zeroLives.get(1).getDestination());
                directionDto.setSecondNextDelay(zeroLives.get(1).getDelay());
            }
            return directionDto;
        }
    }

    @Data
    public static class DirectionDto {
        private String nextDirection = null;
        private String secondNextDirection = null;
        private Integer nextDelay = null; // in minutes
        private Integer secondNextDelay = null; // in minutes

        public void setNextDelay(Long nextDelay) {
            this.nextDelay = Long.valueOf(nextDelay / 60).intValue();
        }

        public void setSecondNextDelay(Long secondNextDelay) {
            this.secondNextDelay = Long.valueOf(secondNextDelay / 60).intValue();
        }

        public void setNextDirection(final String nextDirection) {
            this.nextDirection = convertDirection(nextDirection);
        }

        public void setSecondNextDirection(final String secondNextDirection) {
            this.secondNextDirection = convertDirection(secondNextDirection);
        }

        private String convertDirection(String direction) {
            switch (direction) {
                case "H. DEPARTEMENT":
                    return "Hôtel du département";
                case "ND DE SABLASSOU":
                    return "Notre-dame de sablassou";
                default:
                    return direction;
            }
        }
    }

}


