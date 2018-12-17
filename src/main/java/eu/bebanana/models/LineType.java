package eu.bebanana.models;

public enum LineType {
    tramway, bus;

    public static LineType from(int code) {
        switch (code){
            case 0:
                return LineType.tramway;
            case 3:
                return LineType.bus;
            default:
                throw new UnknownLineType(code);
        }
    }

    private static class UnknownLineType extends RuntimeException {
        UnknownLineType(int lineCode) {
            super("No line type is define for code: " + lineCode);
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case tramway:
                return "tram";
            case bus:
                return "bus";
            default:
                return "";
        }
    }
}
