package titanicsend.model;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

public class Triangle extends LXModel {
    public List<Edge> edges;

    public Triangle(LXPoint p1, LXPoint p2, LXPoint p3, double spacing) {
        this(makeEdges(p1, p2, p3, spacing));
    }

    private Triangle(List<Edge> edges) {
        super(edges.toArray(new LXModel[edges.size()]));
        this.edges = edges;
        reindexPoints();
    }

    private static List<Edge> makeEdges(LXPoint p1, LXPoint p2, LXPoint p3, double spacing) {
        ArrayList<Edge> children = new ArrayList<Edge>();
        LXPoint[][] edges = {{p1, p2}, {p2, p3}, {p3, p1}};

        int index = 0;
        for (LXPoint[] edge : edges) {
            children.add(new Edge(edge[0], edge[1], spacing));
        }
        return children;
    }
}
