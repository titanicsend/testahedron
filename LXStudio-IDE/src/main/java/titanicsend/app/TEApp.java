/**
 * Copyright 2020- Mark C. Slee, Heron Arts LLC
 *
 * This file is part of the LX Studio software library. By using
 * LX, you agree to the terms of the LX Studio Software License
 * and Distribution Agreement, available at: http://lx.studio/license
 *
 * Please note that the LX license is not open-source. The license
 * allows for free, non-commercial use.
 *
 * HERON ARTS MAKES NO WARRANTY, EXPRESS, IMPLIED, STATUTORY, OR
 * OTHERWISE, AND SPECIFICALLY DISCLAIMS ANY WARRANTY OF
 * MERCHANTABILITY, NON-INFRINGEMENT, OR FITNESS FOR A PARTICULAR
 * PURPOSE, WITH RESPECT TO THE SOFTWARE.
 *
 * @author Mark C. Slee <mark@heronarts.com>
 */

package titanicsend.app;

import java.io.File;

import heronarts.lx.LX;
import heronarts.lx.LXPlugin;
import heronarts.lx.studio.LXStudio;
import processing.core.PApplet;
import titanicsend.GigglePixelListener;
import titanicsend.model.TEWholeModel;
import titanicsend.output.TESacnOutput;
import titanicsend.pattern.alex.*;
import titanicsend.pattern.jeff.*;
import titanicsend.pattern.tmc.*;
import titanicsend.pattern.tom.*;
import titanicsend.pattern.mike.*;

public class TEApp extends PApplet implements LXPlugin  {
  private TEWholeModel model;

  private static int WIDTH = 1280;
  private static int HEIGHT = 800;
  private static boolean FULLSCREEN = false;

  @Override
  public void settings() {
    if (FULLSCREEN) {
      fullScreen(PApplet.P3D);
    } else {
      size(WIDTH, HEIGHT, PApplet.P3D);
    }
    pixelDensity(displayDensity());
  }

  @Override
  public void setup() {
    LXStudio.Flags flags = new LXStudio.Flags(this);
    flags.resizable = true;
    flags.useGLPointCloud = false;
    flags.startMultiThreaded = true;

    //String subdir = "testahedron";
    String subdir = "vehicle";

    this.model = new TEWholeModel(subdir);

    new LXStudio(this, flags, this.model);
    this.surface.setTitle(this.model.name);
  }

  @Override
  public void initialize(LX lx) {
    // Here is where you should register any custom components or make modifications
    // to the LX engine or hierarchy. This is also used in headless mode, so note that
    // you cannot assume you are working with an LXStudio class or that any UI will be
    // available.

    TESacnOutput.activateAll(lx, this.model.gapPoint.index);

    // Register custom pattern and effect types
    lx.registry.addPattern(BasicRainbowPattern.class);
    lx.registry.addPattern(Bounce.class);
    lx.registry.addPattern(BrightScreen.class);
    lx.registry.addPattern(ResizeableScreen.class);
    lx.registry.addPattern(Bubbles.class);
    lx.registry.addPattern(Checkers.class);
    lx.registry.addPattern(EdgeRunner.class);
    lx.registry.addPattern(ModelDebugger.class);
    lx.registry.addPattern(ModuleEditor.class);
    lx.registry.addPattern(SimpleSolidEdgePattern.class);
    lx.registry.addPattern(SimpleSolidPanelPattern.class);
    lx.registry.addPattern(Pulse.class);
    lx.registry.addEffect(titanicsend.effect.BasicEffect.class);

    GigglePixelListener gpListener = new GigglePixelListener(lx,"127.0.0.1");
    lx.engine.addLoopTask(gpListener);
  }

  public void initializeUI(LXStudio lx, LXStudio.UI ui) {
    // Here is where you may modify the initial settings of the UI before it is fully
    // built. Note that this will not be called in headless mode. Anything required
    // for headless mode should go in the raw initialize method above.
  }

  public void onUIReady(LXStudio lx, LXStudio.UI ui) {
    // At this point, the LX Studio application UI has been built. You may now add
    // additional views and components to the Ui heirarchy.

    TEVirtualOverlays visual = new TEVirtualOverlays(this.model);
    lx.ui.preview.addComponent(visual);
    new TEUIControls(ui, visual, ui.leftPane.global.getContentWidth()).addToContainer(ui.leftPane.global);
  }

  @Override
  public void draw() {
    // All handled by core LX engine, do not modify, method exists only so that Processing
    // will run a draw-loop.
  }

  /**
   * Main interface into the program. Two modes are supported, if the --headless
   * flag is supplied then a raw CLI version of LX is used. If not, then we embed
   * in a Processing 4 applet and run as such.
   *
   * @param args Command-line arguments
   */
  public static void main(String[] args) {
    LX.log("Initializing LX version " + LXStudio.VERSION);
    boolean headless = false;
    File projectFile = null;
    for (int i = 0; i < args.length; ++i) {
      if ("--help".equals(args[i]) || "-h".equals(args[i])) {
      } else if ("--headless".equals(args[i])) {
        headless = true;
      } else if ("--fullscreen".equals(args[i]) || "-f".equals(args[i])) {
        FULLSCREEN = true;
      } else if ("--width".equals(args[i]) || "-w".equals(args[i])) {
        try {
          WIDTH = Integer.parseInt(args[++i]);
        } catch (Exception x ) {
          LX.error("Width command-line argument must be followed by integer");
        }
      } else if ("--height".equals(args[i]) || "-h".equals(args[i])) {
        try {
          HEIGHT = Integer.parseInt(args[++i]);
        } catch (Exception x ) {
          LX.error("Height command-line argument must be followed by integer");
        }
      } else if (args[i].endsWith(".lxp")) {
        try {
          projectFile = new File(args[i]);
        } catch (Exception x) {
          LX.error(x, "Command-line project file path invalid: " + args[i]);
        }
      }
    }
    if (headless) {
      // We're not actually going to run this as a PApplet, but we need to explicitly
      // construct and set the initialize callback so that any custom components
      // will be run
      LX.Flags flags = new LX.Flags();
      flags.initialize = new TEApp();
      if (projectFile == null) {
        LX.log("WARNING: No project filename was specified for headless mode!");
      }
      LX.headless(flags, projectFile);
    } else {
      PApplet.main("titanicsend.app.TEApp", args);
    }
  }

}
