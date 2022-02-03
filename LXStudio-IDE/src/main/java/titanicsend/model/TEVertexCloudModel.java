package titanicsend.model;

import java.util.Collections;
import heronarts.lx.model.LXModel;
import java.util.*;

public class TEVertexCloudModel extends LXModel {
  public List<TEVertex> vertexes;

  public TEVertexCloudModel(List<TEVertex> vertexes) {
    super(Collections.unmodifiableList(vertexes));
    this.vertexes = vertexes;
  }
}