package com.juliewoolie.delphirender.object;

import com.juliewoolie.delphirender.Consts;
import com.juliewoolie.delphirender.FullStyle;
import com.juliewoolie.delphirender.RenderScreen;
import com.juliewoolie.delphirender.RenderSystem;
import com.juliewoolie.delphirender.math.Rectangle;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class RenderObject {

  public static final Brightness BRIGHTNESS = new Brightness(0, 15);
  public static final boolean SEE_THROUGH = false;

  public final Vector2f position = new Vector2f(0);
  public final Vector2f size = new Vector2f(0);

  public final RenderSystem system;
  public final RenderScreen screen;

  public ElementRenderObject parent;

  public float depth = 0.0f;
  public int domIndex = 0;

  public RenderObject(RenderSystem system) {
    this.system = system;
    this.screen = system.getScreen();
  }

  public void moveTo(Vector2f pos) {
    moveTo(pos.x, pos.y);
  }

  public void moveTo(float x, float y) {
    position.x = x;
    position.y = y;
  }

  public void getBounds(Rectangle rectangle) {
    rectangle.position.x = position.x;
    rectangle.position.y = position.y - size.y;
    rectangle.size.set(this.size);
  }

  protected Location getLocation() {
    Location l = new Location(system.getWorld(), 0, 0, 0);
    screenLocation(this.position, l);
    return l;
  }

  protected void screenLocation(Vector2f screenPos, Location out) {
    Vector3f pos = new Vector3f();
    screen.screenToWorld(screenPos, pos);
    out.set(pos.x, pos.y, pos.z);
  }

  protected float getZIndexDepth() {
    if (parent != null) {
      return parent.style.zindex * Consts.MACRO_LAYER_DEPTH;
    }
    return 0.0f;
  }

  protected FullStyle getParentStyle() {
    if (parent == null) {
      return null;
    }
    return parent.style;
  }

  protected void configureEntity(Display display) {
    display.setBrightness(BRIGHTNESS);

    if (display instanceof TextDisplay text) {
      text.setSeeThrough(SEE_THROUGH);
    }
  }

  protected static Transformation newTransform() {
    return new Transformation(
        new Vector3f(0),
        new Quaternionf(),
        new Vector3f(1),
        new Quaternionf()
    );
  }

  public abstract void spawn();

  public abstract void kill();

  public void killRecursive() {
    kill();
  }

  public void spawnRecursive() {
    spawn();
  }
}
