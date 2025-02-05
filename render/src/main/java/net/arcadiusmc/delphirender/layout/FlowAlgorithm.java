package net.arcadiusmc.delphirender.layout;

import java.util.List;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphirender.object.ElementRenderObject;
import net.arcadiusmc.delphirender.object.RenderObject;
import org.joml.Vector2f;

public class FlowAlgorithm implements LayoutAlgorithm {

  @Override
  public boolean measure(ElementRenderObject ro, MeasureContext ctx, Vector2f out) {
    boolean changed = false;

    out.set(0);

    List<RenderObject> childObjects = ro.getChildObjects();
    if (childObjects.isEmpty()) {
      return false;
    }

    Vector2f childSize = new Vector2f();
    for (RenderObject childObject : childObjects) {
      changed |= NLayout.measure(childObject, ctx, childSize);
      childObject.size.set(childSize);

      out.y += childSize.y;

      if (childObject instanceof ElementRenderObject er) {
        float xWidth = childSize.x + er.style.margin.x();
        out.x = Math.max(out.x, xWidth);
        out.y += er.style.margin.y();
      } else {
        out.x = Math.max(out.x, childSize.x);
      }
    }

    return changed;
  }

  @Override
  public void layout(ElementRenderObject ro) {
    Vector2f pos = new Vector2f();
    ro.getContentStart(pos);

    Vector2f childPos = new Vector2f();
    Vector2f offset = new Vector2f();

    for (RenderObject child : ro.getChildObjects()) {
      childPos.set(pos);

      Rect margin;

      if (child instanceof ElementRenderObject re) {
        margin = re.style.margin;

//        NLayout.getBoxSizingOffset(re, offset);
        childPos.x -= offset.x;
        childPos.y += offset.y;
      } else {
        margin = new Rect(0f);
      }

      childPos.x += margin.left;
      childPos.y -= margin.top;

      child.moveTo(childPos);

      pos.y -= (margin.top + margin.bottom + child.size.y);
    }
  }
}
