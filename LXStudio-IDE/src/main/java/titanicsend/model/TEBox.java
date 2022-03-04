package titanicsend.model;

import heronarts.lx.transform.LXVector;

import java.util.*;

public class TEBox {
  LXVector origin;
  LXVector size;
  public List<List<LXVector>> faces;

  public TEBox(LXVector origin, LXVector size) {
    this.origin = origin;
    this.size = size;

    this.faces = new ArrayList<>();

    LXVector cornerTLB = origin;
    LXVector cornerTLF = origin.copy();
    cornerTLF.x += size.x;

    LXVector cornerTRB = cornerTLB.copy();
    LXVector cornerTRF = cornerTLF.copy();
    cornerTRB.z += size.z;
    cornerTRF.z += size.z;

    LXVector cornerBLB = cornerTLB.copy();
    LXVector cornerBLF = cornerTLF.copy();
    LXVector cornerBRB = cornerTRB.copy();
    LXVector cornerBRF = cornerTRF.copy();
    cornerBLB.y += size.y;
    cornerBLF.y += size.y;
    cornerBRB.y += size.y;
    cornerBRF.y += size.y;

    List<LXVector> topFace = new ArrayList<>();
    List<LXVector> botFace = new ArrayList<>();
    List<LXVector> leftFace = new ArrayList<>();
    List<LXVector> rightFace = new ArrayList<>();
    List<LXVector> frontFace = new ArrayList<>();
    List<LXVector> backFace = new ArrayList<>();

    topFace.add(cornerTLB);
    topFace.add(cornerTLF);
    topFace.add(cornerTRF);
    topFace.add(cornerTRB);

    botFace.add(cornerBLB);
    botFace.add(cornerBLF);
    botFace.add(cornerBRF);
    botFace.add(cornerBRB);

    leftFace.add(cornerTLB);
    leftFace.add(cornerTLF);
    leftFace.add(cornerBLF);
    leftFace.add(cornerBLB);

    rightFace.add(cornerTRB);
    rightFace.add(cornerTRF);
    rightFace.add(cornerBRF);
    rightFace.add(cornerBRB);

    backFace.add(cornerTLB);
    backFace.add(cornerTRB);
    backFace.add(cornerBRB);
    backFace.add(cornerBLB);

    frontFace.add(cornerTLF);
    frontFace.add(cornerTRF);
    frontFace.add(cornerBRF);
    frontFace.add(cornerBLF);

    this.faces.add(topFace);
    this.faces.add(botFace);
    this.faces.add(leftFace);
    this.faces.add(rightFace);
    this.faces.add(backFace);
    this.faces.add(frontFace);
  }
}
