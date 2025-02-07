package net.arcadiusmc.delphirender.layout;

import java.util.ArrayList;
import java.util.List;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.object.ElementRenderObject;
import net.arcadiusmc.delphirender.object.RenderObject;
import net.arcadiusmc.dom.style.BoxSizing;
import net.arcadiusmc.dom.style.DisplayType;
import org.joml.Vector2f;

public class FlowAlgorithm implements LayoutAlgorithm {

  boolean linebreakBefore(ElementRenderObject ro) {
    return ro.style.display != DisplayType.INLINE;
  }

  boolean linebreakAfter(ElementRenderObject ro) {
    return ro.style.display == DisplayType.BLOCK;
  }

  boolean isMarginApplied(ElementRenderObject er) {
    return er.style.display != DisplayType.INLINE;
  }

  @Override
  public boolean measure(ElementRenderObject ro, MeasureContext ctx, Vector2f out) {
    boolean changed = false;

    out.set(0);

    List<RenderObject> childObjects = ro.getChildObjects();
    if (childObjects.isEmpty()) {
      return false;
    }

    Vector2f childSize = new Vector2f(0);
    Vector2f lineSize = new Vector2f(0);
    Vector2f maxSize = getMaxSize(ro);

    for (RenderObject childObject : childObjects) {
      changed |= NLayout.measure(childObject, ctx, childSize);
      childObject.size.set(childSize);

      boolean lbAfter = false;
      boolean lbBefore = false;

      if (childObject instanceof ElementRenderObject er) {
        if (isMarginApplied(er)) {
          childSize.x += er.style.margin.x();
          childSize.y += er.style.margin.y();
        } else {
          childSize.x += er.style.marginInlineStart + er.style.marginInlineEnd;
        }

        lbAfter = linebreakAfter(er);
        lbBefore = linebreakBefore(er);
      }

      boolean onEmptyLine = lineSize.x == 0 && lineSize.y == 0;
      boolean tooBig = lineSize.x + childSize.x > maxSize.x;

      if ((lbBefore || tooBig) && !onEmptyLine) {
        out.y += lineSize.y;
        out.x = Math.max(lineSize.x, out.x);
        lineSize.set(0);
      }

      lineSize.x += childSize.x;
      lineSize.y = Math.max(lineSize.y, childSize.y);

      if (lbAfter) {
        out.y += lineSize.y;
        out.x = Math.max(lineSize.x, out.x);
        lineSize.set(0);
      }
    }

    if (lineSize.x != 0 || lineSize.y != 0) {
      out.y += lineSize.y;
      out.x = Math.max(lineSize.x, out.x);
    }

    return changed;
  }

  @Override
  public void layout(ElementRenderObject ro) {
    Vector2f pos = new Vector2f();
    Vector2f childPos = new Vector2f();
    Vector2f size = new Vector2f();

    ro.getContentStart(pos);
    ro.getContentSize(size);

    List<Line> lines = breakIntoLines(ro, size);

    for (Line line : lines) {
      pos.y -= line.lineHeight;
      float lineX = pos.x;

      for (RenderObject child : line.children) {
        childPos.set(pos);
        childPos.y += child.size.y;

        if (child instanceof ElementRenderObject er) {
          if (isMarginApplied(er)) {
            childPos.x += er.style.margin.left;
            pos.x += er.style.margin.x();
          } else {
            pos.x += er.style.marginInlineStart + er.style.marginInlineEnd;
            childPos.x += er.style.marginInlineStart;
          }
        }

        pos.x += child.size.x;

        child.moveTo(childPos);
      }

      pos.y -= line.largestBottomMargin;
      pos.x = lineX;
    }

    // Apply "margin-left: auto;" or "margin-right: auto;" values
    for (RenderObject childObject : ro.getChildObjects()) {
      if (!(childObject instanceof ElementRenderObject er)) {
        continue;
      }

      ComputedStyleSet set = er.computedStyleSet;
      if (!set.marginLeft.isAuto() && !set.marginRight.isAuto()) {
        continue;
      }
      if (set.display != DisplayType.BLOCK) {
        continue;
      }

      float px = size.x;
      float cx = childObject.size.x;

      float dif = px - cx;

      if (set.marginRight.isAuto() && set.marginLeft.isAuto()) {
        dif *= 0.5f;
      }

      FullStyle style = er.style;

      if (set.marginLeft.isAuto()) {
        style.margin.left = dif;
        childObject.moveTo(childObject.position.x + dif, childObject.position.y);
      }
      if (set.marginRight.isAuto()) {
        style.margin.right = dif;
      }
    }
  }

  private Vector2f getMaxSize(ElementRenderObject ro) {
    Vector2f out = new Vector2f();
    ComputedStyleSet comp = ro.computedStyleSet;
    FullStyle style = ro.style;

    Vector2f contentSize = new Vector2f();
    Vector2f maxSize = new Vector2f();

    maxSize.set(style.maxSize);
    ro.getContentSize(contentSize);

    if (style.boxSizing != BoxSizing.CONTENT_BOX) {
      maxSize.x -= style.outline.x() + style.border.x() + style.padding.x();
      maxSize.y -= style.outline.y() + style.border.y() + style.padding.y();
    }

    if (comp.maxWidth.isAuto()) {
      out.x = contentSize.x;
    } else {
      out.x = Math.min(contentSize.x, maxSize.x);
    }

    if (comp.maxHeight.isAuto()) {
      out.y = contentSize.y;
    } else {
      out.y = Math.min(contentSize.y, maxSize.y);
    }

    return out;
  }

  private List<Line> breakIntoLines(ElementRenderObject ro, Vector2f maxSize) {
    float lineSizeX = 0.0f;

    List<RenderObject> children = ro.getChildObjects();
    List<Line> output = new ArrayList<>();
    Line currentLine = null;

    for (RenderObject child : children) {
      boolean lbBefore = false;
      boolean lbAfter = false;

      float bm = 0.0f;
      float childMarginX = 0.0f;

      if (child instanceof ElementRenderObject er) {
        lbBefore = linebreakBefore(er);
        lbAfter = linebreakAfter(er);

        if (isMarginApplied(er)) {
          bm = er.style.margin.bottom;
          childMarginX = er.style.margin.x();
        } else {
          childMarginX = er.style.marginInlineStart + er.style.marginInlineEnd;
        }
      }

      boolean onEmptyLine = currentLine == null;
      boolean tooBig = lineSizeX + child.size.x > maxSize.x;

      if ((tooBig || lbBefore) && !onEmptyLine) {
        output.add(currentLine);
        currentLine = null;
        lineSizeX = 0.0f;
      }

      if (currentLine == null) {
        currentLine = new Line();
      }

      lineSizeX += child.size.x + childMarginX;

      currentLine.children.add(child);
      currentLine.largestBottomMargin = Math.max(currentLine.largestBottomMargin, bm);

      if (lbAfter) {
        output.add(currentLine);
        currentLine = null;
        lineSizeX = 0.0f;
      }
    }

    if (currentLine != null) {
      output.add(currentLine);
    }

    for (Line line : output) {
      float largestYSize = 0.0f;

      for (RenderObject child : line.children) {
        float y = child.size.y;

        if (child instanceof ElementRenderObject er && isMarginApplied(er)) {
          y += er.style.margin.top;
        }

        largestYSize = Math.max(y, largestYSize);
      }

      line.lineHeight = largestYSize;
    }

    return output;
  }

  private class Line {
    final List<RenderObject> children = new ArrayList<>();

    float largestBottomMargin = 0.0f;
    float lineHeight = 0.0f;
  }
}
