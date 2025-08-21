package com.juliewoolie.nlayout;

import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.dom.style.DisplayType;
import com.juliewoolie.dom.style.VerticalAlign;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import org.joml.Vector2f;

public class FlowLayoutBox extends LayoutBox {

  final List<FlowLine> lines = new ObjectArrayList<>();

  float largestLineWidth = 0.0f;
  float combinedHeight = 0.0f;

  float availableWidth = 0.0f;

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
    lines.clear();

    float prew = this.largestLineWidth;
    float preh = this.combinedHeight;

    this.largestLineWidth = 0;
    this.combinedHeight = 0;

    findAvailableWidth(ctx);
    divyIntoLines(ctx);
    calculateVerticalMargins();
    calculateAutoMargins();
    sumUpLineSizes();

    out.x = largestLineWidth;
    out.y = combinedHeight;

    return prew != largestLineWidth || preh != combinedHeight;
  }

  @Override
  public void layout() {
    layoutSelf();
    layoutChildren();
  }

  private void layoutSelf() {
    Vector2f cpos = new Vector2f();
    Vector2f offset = new Vector2f();

    getContentStart(cpos);

    for (FlowLine line : lines) {
      offset.y += line.topMargin;

      for (LayoutNode node : line.nodes) {
        VerticalAlign vertAlign;
        float endGap = 0.0f;

        if (node instanceof LayoutBox box) {
          if (isMarginApplied(box)) {
            offset.x += box.style.margin.left;
            endGap = box.style.margin.right;
          } else {
            offset.x += box.style.marginInlineStart;
            endGap += box.style.marginInlineEnd;
          }
          vertAlign = box.style.verticalAlign;
        } else {
          vertAlign = VerticalAlign.DEFAULT;
        }

        float freeHeight = line.height - node.size.y;
        float yoff = switch (vertAlign) {
          case BOTTOM -> freeHeight;
          case MIDDLE -> freeHeight * 0.5f;
          case SUB -> freeHeight + (node.size.y * 0.5f);
          case SUPER -> -(node.size.y * 0.5f);
          default -> 0.0f;
        };

        node.position.x = cpos.x + offset.x;
        node.position.y = cpos.y - offset.y - yoff;

        offset.x += node.size.x + endGap;
      }

      offset.x = 0;
      offset.y += line.height + line.bottomMargin;
    }
  }

  private void sumUpLineSizes() {
    for (FlowLine line : lines) {
      largestLineWidth = Math.max(largestLineWidth, line.width);
      combinedHeight += line.height + line.topMargin + line.bottomMargin;
    }
  }

  private void calculateAutoMargins() {
    for (FlowLine line : lines) {
      if (line.nodes.size() != 1
          || !(line.nodes.getFirst() instanceof LayoutBox box)
          || !isMarginApplied(box)
      ) {
        continue;
      }

      boolean leftAuto = box.cstyle.marginLeft.isAuto();
      boolean rightAuto = box.cstyle.marginRight.isAuto();

      float freeSpace = availableWidth - box.size.x;
      float leftOffset = 0;

      if (leftAuto && rightAuto) {
        leftOffset = freeSpace * 0.5f;
      } else if (leftAuto) {
        leftOffset = freeSpace;
      } else {
        continue;
      }

      box.style.margin.left = leftOffset;
    }
  }

  private void calculateVerticalMargins() {
    for (int i = 0; i < lines.size(); i++) {
      FlowLine line = lines.get(i);

      for (int nodeIdx = 0; nodeIdx < line.nodes.size(); nodeIdx++) {
        LayoutNode node = line.nodes.get(nodeIdx);

        if (!(node instanceof LayoutBox box)) {
          continue;
        }

        float marginTop = 0.0f;
        float marginBottom = 0.0f;

        if (isMarginApplied(box)) {
          marginTop = box.style.margin.top;
          marginBottom = box.style.margin.bottom;
        }

        float freeVertSpace = line.height - node.size.y;
        float halfHeight = node.size.y * 0.5f;

        float effectiveTopMargin;
        float effectiveBtmMargin;

        switch (box.style.verticalAlign) {
          case MIDDLE:
            effectiveTopMargin = marginTop - (freeVertSpace * 0.5f);
            effectiveBtmMargin = marginBottom - (freeVertSpace * 0.5f);
            break;
          case BOTTOM:
            effectiveTopMargin = marginTop - freeVertSpace;
            effectiveBtmMargin = marginBottom;
            break;
          case TOP:
            effectiveTopMargin = marginTop;
            effectiveBtmMargin = marginBottom - freeVertSpace;
            break;
          case SUPER:
            effectiveTopMargin = marginTop + halfHeight;
            effectiveBtmMargin = marginBottom - freeVertSpace - halfHeight;
            break;
          case SUB:
            effectiveTopMargin = marginTop - freeVertSpace - halfHeight;
            effectiveBtmMargin = marginBottom + halfHeight;
            break;

          default:
            throw new IllegalStateException("Unexpected value: " + box.style.verticalAlign);
        }

        line.topMargin = Math.max(effectiveTopMargin, line.topMargin);
        line.bottomMargin = Math.max(effectiveBtmMargin, line.bottomMargin);
      }
    }
  }

  private void divyIntoLines(LayoutContext ctx) {
    Vector2f childSize = new Vector2f();
    FlowLine line = null;
    Rect margin = new Rect();

    for (LayoutNode node : nodes) {
      if (shouldIgnore(node)) {
        continue;
      }

      if (line == null) {
        line = new FlowLine();
      }

      boolean lbAfter = false;
      boolean lbBefore = false;

      measure(node, ctx, childSize);

      float yscale = 1.0f;

      if (node instanceof LayoutBox box) {
        if (isMarginApplied(box)) {
          margin.set(box.style.margin);
        } else {
          margin.left = box.style.marginInlineStart;
          margin.right = box.style.marginInlineEnd;
        }

        lbAfter = linebreakAfter(box);
        lbBefore = linebreakBefore(box);

        if (box.style.verticalAlign == VerticalAlign.SUPER
            || box.style.verticalAlign == VerticalAlign.SUB
        ) {
          yscale = 0.5f;
        }

      } else {
        margin.set(0);
      }

      float w = childSize.x + margin.x();

      boolean onEmptyLine = line.nodes.isEmpty();
      boolean tooBig = line.width + w > availableWidth;

      if ((lbBefore || tooBig) && !onEmptyLine) {
        lines.addLast(line);
        line = new FlowLine();
      }

      line.width += w;
      line.height = Math.max(line.height, childSize.y * yscale);
      line.nodes.add(node);

      if (lbAfter) {
        lines.addLast(line);
        line = null;
      }
    }

    if (line != null) {
      lines.addLast(line);
    }
  }

  private void findAvailableWidth(LayoutContext ctx) {
    availableWidth = size.x - getXBorder();
  }

  static class FlowLine {
    List<LayoutNode> nodes = new ObjectArrayList<>();

    float width = 0.0f;
    float height = 0.0f;

    float topMargin = 0.0f;
    float bottomMargin = 0.0f;
  }
}
