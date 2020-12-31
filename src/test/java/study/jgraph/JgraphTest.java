package study.jgraph;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JgraphTest {
    @Test
    public void getDijkstraShortestPath() {
        String source = "v3";
        String target = "v1";
        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        List<String> shortestPath = dijkstraShortestPath.getPath(source, target).getVertexList();

        assertThat(shortestPath.size()).isEqualTo(3);
    }

    @Test
    public void getKShortestPaths() {
        String source = "v3";
        String target = "v1";

        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");
        graph.setEdgeWeight(graph.addEdge("v1", "v2"), 2);
        graph.setEdgeWeight(graph.addEdge("v2", "v3"), 2);
        graph.setEdgeWeight(graph.addEdge("v1", "v3"), 100);

        List<GraphPath> paths = new KShortestPaths(graph, 100).getPaths(source, target);

        assertThat(paths).hasSize(2);
        paths.forEach(it -> {
                    assertThat(it.getVertexList()).startsWith(source);
                    assertThat(it.getVertexList()).endsWith(target);
        });
    }

    @DisplayName("노선 한개짜리 그래프를 그리고 탐색하기")
    @Test
    void justOneLineTest() {
        String station1 = "강남역";
        String station2 = "역삼역";
        String station3 = "선릉역";

        WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        graph.addVertex(station1);
        graph.addVertex(station2);
        graph.addVertex(station3);
        graph.setEdgeWeight(graph.addEdge(station1, station2), 2);
        graph.setEdgeWeight(graph.addEdge(station2, station3), 2);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        // 최단 경로 탐색 가능
        List<String> shortestPath = dijkstraShortestPath.getPath(station1, station3).getVertexList();
        assertThat(shortestPath.get(0)).isEqualTo(station1);
        assertThat(shortestPath.get(2)).isEqualTo(station3);

        // 탐색까지의 총 소요되는 가중치 계산 가능
        double pathWeight = dijkstraShortestPath.getPathWeight(station1, station3);
        assertThat(pathWeight).isEqualTo(4);

        // 경로의 그래프 확인 가능
        Arrays.asList(station1, station2, station3).forEach(station -> {
            ShortestPathAlgorithm.SingleSourcePaths paths = dijkstraShortestPath.getPaths(station);
            assertThat(paths.getGraph()).isEqualTo(graph);
        });
    }

    @DisplayName("겹치는 구간이 있는 그래프 두개 그리고 탐색해보기")
    @Test
    void twoLinesWithConnectionTest() {
        /*
                         왕십리역
                            |
            강남역 - 역삼역 - 선릉역
                            |
                           분당역
         */

        String station1 = "강남역";
        String station2 = "역삼역";
        String station3 = "선릉역";
        String station4 = "분당역";
        String station5 = "왕십리역";

        // 아예 처음부터 통째로 다 등록해버린다. (이렇게 어마어마하게 큰 Path를 만들어도 문제는 없을까?)
        WeightedMultigraph<String, DefaultWeightedEdge> path = new WeightedMultigraph(DefaultWeightedEdge.class);
        // 2호선 등록
        path.addVertex(station1);
        path.addVertex(station2);
        path.addVertex(station3);
        path.setEdgeWeight(path.addEdge(station1, station2), 1);
        path.setEdgeWeight(path.addEdge(station2, station3), 2);
        // 분당선 등록
        path.addVertex(station3);   // vetex는 set으로 관리되기 때문에 중복되도 상관 없음
        path.addVertex(station4);
        path.addVertex(station5);
        path.setEdgeWeight(path.addEdge(station5, station3), 3);
        path.setEdgeWeight(path.addEdge(station3, station4), 4);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(path);
        List stations = dijkstraShortestPath.getPath(station1, station5).getVertexList();
        assertThat(stations.get(0)).isEqualTo(station1);
        assertThat(stations.get(1)).isEqualTo(station2);
        assertThat(stations.get(2)).isEqualTo(station3);
        assertThat(stations.get(3)).isEqualTo(station5);

        double pathWeight = dijkstraShortestPath.getPathWeight(station1, station5);
        assertThat(pathWeight).isEqualTo(6);
    }

    @DisplayName("겹치는 역이 없는 노선 두개 생성하고 탐색해보기")
    @Test
    void twoLinesWithoutConnectionTest() {
        /*
            강남역 - 역삼역 - 선릉역

            행당역 - 왕십리역 - 마장역
        */

        String station1 = "강남역";
        String station2 = "역삼역";
        String station3 = "선릉역";
        String station4 = "행당역";
        String station5 = "왕십리역";
        String station6 = "마장역";

        WeightedMultigraph<String, DefaultWeightedEdge> path = new WeightedMultigraph(DefaultWeightedEdge.class);

        path.addVertex(station1);
        path.addVertex(station2);
        path.addVertex(station3);
        path.addVertex(station4);
        path.addVertex(station5);
        path.addVertex(station6);

        path.setEdgeWeight(path.addEdge(station1, station2), 3);
        path.setEdgeWeight(path.addEdge(station2, station3), 3);
        path.setEdgeWeight(path.addEdge(station4, station5), 3);
        path.setEdgeWeight(path.addEdge(station5, station6), 3);

        DijkstraShortestPath shortestPath = new DijkstraShortestPath(path);

        // 존재하는 구간은 잘 찾는다.
        List<String> stations = shortestPath.getPath(station1, station3).getVertexList();
        assertThat(stations.get(0)).isEqualTo(station1);
        assertThat(stations.get(1)).isEqualTo(station2);
        assertThat(stations.get(2)).isEqualTo(station3);

        // 존재하지 않는 구간은 반환값이고 뭐고 없이 그냥 예외 던져버린다.
        try {
            shortestPath.getPath(station1, station4);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }
}