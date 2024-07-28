package net.arcadiusmc.delphiplugin.render;

import net.arcadiusmc.delphi.Screen;
import net.arcadiusmc.delphi.dom.DelphiNode;
import net.arcadiusmc.delphi.dom.scss.ComputedStyle;
import net.arcadiusmc.delphi.dom.scss.PropertySet;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import org.joml.Vector2f;

public abstract class RenderObject {

  private final DelphiNode node;
  private final Screen screen;

  private final PropertySet styleProperties;
  private final ComputedStyle style;

  protected final Vector2f position = new Vector2f();
  protected boolean spawned;
  protected ElementRenderObject parent;

  public RenderObject(DelphiNode node, Screen screen) {
    this.node = node;
    this.screen = screen;

    this.style = node.style;
    this.styleProperties = node.styleSet;
  }

  public abstract void moveTo(Vector2f screenPosition);

  public abstract void getSize(Vector2f out);

  public abstract void getBounds(Rectangle rectangle);

  public abstract void spawn();

  public void update() {
    if (!spawned) {
      return;
    }

    spawn();
  }
}
