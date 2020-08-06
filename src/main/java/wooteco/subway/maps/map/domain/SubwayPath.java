package wooteco.subway.maps.map.domain;

import com.google.common.collect.Lists;
import wooteco.subway.maps.line.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class SubwayPath {
    public static final int DEFAULT_FARE = 1250;

    private List<LineStationEdge> lineStationEdges;

    public SubwayPath(List<LineStationEdge> lineStationEdges) {
        this.lineStationEdges = lineStationEdges;
    }

    public List<LineStationEdge> getLineStationEdges() {
        return lineStationEdges;
    }

    public List<Long> extractStationId() {
        List<Long> stationIds = Lists.newArrayList(lineStationEdges.get(0).getLineStation().getPreStationId());
        stationIds.addAll(lineStationEdges.stream()
                .map(it -> it.getLineStation().getStationId())
                .collect(Collectors.toList()));

        return stationIds;
    }

    public int calculateDuration() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDuration()).sum();
    }

    public int calculateDistance() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDistance()).sum();
    }

    //TODO: 연령별 할인 정책
    public int calculateFare() {
        return DEFAULT_FARE
                + calculateLineExtraFare()
                + calculateOverDistanceExtraFare();
    }

    private int calculateLineExtraFare() {
        return lineStationEdges.stream()
                .map(LineStationEdge::getLine)
                .mapToInt(Line::getExtraFare)
                .max()
                .orElseThrow(RuntimeException::new);
    }

    private int calculateOverDistanceExtraFare() {
        int extraFare = 0;
        int distance = calculateDistance();

        if (distance > 50) {
            extraFare += Math.ceil((distance - 50) / 8.0) * 100;
            distance = 50;
        }
        if (distance > 10) {
            extraFare += Math.ceil((distance - 10) / 5.0) * 100;
        }
        return extraFare;
    }
}
