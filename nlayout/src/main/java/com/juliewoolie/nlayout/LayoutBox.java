package com.juliewoolie.nlayout;

import static com.juliewoolie.nlayout.LayoutStyle.UNSET;

import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.ValueOrAuto;
import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.FlexDirection;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import lombok.Getter;
import org.joml.Vector2f;

@Getter
public abstract class LayoutBox extends LayoutNode {

  /**
   * The size of a single pixel on an unscaled text display
   */
  static final float CHAR_PX_SIZE_X = 0.025f;
  
  /**
   * The width of the '0' character in pixels
   */
  static final float LEN0 = 5;

  /**
   * The width of the '0' character in unscaled entity pixels
   */
  static final float LEN0_PX = LEN0 * CHAR_PX_SIZE_X;
  
  static final byte X = 0;
  static final byte Y = 1;
  
  protected final LayoutStyle style;
  protected final ComputedStyleSet cstyle;
  protected final List<LayoutNode> nodes = new ObjectArrayList<>();

  public LayoutBox(LayoutStyle style, ComputedStyleSet cstyle) {
    this.style = style;
    this.cstyle = cstyle;
  }
  
  protected void transferComputedStyle(LayoutContext ctx) {
    style.display = cstyle.display;
    style.alignItems = cstyle.alignItems;
    style.alignSelf = cstyle.alignSelf;
    style.flexDirection = cstyle.flexDirection;
    style.flexWrap = cstyle.flexWrap;
    style.justify = cstyle.justifyContent;
    style.order = cstyle.order;
    style.grow = cstyle.grow;
    style.shrink = cstyle.shrink;
    style.boxSizing = cstyle.boxSizing;
    style.visibility = cstyle.visibility;
    style.verticalAlign = cstyle.verticalAlign;

    style.size.x = resolve(cstyle.width, ctx, UNSET, X);
    style.size.y = resolve(cstyle.height, ctx, UNSET, Y);

    style.fontSize = resolveFontSize(cstyle.fontSize);

    style.minSize.x = resolve(cstyle.minWidth, ctx, UNSET, X);
    style.minSize.y = resolve(cstyle.minHeight, ctx, UNSET, Y);
    style.maxSize.x = resolve(cstyle.maxWidth, ctx, UNSET, X);
    style.maxSize.y = resolve(cstyle.maxHeight, ctx, UNSET, Y);

    style.margin.top = resolve(cstyle.marginTop, ctx, 0, Y);
    style.margin.right = resolve(cstyle.marginRight, ctx, 0, X);
    style.margin.bottom = resolve(cstyle.marginBottom, ctx, 0, Y);
    style.margin.left = resolve(cstyle.marginLeft, ctx, 0, X);

    style.outline.top = resolve(cstyle.outlineTop, ctx, 0, Y);
    style.outline.right = resolve(cstyle.outlineRight, ctx, 0, X);
    style.outline.bottom = resolve(cstyle.outlineBottom, ctx, 0, Y);
    style.outline.left = resolve(cstyle.outlineLeft, ctx, 0, X);

    style.border.top = resolve(cstyle.borderTop, ctx, 0, Y);
    style.border.right = resolve(cstyle.borderRight, ctx, 0, X);
    style.border.bottom = resolve(cstyle.borderBottom, ctx, 0, Y);
    style.border.left = resolve(cstyle.borderLeft, ctx, 0, X);

    style.padding.top = resolve(cstyle.paddingTop, ctx, 0, Y);
    style.padding.right = resolve(cstyle.paddingRight, ctx, 0, X);
    style.padding.bottom = resolve(cstyle.paddingBottom, ctx, 0, Y);
    style.padding.left = resolve(cstyle.paddingLeft, ctx, 0, X);

    style.marginInlineStart = resolve(cstyle.marginInlineStart, ctx, 0, X);
    style.marginInlineEnd = resolve(cstyle.marginInlineEnd, ctx, 0, X);

    byte flexBasisAxis = cstyle.flexDirection == FlexDirection.DEFAULT ? X : Y;
    style.flexBasis = resolve(cstyle.flexBasis, ctx, 0, flexBasisAxis);
    style.rowGap = resolve(cstyle.rowGap, ctx, 0, X);
    style.columnGap = resolve(cstyle.columnGap, ctx, 0, Y);
  }

  static float resolveFontSize(ValueOrAuto v) {
    if (v.isAuto()) {
      return 1.0f;
    }

    Primitive prim = v.primitive();
    Unit u = prim.getUnit();

    if (u == Unit.PERCENT) {
      return prim.getValue() * 0.01f;
    }

    return prim.getValue();
  }

  static float resolve(ValueOrAuto v, LayoutContext ctx, float auto, byte axis) {
    if (v.isAuto()) {
      return auto;
    }

    // Ts can suck my a**
    // https://www.w3.org/TR/css-sizing-3/#cyclic-percentage-contribution
    if (v.is(Unit.PERCENT)) {
      boolean definite = axis == X
          ? ctx.isWidthDefinite()
          : ctx.isHeightDefinite();

      if (!definite) {
        return auto;
      }
    }

    return resolvePrimitive(v.primitive(), ctx, axis);
  }

