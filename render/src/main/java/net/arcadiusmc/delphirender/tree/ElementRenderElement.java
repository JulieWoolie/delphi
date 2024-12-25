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

  public void addChild(RenderElement child) {
    children.add(child);
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

  private void sortChildren() {
    if (style.display == DisplayType.FLEX) {
      children.sort(BY_ORDER);
      return;
    }

    children.sort(BY_INDEX);
  }

}
