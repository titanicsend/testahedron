package titanicsend.lasercontrol;

import heronarts.lx.transform.LXVector;
import titanicsend.model.TELaserModel;

public class Target extends LaserControl {
  public LXVector bullseye;

  public Target(TELaserModel laser) {
    super(laser);
    this.bullseye = new LXVector(-10e6F, 0, 0);
    this.update(0);
  }

  public void update(double deltaMsec) {
    this.laser.direction = this.bullseye.copy().sub(this.laser.origin);
  }
}
