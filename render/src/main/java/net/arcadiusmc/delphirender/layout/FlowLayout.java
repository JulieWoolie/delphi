package net.arcadiusmc.delphirender.layout;

import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphirender.tree.ElementRenderElement;
import net.arcadiusmc.delphirender.tree.RenderElement;
import org.joml.Vector2f;

public class FlowLayout implements LayoutStyle {

  @Override
  public void firstLayoutPass(RenderElement node) {
    if (!(node instanceof ElementRenderElement el)) {
      return;
    }

    Vector2f pos = new Vector2f();
    node.getContentStart(pos);

    Vector2f childPos = new Vector2f();

    for (RenderElement child : el.getChildren()) {
      childPos.set(pos);

      Rect margin = child.getStyle().margin;

      childPos.x += margin.left;
      childPos.y -= margin.top;

      child.moveTo(childPos);

      pos.y -= (margin.top + margin.bottom + child.getSize().y);
    }
  }

  @Override
  public void measure(ElementRenderElement el, Vector2f out) {
    Rect margin = new Rect();
    for (RenderElement child : el.getChildren()) {
      margin.set(child.getStyle().margin);
      margin.max(0.0f);

      out.y += margin.y() + child.size.y;
      out.x = Math.max(out.x, (margin.x() + child.size.x));
    }
  }
}
