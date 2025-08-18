package com.juliewoolie.nlayout;

import static com.juliewoolie.nlayout.LayoutStyle.UNSET;

import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.ValueOrAuto;
import com.juliewoolie.dom.style.AlignItems;
import com.juliewoolie.dom.style.DisplayType;
import com.juliewoolie.dom.style.FlexDirection;
import com.juliewoolie.dom.style.FlexWrap;
import com.juliewoolie.dom.style.JustifyContent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

public class FlexLayoutBox extends LayoutBox {

  private final List<FlexItem> items = new ObjectArrayList<>();
  private List<FlexLine> lines;

  private float mainSize = 0.0f;
  private float crossSize = 0.0f;

  public FlexLayoutBox(LayoutStyle style, ComputedStyleSet cstyle) {
    super(style, cstyle);
  }

  boolean isMainAxisVertical() {
    return cstyle.flexDirection == FlexDirection.COLUMN
        || cstyle.flexDirection == FlexDirection.COLUMN_REVERSE;
  }

  boolean isSingleLine() {
    return cstyle.flexWrap == FlexWrap.NOWRAP;
  }

  boolean hasGaps() {
    return style.justify != JustifyContent.SPACE_AROUND
        && style.justify != JustifyContent.SPACE_BETWEEN
        && style.justify != JustifyContent.SPACE_EVENLY;
  }

  ValueOrAuto getMainSize(ComputedStyleSet cstyle) {
    return isMainAxisVertical() ? cstyle.height : cstyle.width;
  }

  float getMainSize(Vector2f v) {
    return isMainAxisVertical() ? v.y : v.x;
  }

  float getCrossSize(Vector2f v) {
    return isMainAxisVertical() ? v.x : v.y;
  }

  void setSizes(Vector2f vec, float main, float cross) {
    if (isMainAxisVertical()) {
      vec.x = cross;
      vec.y = main;
    } else {
      vec.x = main;
      vec.y = cross;
    }
  }

  @Override
  protected boolean measure(LayoutContext ctx, Vector2f out) {
    items.clear();
    lines = null;

    float preMainSize = mainSize;
    float preCrossSize = crossSize;

    gatherItems();
    calculateBasis(ctx);
    calculateItemMainSizes();
    calculateItemCrossSizes();
    boolean childrenChanged = applyCrossAndMainSizes(ctx);

    out.set(0);

    if (isMainAxisVertical()) {
      out.x = crossSize;
      out.y = mainSize;
    } else {
      out.x = mainSize;
      out.y = crossSize;
    }

    return childrenChanged || (mainSize != preMainSize || crossSize != preCrossSize);
  }

  @Override
  public void layout() {
    Vector2f innerSpace = new Vector2f();
    getInnerSize(innerSpace);

    float mainInnerSpace = getMainSize(innerSpace);
    float freeMainSpace = mainInnerSpace - mainSize;

    float mainGap = 0;
    float crossGap = 0;

    if (isMainAxisVertical()) {
      mainGap = style.rowGap;
      crossGap = style.columnGap;
    } else {
      mainGap = style.columnGap;
      crossGap = style.rowGap;
    }

    JustifyContent justify = style.justify;

    Vector2f cpos = new Vector2f();
    getContentStart(cpos);

    Vector2f offset = new Vector2f(0);

    for (int i = 0; i < lines.size(); i++) {
      FlexLine line = lines.get(i);

      float startingSpace = 0.0f;
      float lineMainGap = mainGap;

      if (isMainAxisVertical()) {
        offset.y = 0;
      } else {
        offset.x = 0;
      }

      switch (justify) {
        case FLEX_END:
          startingSpace = freeMainSpace;
          break;
        case CENTER:
          startingSpace = freeMainSpace * 0.5f;
          break;
        case SPACE_BETWEEN:
          lineMainGap = freeMainSpace / line.items.size();
          break;
        case SPACE_AROUND:
          lineMainGap = freeMainSpace / line.items.size();
          startingSpace = (freeMainSpace / (line.items.size() + 1)) * 0.5f;
          break;
        case SPACE_EVENLY:
          lineMainGap = freeMainSpace / line.items.size();
          startingSpace = (freeMainSpace / (line.items.size() + 2)) * 0.5f;
          break;
      }

      for (int itemIdx = 0; itemIdx < line.items.size(); itemIdx++) {
        FlexItem item = line.items.get(itemIdx);

        if (itemIdx == 0) {
          if (isMainAxisVertical()) {
            offset.y -= startingSpace;
          } else {
            offset.x += startingSpace;
          }
        } else {
          if (isMainAxisVertical()) {
            offset.y -= lineMainGap;
          } else {
            offset.x += lineMainGap;
          }
        }

        float posx = cpos.x + offset.x;
        float posy = cpos.y + offset.y;

        if (item.alignSelf == AlignItems.CENTER) {
          float off = (line.crossSize - item.crossSize) * 0.5f;
          if (isMainAxisVertical()) {
            posx += off;
          } else {
            posy -= off;
          }
        } else if (item.alignSelf == AlignItems.FLEX_END) {
          float off = line.crossSize - item.crossSize;
          if (isMainAxisVertical()) {
            posx += off;
          } else {
            posy -= off;
          }
        }

        item.node.position.x = posx;
        item.node.position.y = posy;

        if (isMainAxisVertical()) {
          offset.y -= item.mainSize;
        } else {
          offset.x += item.mainSize;
        }
      }

      if (isMainAxisVertical()) {
        offset.x += line.crossSize + crossGap;
        offset.y = 0;
      } else {
        offset.x = 0;
        offset.y -= line.crossSize + crossGap;
      }
    }

    layoutChildren();
  }

