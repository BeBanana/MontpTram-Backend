package eu.bebanana.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Line {

    public String id;
    public String name;
    public LineType type;

    public Line(String id, String name, int typeCode) {
        this.id = id;
        this.name = name;
        this.type = LineType.from(typeCode);
    }
}
