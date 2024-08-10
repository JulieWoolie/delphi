package net.arcadiusmc.delphiplugin.render;

import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphidom.scss.ComputedStyle;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.dom.style.DisplayType;
import org.bukkit.Location;
import org.joml.Vector2f;

public class ElementRenderObject extends RenderObject {

  private final Align align = Align.Y;
  public final Vector2f contentSize = new Vector2f();

  protected final List<RenderObject> childObjects = new ArrayList<>();

  public ElementRenderObject(PageView view, ComputedStyle style, Screen screen) {
    super(view, style, screen);
  }

  @Override
  protected void measureContent(Vector2f out) {
    out.set(contentSize);
  }

  @Override
  protected void applyContentExtension(Vector2f out) {
    out.add(contentSize);
  }

  @Override
  public void killRecursive() {
    for (RenderObject childObject : childObjects) {
      childObject.killRecursive();
    }

    kill();
  }

  @Override
  public void spawnRecursive() {
    for (RenderObject childObject : childObjects) {
      childObject.spawnRecursive();
    }

    spawn();
  }

  @Override
  protected void postMove(Vector2f screenPos, Vector2f currentPos) {
    if (childObjects.isEmpty()) {
      return;
    }

    final Vector2f newChildPos = new Vector2f();
    final Vector2f currentChildPos = new Vector2f();
    final Vector2f dif = new Vector2f();

    for (RenderObject child : childObjects) {
      currentChildPos.set(child.position);
      dif.set(currentChildPos).sub(currentPos);

      newChildPos.set(screenPos).add(dif);
      child.moveTo(newChildPos);
    }
  }

  @Override
  protected void spawnContent(Location location) {
    // no op
  }

  /* --------------------------- Children ---------------------------- */

  public void addChild(RenderObject renderObject) {
    addChild(renderObject, childObjects.size());
  }

  public void addChild(RenderObject renderObject, int index) {
    childObjects.add(index, renderObject);
    renderObject.parent = this;
  }

  public RenderObject removeChild(RenderObject element) {
    if (!childObjects.remove(element)) {
      return null;
    }

    element.parent = null;
    return element;
  }

  /* --------------------------- Alignment ---------------------------- */

  public void align() {
    if (childObjects.isEmpty()) {
      return;
    }

    for (RenderObject childObject : childObjects) {
      if (childObject instanceof ElementRenderObject el) {
        el.align();
      }
    }

    boolean aligningOnX = align == Align.X;

    Vector2f alignPos = new Vector2f();
    getContentStart(alignPos);

    Vector2f pos = new Vector2f(alignPos);
    Vector2f tempMargin = new Vector2f(0);
    Vector2f elemSize = new Vector2f();

    for (RenderObject child : childObjects) {
      if (child.style.display == DisplayType.NONE) {
        continue;
      }

      Rect margin = child.style.margin;

      if (aligningOnX) {
        pos.x += margin.left;

        tempMargin.set(0, -margin.top);
        pos.y -= margin.top;
      } else {
        pos.y -= margin.top;

        tempMargin.set(margin.left, 0);
        pos.x += margin.left;
      }

      child.moveTo(pos);
      pos.sub(tempMargin);

      child.getElementSize(elemSize);

      if (aligningOnX) {
        pos.x += margin.right + elemSize.x;
      } else {
        pos.y -= margin.bottom + elemSize.y;
      }
    }

    postAlign();
  }

  private void postAlign() {
    if (childObjects.isEmpty()) {
      return;
    }

    Vector2f bottomRight = new Vector2f(Float.MIN_VALUE, Float.MAX_VALUE);
    Vector2f childMax = new Vector2f();

    Rectangle rectangle = new Rectangle();

    for (RenderObject child : childObjects) {
      child.getBounds(rectangle);

      childMax.x = rectangle.getPosition().x + rectangle.getSize().x;
      childMax.y = rectangle.getPosition().y;

      bottomRight.x = Math.max(childMax.x, bottomRight.x);
      bottomRight.y = Math.min(childMax.y, bottomRight.y);
    }

    Vector2f contentStart = new Vector2f();
    getContentStart(contentStart);

    float difX = Math.max(bottomRight.x - contentStart.x, 0);
    float difY = Math.max(contentStart.y - bottomRight.y, 0);

    contentSize.set(difX, difY);
    spawn();
  }
}
