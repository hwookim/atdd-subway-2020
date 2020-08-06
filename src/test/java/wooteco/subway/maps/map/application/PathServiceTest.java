package wooteco.subway.maps.map.application;

import com.google.common.collect.Lists;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.domain.SubwayPath;
import wooteco.subway.maps.station.domain.Station;
import wooteco.subway.common.TestObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PathServiceTest {
    private List<Line> lines;
    private PathService pathService;

    @BeforeEach
    void setUp() {
        Map<Long, Station> stations = new HashMap<>();
        stations.put(1L, TestObjectUtils.createStation(1L, "교대역"));
        stations.put(2L, TestObjectUtils.createStation(2L, "강남역"));
        stations.put(3L, TestObjectUtils.createStation(3L, "양재역"));
        stations.put(4L, TestObjectUtils.createStation(4L, "남부터미널역"));
        stations.put(5L, TestObjectUtils.createStation(5L, "매봉역"));
        stations.put(6L, TestObjectUtils.createStation(6L, "도곡역"));

        Line line1 = TestObjectUtils.createLine(1L, "2호선", "GREEN", 200);
        line1.addLineStation(new LineStation(1L, null, 0, 0));
        line1.addLineStation(new LineStation(2L, 1L, 2, 2));

        Line line2 = TestObjectUtils.createLine(2L, "신분당선", "RED", 100);
        line2.addLineStation(new LineStation(2L, null, 0, 0));
        line2.addLineStation(new LineStation(3L, 2L, 2, 1));

        Line line3 = TestObjectUtils.createLine(3L, "3호선", "ORANGE", 0);
        line3.addLineStation(new LineStation(1L, null, 0, 0));
        line3.addLineStation(new LineStation(4L, 1L, 1, 2));
        line3.addLineStation(new LineStation(3L, 4L, 2, 2));
        line3.addLineStation(new LineStation(5L, 3L, 20, 2));
        line3.addLineStation(new LineStation(6L, 5L, 50, 2));

        lines = Lists.newArrayList(line1, line2, line3);

        pathService = new PathService();
    }

    @Test
    void findPathByDistance() {
        // when
        SubwayPath subwayPath = pathService.findPath(lines, 1L, 3L, PathType.DISTANCE);

        // then
        assertThat(subwayPath.extractStationId().size()).isEqualTo(3);
        assertThat(subwayPath.extractStationId().get(0)).isEqualTo(1L);
        assertThat(subwayPath.extractStationId().get(1)).isEqualTo(4L);
        assertThat(subwayPath.extractStationId().get(2)).isEqualTo(3L);
    }

    @Test
    void findPathByDuration() {
        // when
        SubwayPath subwayPath = pathService.findPath(lines, 1L, 3L, PathType.DURATION);

        // then
        assertThat(subwayPath.extractStationId().size()).isEqualTo(3);
        assertThat(subwayPath.extractStationId().get(0)).isEqualTo(1L);
        assertThat(subwayPath.extractStationId().get(1)).isEqualTo(2L);
        assertThat(subwayPath.extractStationId().get(2)).isEqualTo(3L);
    }

    @Test
    void calculateFare() {
        // when
        SubwayPath subwayPath = pathService.findPath(lines, 1L, 4L, PathType.DURATION);

        // then
        assertThat(subwayPath.calculateFare()).isEqualTo(1250);
    }

    @Test
    void calculateFare_extraFare() {
        // when
        SubwayPath subwayPath = pathService.findPath(lines, 1L, 3L, PathType.DURATION);

        // then
        assertThat(subwayPath.calculateFare()).isEqualTo(1450);
    }

    @Test
    void calculateFare_overDistance_under50km() {
        // when
        SubwayPath subwayPath = pathService.findPath(lines, 3L, 5L, PathType.DURATION);

        // then
        assertThat(subwayPath.calculateDistance()).isEqualTo(20);
        assertThat(subwayPath.calculateFare()).isEqualTo(1450);
    }

    @Test
    void calculateFare_overDistance_over50km() {
        // when
        SubwayPath subwayPath = pathService.findPath(lines, 3L, 6L, PathType.DURATION);

        // then
        assertThat(subwayPath.calculateDistance()).isEqualTo(70);
        assertThat(subwayPath.calculateFare()).isEqualTo(2350);
    }
}
