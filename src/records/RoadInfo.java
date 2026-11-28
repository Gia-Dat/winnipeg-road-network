package records;

public record RoadInfo(
    String roadName,
    String roadQualifier,
    Integer maxKmPerHour,
    int numLanes,
    String streetClass,
    boolean oneWay,
    double startMeasure,
    double endMeasure
) {}
