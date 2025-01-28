package net.arcadiusmc.delphirender.tree;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.delphirender.RenderSystem;
import net.arcadiusmc.dom.style.DisplayType;

@Getter
public class ElementRenderElement extends RenderElement {

  static final Comparator<RenderElement> BY_INDEX
      = Comparator.comparingInt(RenderElement::getDomIndex);

  static final Comparator<RenderElement> BY_ORDER
      = Comparator.<RenderElement>comparingInt(value -> value.style.order)
      .thenComparing(BY_INDEX);

  private final List<RenderElement> children = new ObjectArrayList<>();

  public ElementRenderElement(RenderSystem system, ComputedStyleSet styleSet) {
    super(system, styleSet);
  }

  @Override
  public void spawnRecursive() {
    for (RenderElement child : children) {
      child.spawnRecursive();
    }

    spawn();
  }

  @Override
  public void killRecursive() {
    for (RenderElement child : children) {
      child.killRecursive();
    }

    kill();
  }

  @Override
  public void moveTo(float x, float y) {
    float offx = x - this.position.x;
    float offy = y - this.position.y;

    super.moveTo(x, y);

    for (RenderElement child : children) {
      float cx = offx + child.position.x;
      float cy = offy + child.position.y;

      child.moveTo(cx, cy);
    }
  }

  public void addChild(RenderElement child, int idx) {
    children.add(idx, child);
    child.parent = this;
    sortChildren();
  }

  public void removeChild(RenderElement element) {
    if (!children.remove(element)) {
      return;
    }

    element.parent = null;
    sortChildren();
  }

  public void sortChildren() {
    if (style.display == DisplayType.FLEX) {
      children.sort(BY_ORDER);
      return;
    }

    children.sort(BY_INDEX);
  }

}
