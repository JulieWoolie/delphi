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
    applyCrossAndMainSizes(ctx);

    out.set(0);

    if (isMainAxisVertical()) {
      out.x = crossSize;
      out.y = mainSize;
    } else {
      out.x = mainSize;
      out.y = crossSize;
    }

    return mainSize != preMainSize || crossSize != preCrossSize;
  }

  @Override
  protected void layoutSelf() {
    // This function hurts my head, and it's the epitome of having to
    // juggle x,y vectors as well as tracking the main and cross sizes
    // of the flex item and container.

    Vector2f innerSpace = new Vector2f();
    getInnerSize(innerSpace);

    float mainInnerSpace = getMainSize(innerSpace);

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

    // cpos = current position, basically the position
    // we're going to place the next element
    Vector2f cpos = new Vector2f();
    getContentStart(cpos);

    Vector2f offset = new Vector2f(0);

    // 'align-content'??? Never heard of her

    for (int i = 0; i < lines.size(); i++) {
      FlexLine line = lines.get(i);

      float startingSpace = 0.0f;
      float lineMainGap = mainGap;

      if (isMainAxisVertical()) {
        offset.y = 0;
      } else {
        offset.x = 0;
      }

      float freeMainSpace = mainInnerSpace - line.lineLength;

      switch (justify) {
        case FLEX_END:
          startingSpace = freeMainSpace;
          break;
        case CENTER:
          startingSpace = freeMainSpace * 0.5f;
          break;

        // There's items-1 gaps between the items, so to get
        // the size of each gap, divide the free space by items-1
        case SPACE_BETWEEN:
          lineMainGap = freeMainSpace / (line.items.size() - 1);
          break;

        // Each item has an equal gap on it's left and right (in the case of row direction)
        case SPACE_AROUND:
          float d = freeMainSpace / (line.items.size() * 2);
          lineMainGap = d * 2;
          startingSpace = d;
          break;

        // There's items+1 gaps between each item (and also before and after)
        case SPACE_EVENLY:
          float a = freeMainSpace / (line.items.size() + 1);
          lineMainGap = a;
          startingSpace = a;
          break;
      }

      // So, if there's a gap set, we need to use it, even in the case
      // of the 'space-*' justifies, this max() call is also why we
      // keep track of totalGap in calculateItemMainSizes()
      lineMainGap = Math.max(lineMainGap, mainGap);

      for (int itemIdx = 0; itemIdx < line.items.size(); itemIdx++) {
        FlexItem item = line.items.get(itemIdx);

        // I hate this, but it works
        // It offsets the current line's placement position by either
        // the starting space if this is the first item, or by the gap,
        // if it's not
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

        // Apply item alignments, again, the god-damn axes checks
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

        // This is impossible to keep track of in my head
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
            i.alignSelf = this.style.alignItems;
            i.shrink = 0;
            i.grow = 0;
          }

          return i;
        })
        .sorted()
        .forEach(items::add);
  }

  void applyCrossAndMainSizes(LayoutContext ctx) {
    Vector2f discard = new Vector2f();
    Vector2f innerSize = new Vector2f();

    for (int i = 0; i < lines.size(); i++) {
      FlexLine line = lines.get(i);

      for (int itemIdx = 0; itemIdx < line.items.size(); itemIdx++) {
        FlexItem item = line.items.get(itemIdx);
        setSizes(item.node.size, item.mainSize, item.crossSize);

        if (item.node instanceof LayoutBox box) {
          box.getInnerSize(innerSize);

          ctx.parentSizes.push(innerSize);
          box.measure(ctx, discard);
          ctx.parentSizes.pop();
        }
      }
    }
  }

  void calculateItemCrossSizes() {
    crossSize = 0.0f;

    float crossGap;
    boolean crossSizeSet = false;

    Vector2f innerSize = new Vector2f();
    getInnerSize(innerSize);

    // There's some stupid stuff here to do with the 'align-items: stretch'
    // not being applied correctly unless these cstyle.isAuto() checks
    // weren't here
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

        // If we're stretching, stretch... duh lmao
        if (item.alignSelf == AlignItems.STRETCH) {
          item.crossSize = line.crossSize;
        }
      }
    }
  }

  void calculateItemMainSizes() {
    mainSize = 0.0f;

    float gap = isMainAxisVertical() ? style.rowGap : style.columnGap;
    float totalGap = 0.0f;

    // Calculate container's main size (kinda) by adding
    // all flex items' flex-basis together (+ gap)
    // Save the total gap we've added to the size (we might need it later)
    for (int i = 0; i < items.size(); i++) {
      if (i != 0) {
        mainSize += gap;
        totalGap += gap;
      }

      FlexItem item = items.get(i);
      mainSize += item.flexbasis;
      item.mainSize = item.flexbasis;
    }

    // Get the inner size of the container (this.size - padding - border - outline)
    Vector2f innerSize = new Vector2f();
    getInnerSize(innerSize);

    float availableMainSize = getMainSize(innerSize);

    // If the combined width of all items is greater
    // than the size actually available to us
    if (mainSize > availableMainSize) {

      // If we're not wrapping, then we have to shrink each item down
      if (isSingleLine()) {
        // Combine all the 'flex-shrink' values of the items
        int shrinkTotal = items.stream().filter(i -> i.shrink > 0)
            .mapToInt(value -> value.shrink)
            .sum();

        // How much we have to shrink
        float dif = mainSize - availableMainSize;

        // If no elements have a set 'flex-shrink' => Shrink all
        if (shrinkTotal == 0) {
          float perItem = dif / items.size();
          for (int i = 0; i < items.size(); i++) {
            FlexItem item = items.get(i);
            item.mainSize -= perItem;
          }
        } else {
          // Some have a set 'flex-shrink' => Shrink those
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
        line.lineLength = availableMainSize;
        lines = List.of(line);

        return;
      }

      // 'flex-wrap' is set to either 'wrap' or 'wrap-reverse'
      lines = new ObjectArrayList<>();
      FlexLine currentLine = null;

      // Stupid bullshit that gathers items into lines by measuring
      // them and then dividing them (+ gap)
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

      // Apply any set 'flex-grow' on items on any line
      for (int i = 0; i < lines.size(); i++) {
        FlexLine line = lines.get(i);
        if (applyGrowth(line.items, line.lineLength, availableMainSize)) {
          line.lineLength = availableMainSize;
        }
      }

      return;
    }

    // If 'justify-content' is not a basic start, end or center value
    // This is to prevent it messing with calculations later on when
    // actually laying out the elments
    if (!hasGaps()) {
      mainSize -= totalGap;
    }

    FlexLine line = new FlexLine();
    line.items.addAll(items);
    line.lineLength = mainSize;
    lines = List.of(line);

    // If we have space left to grow, then grow
    if (mainSize < availableMainSize) {
      boolean anyGrew = applyGrowth(items, mainSize, availableMainSize);
      if (anyGrew) {
        mainSize = availableMainSize;
        line.lineLength = mainSize;
      }
    }
  }

  boolean applyGrowth(List<FlexItem> items, float lineLen, float totalFreeSpace) {
    // Only items with a non-zero, positive 'flex-grow' value are grown;
    // the rest keep their size. The 'flex-grow' value also determines
    // the relative amount of growth they receive, effectively as a growth
    // weight value.
    int growingItems = items.stream()
        .filter(i -> i.grow > 0)
        .mapToInt(value -> value.grow)
        .sum();

    if (growingItems < 1) {
      return false;
    }

    float dif = totalFreeSpace - lineLen;
    if (dif <= 0) {
      return false;
    }

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

    //
    // I have no idea if this part follows the W3C spec, but I
    // can't read that technobabble anyway, so who cares. But
    // basically, this part uses either the 'flex-basis'
    // property or a set 'width' property as the base size
    // of each item.
    //
    // If an item doesn't have a set 'flex-basis', use the
    // flex container's, if set, otherwise, idk, have fun
    // figuring ts out
    //
    // FOR MORE INFO:
    // https://www.quirksmode.org/css/flexbox-algorithm.html
    // (love u, Marc Thiele)
    //

    for (FlexItem item : items) {
      Vector2f size = new Vector2f();
      measure(item.node, ctx, size);
      float measuredMain = getMainSize(size);

      if (item.node instanceof LayoutBox ero) {
        ComputedStyleSet cstyle = ero.cstyle;
        LayoutStyle style = ero.style;

        ValueOrAuto mainSize = getMainSize(cstyle);
        ValueOrAuto basis = cstyle.flexBasis;

        if (mainSize.isAuto() && basis.isAuto()) {
          if (fbBasis > 0) {
            item.flexbasis = fbBasis;
          } else {
            item.flexbasis = measuredMain;
          }
          continue;
        }

        if (!mainSize.isAuto()) {
          item.flexbasis = getMainSize(style.size);
        } else {
          item.flexbasis = style.flexBasis;
        }

        if (item.flexbasis == UNSET) {
          item.flexbasis = measuredMain;
        }
      } else {
        item.flexbasis = measuredMain;
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
