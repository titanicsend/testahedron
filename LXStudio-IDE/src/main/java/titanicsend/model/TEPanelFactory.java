package titanicsend.model;

import java.util.ArrayList;

import Jama.LUDecomposition;
import Jama.Matrix;
import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.transform.LXMatrix;
import heronarts.lx.transform.LXVector;

public class TEPanelFactory {
  public static int MARGIN = 75000; // 75k microns ~= 3 inches
  public static int DISTANCE_BETWEEN_PIXELS = 25000; // 25k microns ~= 1 inch

  public static TEPanelModel build(TEVertex v0, TEVertex v1, TEVertex v2,
                            TEEdgeModel e0, TEEdgeModel e1, TEEdgeModel e2,
                            String panelType) {
    ArrayList<LXPoint> points = new ArrayList<LXPoint>();

    double centroidX = (v0.x + v1.x + v2.x) / 3.0;
    double centroidY = (v0.y + v1.y + v2.y) / 3.0;
    double centroidZ = (v0.z + v1.z + v2.z) / 3.0;

    LXPoint centroid = new LXPoint(centroidX, centroidY, centroidZ);

    if (panelType.equals(TEPanelModel.LIT)) {
      //stripe(points, v0, v1, v2, centroid);
    } else if (panelType.equals(TEPanelModel.SOLID)) {
      points.add(centroid);
    }

    TEVertex[] originalVertices = new TEVertex[]{v0, v1, v2};
    TEVertex[] transformedVertices = copyToXYPlane(v0, v1, v2);
    for (int i = 0; i < 3; i++) {
      for (int j = i + 1; j < 3; j++) {
        double error = transformedVertices[i].distanceTo(transformedVertices[j]) - originalVertices[i].distanceTo(originalVertices[j]);
        assert error < 1 : String.format("length of edge between vertices %d and %d is off by %f", i, j, error);
      }
    }
    Matrix transform = solveTransform(transformedVertices, originalVertices);

    LXPoint test = transformedVertices[0].transform(transform);
    LXPoint test2 = transformedVertices[1].transform(transform);
    LXPoint test3 = transformedVertices[2].transform(transform);

    return new TEPanelModel(points, v0, v1, v2, e0, e1, e2, panelType, centroid);
  }

  private static TEVertex[] copyToXYPlane(TEVertex v0, TEVertex v1, TEVertex v2) {
    double distance_0_1 = v0.distanceTo(v1);
    double distance_1_2 = v1.distanceTo(v2);
    double distance_0_2 = v0.distanceTo(v2);

    TEVertex v0_prime = new TEVertex(new LXPoint(0, 0, 0), -1, -1);
    TEVertex v1_prime = new TEVertex(new LXPoint(distance_0_1, 0, 0), -1, -1);
    LXVector v2_vector = new LXVector(new LXPoint(distance_0_2, 0, 0));
    double v2_angle = Math.acos((Math.pow(distance_0_1, 2) + Math.pow(distance_0_2, 2) - Math.pow(distance_1_2, 2)) / (2 * distance_0_1 * distance_0_2));
    v2_vector.rotate((float)v2_angle);
    TEVertex v2_prime = new TEVertex(new LXPoint(v2_vector.x, v2_vector.y, v2_vector.z), -1, -1);

    return new TEVertex[]{v0_prime, v1_prime, v2_prime};
  }

  public static Matrix solveTransform(TEVertex[] originalVertices, TEVertex[] transformedVertices) {
    double[][] originalVals = {
            {originalVertices[0].x, originalVertices[1].x, originalVertices[2].x},
            {originalVertices[0].y, originalVertices[1].y, originalVertices[2].y},
            {originalVertices[0].z, originalVertices[1].z, originalVertices[2].z},
            {1, 1, 1},
    };
    Matrix original = new Matrix(originalVals);

    double[][] transformedVals = {
            {transformedVertices[0].x, transformedVertices[1].x, transformedVertices[2].x},
            {transformedVertices[0].y, transformedVertices[1].y, transformedVertices[2].y},
            {transformedVertices[0].z, transformedVertices[1].z, transformedVertices[2].z},
            {1, 1, 1},
    };
    Matrix transformed = new Matrix(transformedVals);

    return transformed.times(original.inverse());
  }

/*
  private void stripe(ArrayList<LXPoint> points, TEVertex v0, TEVertex v1, TEVertex v2,
                      LXPoint centroid) {

  }
*/
}