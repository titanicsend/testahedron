package titanicsend.util;

import heronarts.lx.transform.LXVector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OffsetVertexesTest {
    @Test
    public void OffsetVertexesCorrectDistance() {
        LXVector[] vertexes = new LXVector[]{
                new LXVector(0, 0, 0),
                new LXVector(0, 1, 0),
                new LXVector(1, 1, 0)
        };

        LXVector[] offset = OffsetVertexes.copy(vertexes, 1);

        for (int i=0; i < vertexes.length; i++) {
            assertEquals(offset[i].copy().sub(vertexes[i]).mag(), 1);
        }
    }
}