  private void gatherItems() {
    nodes
        .stream()
        .filter(renderObject -> {
          if (!(renderObject instanceof LayoutBox ero)) {
            return true;
          }

          ComputedStyleSet cstyle = ero.cstyle;
          return cstyle.display != DisplayType.NONE;
        })
        .map(ro -> {
          FlexItem i = new FlexItem(ro);

          if (ro instanceof LayoutBox ero) {
            ComputedStyleSet cstyle = ero.cstyle;

            if (cstyle.order != 0) {
              i.order = cstyle.order;
            } else {
              i.order = ro.domIndex;
            }

            i.shrink = ero.style.shrink;
            i.grow = ero.style.grow;

            if (cstyle.alignSelf == null) {
              i.alignSelf = this.style.alignItems;
            } else {
              i.alignSelf = cstyle.alignSelf;
            }
          } else {
            i.order = ro.domIndex;
          }

          return i;
        })
        .sorted()
        .forEach(items::add);
  }

  boolean applyCrossAndMainSizes(LayoutContext ctx) {
    Vector2f discard = new Vector2f();
    boolean changed = false;

    Vector2f innerSize = new Vector2f();

    for (int i = 0; i < lines.size(); i++) {
      FlexLine line = lines.get(i);
      for (int itemIdx = 0; itemIdx < line.items.size(); itemIdx++) {
        FlexItem item = line.items.get(itemIdx);
        setSizes(item.node.size, item.mainSize, item.crossSize);

        if (item.node instanceof LayoutBox box) {
          box.getInnerSize(innerSize);

          ctx.parentSizes.push(innerSize);
          changed = box.measure(ctx, discard);
          ctx.parentSizes.pop();
        }
      }
    }

    return changed;
  }

  void calculateItemCrossSizes() {
    crossSize = 0.0f;

    float crossGap;
    boolean crossSizeSet = false;

    Vector2f innerSize = new Vector2f();
    getInnerSize(innerSize);

    if (isMainAxisVertical()) {
      crossGap = style.columnGap;

      if (!cstyle.width.isAuto() && lines.size() == 1) {
        crossSize = innerSize.x;
        crossSizeSet = true;
      }
    } else {
      crossGap = style.rowGap;

      if (!cstyle.height.isAuto() && lines.size() == 1) {
        crossSize = innerSize.y;
        crossSizeSet = true;
      }
    }

    for (int i = 0; i < lines.size(); i++) {
      FlexLine line = lines.get(i);

      if (crossSizeSet) {
        line.crossSize = crossSize;
      }

      for (int lidx = 0; lidx < line.items.size(); lidx++) {
        FlexItem item = line.items.get(lidx);
        item.crossSize = getCrossSize(item.node.size);
        line.crossSize = Math.max(line.crossSize, item.crossSize);
      }

      if (!crossSizeSet) {
        crossSize += line.crossSize;
        if (i != 0) {
          crossSize += crossGap;
        }
      }

      for (int lidx = 0; lidx < line.items.size(); lidx++) {
        FlexItem item = line.items.get(lidx);

        if (item.alignSelf == AlignItems.STRETCH) {
          item.crossSize = line.crossSize;
        }
      }
    }
  }

