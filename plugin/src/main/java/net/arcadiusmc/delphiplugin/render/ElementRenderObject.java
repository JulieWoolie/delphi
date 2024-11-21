package net.arcadiusmc.delphiplugin.render;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.dom.style.DisplayType;
import org.bukkit.Location;
import org.joml.Vector2f;

public class ElementRenderObject extends RenderObject {

  static final Comparator<RenderObject> INDEX_COMPARATOR
      = Comparator.comparingInt(RenderObject::getSourceIndex);

  static final Comparator<RenderObject> ORDER_COMPARATOR = (o1, o2) -> {
    int order1 = o1.style.order;
    int order2 = o2.style.order;

    if (order1 == order2) {
      return Integer.compare(o1.getSourceIndex(), o2.getSourceIndex());
    }

    return Integer.compare(order1, order2);
  };

  public final Vector2f contentSize = new Vector2f();

  protected final List<RenderObject> childObjects = new ArrayList<>();

  public ElementRenderObject(RenderSystem system, ComputedStyleSet style) {
    super(system, style);
  }

  @Override
  protected boolean isHidden() {
    return super.isHidden() || childrenAreHidden();
  }

  public boolean childrenAreHidden() {
    if (childObjects.isEmpty()) {
      return true;
    }

    for (RenderObject childObject : childObjects) {
      if (!childObject.isHidden()) {
        return false;
      }
    }

    return true;
  }

  @Override
  protected void measureContent(Vector2f out) {
    float width = Math.max(contentSize.x, style.setSize.x);
    float height = Math.max(contentSize.y, style.setSize.y);

    out.x = width;
    out.y = height;
  }

  @Override
  protected void applyContentExtension(Vector2f out) {
    float width = Math.max(contentSize.x, style.setSize.x);
    float height = Math.max(contentSize.y, style.setSize.y);

    out.x += width;
    out.y += height;
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
    sortChildren();
  }

  public RenderObject removeChild(RenderObject element) {
    if (!childObjects.remove(element)) {
      return null;
    }

    element.parent = null;
    return element;
  }

  public void sortChildren() {
    if (style.display == DisplayType.FLEX) {
      childObjects.sort(ORDER_COMPARATOR);
    } else {
      childObjects.sort(INDEX_COMPARATOR);
    }
  }
}
