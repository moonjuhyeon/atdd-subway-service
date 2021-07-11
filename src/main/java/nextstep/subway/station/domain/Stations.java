package nextstep.subway.station.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;

public class Stations {
    List<Station> values = new ArrayList<>();

    public Stations() {
    }

    public Stations(List<Station> values) {
        this.values = values;
    }

    public Station getById(Long id) {
        return values.stream()
                .filter(value -> value.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format("id가 %d인 역을 찾을 수 없습니다.", id)));
    }

    public void addAll(Stations stations) {
        this.values.addAll(stations.get());
    }

    public int size() {
        return this.values.size();
    }

    public List<Station> get() {
        return unmodifiableList(this.values);
    }

    public Station get(int index) {
        return values.get(index);
    }

    public int lastIndex() {
        return values.size() - 1;
    }

    public boolean contains(Station findingStation) {
        return this.values.contains(findingStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stations stations = (Stations) o;
        return Objects.equals(values, stations.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}