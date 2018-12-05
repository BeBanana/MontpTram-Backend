package eu.bebanana.models;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Live implements Comparable<Live> {

    public Stop stop;
    public Line line;
    public String destination;
    public long delay; // in second
    public int directionId;

    public Live(Stop stop, Line line, String destination, long delay, int directionId) {
        this.stop = stop;
        this.line = line;
        this.destination = destination;
        this.delay = delay;
        this.directionId = directionId;
    }

    @Override
    public int compareTo(Live o) {
        return Long.valueOf(delay - o.delay).intValue();
    }
}