  void calculateItemMainSizes() {
    mainSize = 0.0f;
    float gap = 0.0f;

    if (hasGaps()) {
      gap = isMainAxisVertical() ? style.rowGap : style.columnGap;
    }

    for (int i = 0; i < items.size(); i++) {
      if (i != 0) {
        mainSize += gap;
      }

      FlexItem item = items.get(i);
      mainSize += item.flexbasis;
      item.mainSize = item.flexbasis;
    }

    Vector2f innerSize = new Vector2f();
    getInnerSize(innerSize);

    float availableMainSize = getMainSize(innerSize);

    if (mainSize > availableMainSize) {
      // SHRINK or WRAP
      //   If WRAP, then growth has to be applied as well to each line separately
      //   If SHRINK, then idk man, this shit is impossible to do

      if (isSingleLine()) {
        // SHRINK if only I knew how
        int shrinkTotal = items.stream().filter(i -> i.shrink > 0)
            .mapToInt(value -> value.shrink)
            .sum();

        float dif = mainSize - availableMainSize;

        if (shrinkTotal == 0) {
          float perItem = dif / items.size();
          for (int i = 0; i < items.size(); i++) {
            FlexItem item = items.get(i);
            item.mainSize -= perItem;
          }
        } else {
          float perItem = dif / shrinkTotal;

          for (int i = 0; i < items.size(); i++) {
            FlexItem item = items.get(i);
            if (item.shrink < 1) {
              continue;
            }

            float shrink = perItem * item.shrink;
            item.mainSize -= shrink;
          }
        }

        FlexLine line = new FlexLine();
        line.items.addAll(items);
        lines = List.of(line);

        return;
      }

      lines = new ObjectArrayList<>();

      FlexLine currentLine = null;

      for (int i = 0; i < this.items.size(); i++) {
        FlexItem item = items.get(i);

        if (currentLine == null) {
          currentLine = new FlexLine();
        }

        float nlen = currentLine.lineLength + item.mainSize;

        if (currentLine.lineLength == 0.0f || nlen < availableMainSize) {
          if (!currentLine.items.isEmpty()) {
            nlen += gap;
          }

          currentLine.items.addLast(item);
          currentLine.lineLength = nlen;
          continue;
        }

        if (style.flexWrap == FlexWrap.WRAP_REVERSE) {
          lines.addFirst(currentLine);
        } else {
          lines.addLast(currentLine);
        }

        currentLine = new FlexLine();
        currentLine.items.add(item);
        currentLine.lineLength = item.mainSize;
      }

      if (currentLine != null) {
        if (style.flexWrap == FlexWrap.WRAP_REVERSE) {
          lines.addFirst(currentLine);
        } else {
          lines.addLast(currentLine);
        }
      }

      for (int i = 0; i < lines.size(); i++) {
        FlexLine line = lines.get(i);
        applyGrowth(line.items, line.lineLength, availableMainSize);
      }

      return;
    }

    FlexLine line = new FlexLine();
    line.items.addAll(items);
    lines = List.of(line);

    if (mainSize < availableMainSize) {
      boolean anyGrew = applyGrowth(items, mainSize, availableMainSize);
      if (anyGrew) {
        mainSize = availableMainSize;
      }
    }
  }

  boolean applyGrowth(List<FlexItem> items, float lineLen, float totalFreeSpace) {
    int growingItems = items.stream()
        .filter(i -> i.grow > 0)
        .mapToInt(value -> value.grow)
        .sum();

    if (growingItems < 1) {
      return false;
    }

    float dif = totalFreeSpace - lineLen;
    float growth = dif / growingItems;

    for (int i = 0; i < items.size(); i++) {
      FlexItem item = items.get(i);
      if (item.grow < 1) {
        continue;
      }
      item.mainSize = item.flexbasis + (growth * item.grow);
    }

    return true;
  }

  void calculateBasis(LayoutContext ctx) {
    float fbBasis = style.flexBasis;

    for (FlexItem item : items) {
      Vector2f size = new Vector2f();
      measure(item.node, ctx, size);

      if (item.node instanceof LayoutBox ero) {
        ComputedStyleSet cstyle = ero.cstyle;
        LayoutStyle style = ero.style;

        ValueOrAuto mainSize = getMainSize(cstyle);
        ValueOrAuto basis = cstyle.flexBasis;

        if (mainSize.isAuto() && basis.isAuto()) {
          if (fbBasis > 0) {
            item.flexbasis = fbBasis;
          } else {
            item.flexbasis = getMainSize(size);
          }
          continue;
        }

        if (!mainSize.isAuto()) {
          item.flexbasis = getMainSize(style.size);
          continue;
        }

        item.flexbasis = style.flexBasis;
      } else {
        item.flexbasis = getMainSize(item.node.size);
      }
    }
  }

  static class FlexLine {
    private final List<FlexItem> items = new ObjectArrayList<>();
    private float crossSize = 0.0f;
    private float lineLength = 0.0f;
  }

  static class FlexItem implements Comparable<FlexItem> {

    private final LayoutNode node;
    float flexbasis = UNSET;
    int order = 0;
    int shrink = 0;
    int grow = 0;
    AlignItems alignSelf;

    float mainSize = 0.0f;
    float crossSize = 0.0f;

    public FlexItem(LayoutNode node) {
      this.node = node;
    }

    @Override
    public int compareTo(@NotNull FlexItem o) {
      return Integer.compare(order, o.order);
    }
  }
}
