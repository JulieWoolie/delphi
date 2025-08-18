package com.juliewoolie.nlayout;

import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.dom.style.DisplayType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;

public class FlowLayoutBox extends LayoutBox {

  private final List<Line> lines = new ObjectArrayList<>();

  public FlowLayoutBox(LayoutStyle style, ComputedStyleSet cstyle) {
    super(style, cstyle);
  }

  boolean linebreakBefore(LayoutBox ro) {
    return ro.style.display != DisplayType.INLINE;
  }

  boolean linebreakAfter(LayoutBox ro) {
    return ro.style.display == DisplayType.BLOCK;
  }

  boolean isMarginApplied(LayoutBox er) {
    return er.style.display != DisplayType.INLINE;
  }

  boolean shouldIgnore(LayoutNode object) {
    if (!(object instanceof LayoutBox er)) {
      return false;
    }

    return er.style.display == DisplayType.NONE;
  }

  @Override
  protected boolean measure(LayoutContext ctx, Vector2f out) {
    boolean changed = false;

    out.set(0);

    if (nodes.isEmpty()) {
      return false;
    }

    Vector2f childSize = new Vector2f(0);
    Vector2f lineSize = new Vector2f(0);
    Vector2f maxSize = new Vector2f(0);
    maxSize.set(ctx.screenSize);

    for (LayoutNode childObject : nodes) {
      if (shouldIgnore(childObject)) {
        continue;
      }

      changed |= measure(childObject, ctx, childSize);
      childObject.size.set(childSize);

      boolean lbAfter = false;
      boolean lbBefore = false;

      if (childObject instanceof LayoutBox er) {
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
  public void layout() {
    layoutSelf();
    layoutChildren();
  }

  public void layoutSelf() {
    Vector2f pos = new Vector2f();
    Vector2f childPos = new Vector2f();
    Vector2f size = new Vector2f();

    getContentStart(pos);
    getInnerSize(size);

    breakIntoLines(size);

    for (Line line : lines) {
      pos.y -= line.lineHeight;
      float lineX = pos.x;

      for (LayoutNode child : line.children) {
        childPos.set(pos);
        childPos.y += child.size.y;

        if (child instanceof LayoutBox er) {
          if (isMarginApplied(er)) {
            childPos.x += er.style.margin.left;
            pos.x += er.style.margin.x();
          } else {
            pos.x += er.style.marginInlineStart + er.style.marginInlineEnd;
            childPos.x += er.style.marginInlineStart;
          }
        }

        pos.x += child.size.x;
        child.position.set(childPos);
      }

      pos.y -= line.largestBottomMargin;
      pos.x = lineX;
    }

    // Apply "margin-left: auto;" or "margin-right: auto;" values
    for (LayoutNode childObject : nodes) {
      if (!(childObject instanceof LayoutBox er)) {
        continue;
      }

      ComputedStyleSet set = er.cstyle;
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

      LayoutStyle style = er.style;

      if (set.marginLeft.isAuto()) {
        style.margin.left = dif;
        childObject.moveTo(childObject.position.x + dif, childObject.position.y);
      }
      if (set.marginRight.isAuto()) {
        style.margin.right = dif;
      }
    }
  }

  private void breakIntoLines(Vector2f maxSize) {
    lines.clear();

    float lineSizeX = 0.0f;

    Line currentLine = null;

    for (LayoutNode child : super.nodes) {
      if (shouldIgnore(child)) {
        continue;
      }

      boolean lbBefore = false;
      boolean lbAfter = false;

      float bm = 0.0f;
      float childMarginX = 0.0f;

      if (child instanceof LayoutBox er) {
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
        lines.add(currentLine);
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
        lines.add(currentLine);
        currentLine = null;
        lineSizeX = 0.0f;
      }
    }

    if (currentLine != null) {
      lines.add(currentLine);
    }

    for (Line line : lines) {
      float largestYSize = 0.0f;

      for (LayoutNode child : line.children) {
        float y = child.size.y;

        if (child instanceof LayoutBox er && isMarginApplied(er)) {
          y += er.style.margin.top;
        }

        largestYSize = Math.max(y, largestYSize);
      }

      line.lineHeight = largestYSize;
    }
  }

  private static class Line {
    final List<LayoutNode> children = new ArrayList<>();
    float largestBottomMargin = 0.0f;
    float lineHeight = 0.0f;
  }
}