  public static float resolvePrimitive(Primitive prim, LayoutContext ctx, byte axis) {
    float percent = prim.getValue() * 0.01f;
    float value = prim.getValue();

    return switch (prim.getUnit()) {
      case PX -> value * CHAR_PX_SIZE_X;
      case CH -> value * LEN0_PX;
      case VW -> percent * ctx.screenSize.x;
      case VH -> percent * ctx.screenSize.y;
      case CM -> percent;

      case PERCENT -> {
        Vector2f parentSize = ctx.parentSizes.peek();

        if (axis == X) {
          yield parentSize.x * percent;
        } else {
          yield parentSize.y * percent;
        }
      }

      default -> value;
    };
  }

  public static void subtractExtraSpace(Vector2f out, LayoutStyle style) {
    Rect outline = style.outline;
    Rect border = style.border;
    Rect padding = style.padding;

    out.x -= outline.x() + border.x() + padding.x();
    out.y -= outline.y() + border.y() + padding.y();
  }

  private static float clampSize(float n, ValueOrAuto cmin, ValueOrAuto cmax, float min, float max) {
    if (!cmin.isAuto()) {
      n = Math.max(n, min);
    }
    if (!cmax.isAuto()) {
      n = Math.min(n, max);
    }
    return n;
  }

  private void clampBoxSize() {
    size.x = clampSize(size.x, cstyle.minWidth, cstyle.maxWidth, style.minSize.x, style.maxSize.x);
    size.y = clampSize(size.y, cstyle.minHeight, cstyle.maxHeight, style.minSize.y, style.maxSize.y);
  }

  public void reflow(LayoutContext ctx) {
    measureBox(ctx);
    layout();
  }

  public boolean measureBox(LayoutContext ctx) {
    transferComputedStyle(ctx);

    final float prex = size.x;
    final float prey = size.y;

    if (size.x == UNSET) {
      if (cstyle.width.isAuto()) {
        size.x = ctx.parentSizes.peek().x;
      } else {
        size.x = style.size.x;
        if (style.boxSizing == BoxSizing.CONTENT_BOX && !cstyle.width.is(Unit.PERCENT)) {
          size.x += getXBorder();
        }
      }
    } else if (!cstyle.width.isAuto()) {
      size.x = style.size.x;
      if (style.boxSizing == BoxSizing.CONTENT_BOX && !cstyle.width.is(Unit.PERCENT)) {
        size.x += getXBorder();
      }
    }

    if (size.y == UNSET) {
      if (cstyle.height.isAuto()) {
        size.y = ctx.parentSizes.peek().y;
      } else {
        size.y = style.size.y;
        if (style.boxSizing == BoxSizing.CONTENT_BOX && !cstyle.height.is(Unit.PERCENT)) {
          size.y += getYBorder();
        }
      }
    } else if (!cstyle.height.isAuto()) {
      size.y = style.size.y;
      if (style.boxSizing == BoxSizing.CONTENT_BOX && !cstyle.height.is(Unit.PERCENT)) {
        size.y += getYBorder();
      }
    }

    clampBoxSize();

    Vector2f innerSize = new Vector2f();
    getInnerSize(innerSize);

    boolean parentWidthDefinite = ctx.isWidthDefinite();
    boolean parentHeightDefinite = ctx.isWidthDefinite();

    ctx.parentSizes.push(innerSize);
    ctx.definiteWidths.push(cstyle.width.isPrimitive());
    ctx.definiteHeights.push(cstyle.height.isPrimitive());

    boolean first = true;
    Vector2f msize = new Vector2f();

    boolean useMeasuredWidth = cstyle.width.isAuto()
        || (cstyle.width.is(Unit.PERCENT) && !parentWidthDefinite);
    boolean useMeasuredHeight = cstyle.height.isAuto()
        || (cstyle.height.is(Unit.PERCENT) && !parentHeightDefinite);

    while (measure(ctx, msize) || first) {
      first = false;

      if (useMeasuredWidth) {
        size.x = msize.x + getXBorder();
      }
      if (useMeasuredHeight) {
        size.y = msize.y + getYBorder();
      }

      clampBoxSize();
      getInnerSize(innerSize);
    }

    ctx.parentSizes.pop();
    ctx.definiteWidths.popBoolean();
    ctx.definiteHeights.popBoolean();

    return prex != size.x || prey != size.y;
  }

  protected abstract boolean measure(LayoutContext ctx, Vector2f out);

  public void layout() {
    layoutSelf();
    layoutChildren();
  }

  protected abstract void layoutSelf();

  protected void layoutChildren() {
    for (LayoutNode node : nodes) {
      if (node instanceof LayoutBox box) {
        box.layout();
      }
    }
  }

  public void getContentStart(Vector2f out) {
    getContentStart(out, position, style);
  }

  public static void getContentStart(Vector2f out, Vector2f position, LayoutStyle style) {
    float leftOff = style.padding.left + style.border.left + style.outline.left;
    float topOff = style.padding.top + style.border.top + style.outline.top;

    out.x = position.x + leftOff;
    out.y = position.y - topOff;
  }

  public float getXBorder() {
    return style.padding.x() + style.outline.x() + style.border.x();
  }

  public float getYBorder() {
    return style.padding.y() + style.outline.y() + style.border.y();
  }

  public void getInnerSize(Vector2f out) {
    out.set(size);
    subtractExtraSpace(out, style);
  }

  protected boolean measure(LayoutNode node, LayoutContext ctx, Vector2f out) {
    if (node instanceof LayoutItem item) {
      item.measure(out);
      item.size.set(out);
      return false;
    }

    LayoutBox box = (LayoutBox) node;
    boolean changed = box.measureBox(ctx);
    out.set(box.size);

    return changed;
  }
}
