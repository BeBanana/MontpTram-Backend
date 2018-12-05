package eu.bebanana.models;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class Stop {

    public String id;
    public String name;
    public Double lat;
    public Double lng;

    public Stop(String id, String name, Double lat, Double lng) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return Objects.equals(id, stop.id) &&
                Objects.equals(name, stop.name) &&
                Objects.equals(lat, stop.lat) &&
                Objects.equals(lng, stop.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lat, lng);
    }
}
