package titanicsend.util;

import heronarts.lx.transform.LXVector;

public class OffsetVertexes {
    public static LXVector[] copy(LXVector[] vertexes, float distance) {
        LXVector normal = vertexes[1].copy().sub(vertexes[0])
                .cross(vertexes[2].copy().sub(vertexes[0]))
                .normalize()
                .mult(distance);

        LXVector[] offset = new LXVector[3];
        for (int i=0; i< vertexes.length; i++) {
            offset[i] = vertexes[i].copy().add(normal);
        }
        return offset;
    }
}
