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
  
  protected void calculateSimpleStyle() {
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
  }
  
  protected void calculateComplexStyle(LayoutContext ctx) {
    style.size.x = resolve(cstyle.width, ctx, 0f, X);
    style.size.y = resolve(cstyle.height, ctx, 0f, Y);

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

  void applyMeasuredSize(Vector2f out) {
    out.x = 0;
    out.y = 0;

    ValueOrAuto width = cstyle.width;
    ValueOrAuto height = cstyle.height;

    getContentSize(out);

    Vector2f growth = new Vector2f();
    getBordersSizes(growth);

    BoxSizing sizing = style.boxSizing;

    if (width.isAuto() || sizing == BoxSizing.CONTENT_BOX) {
      out.x += growth.x;
    }
    if (height.isAuto() || sizing == BoxSizing.CONTENT_BOX) {
      out.y += growth.y;
    }
  }

  void getContentSize(Vector2f out) {
    if (style.size.x != UNSET) {
      out.x = clamp(style.size.x, style.minSize.x, style.maxSize.x);
    }
    if (style.size.y != UNSET) {
      out.y = clamp(style.size.y, style.minSize.y, style.maxSize.y);
    }
  }

  private static float clamp(float v, float lower, float upper) {
    float min = lower == UNSET ? Float.MIN_VALUE : lower;
    float max = upper == UNSET ? Float.MAX_VALUE : upper;

    if (v < min) {
      return min;
    }

    return Math.min(v, max);
  }

  void getBordersSizes(Vector2f out) {
    Rect rect = new Rect();

    rect.set(style.outline);
    out.x += rect.x();
    out.y += rect.y();

    rect.set(style.border);
    out.x += rect.x();
    out.y += rect.y();

    rect.set(style.padding);
    out.x += rect.x();
    out.y += rect.y();
  }

  public boolean reflow(LayoutContext ctx) {
    calculateSimpleStyle();
    calculateComplexStyle(ctx);

    Vector2f preSize = new Vector2f();
    preSize.set(this.size);

    Vector2f childObjectsSize = new Vector2f(0);
    boolean first = true;

    if (cstyle.width.isAuto()) {
      style.size.x = ctx.screenSize.x;
    }
    if (cstyle.height.isAuto()) {
      style.size.y = ctx.screenSize.y;
    }

    Vector2f psize = new Vector2f();
    applyMeasuredSize(psize);
    subtractExtraSpace(psize, style);

    ctx.parentSizes.push(psize);

    while (measure(ctx, childObjectsSize) || first) {
      first = false;

      if (cstyle.width.isAuto()) {
        style.size.x = childObjectsSize.x;
      }
      if (cstyle.height.isAuto()) {
        style.size.y = childObjectsSize.y;
      }

      applyMeasuredSize(this.size);

      psize.set(this.size);
      subtractExtraSpace(psize, style);
    }

    layout();

    ctx.parentSizes.pop();

    return size.x != preSize.x || size.y != preSize.y;
  }

  protected abstract boolean measure(LayoutContext ctx, Vector2f out);

  public abstract void layout();

  protected void layoutChildren() {
    for (LayoutNode node : nodes) {
      if (node instanceof LayoutBox box) {
        box.layout();
      }
    }
  }

  public void getContentStart(Vector2f out) {
    float leftOff = style.padding.left + style.border.left + style.outline.left;
    float topOff = style.padding.top + style.border.top + style.outline.top;

    out.x = position.x + leftOff;
    out.y = position.y - topOff;
  }

  public void getInnerSize(Vector2f out) {
    out.set(size);
    subtractExtraSpace(out, style);
  }

  protected boolean measure(LayoutNode node, LayoutContext ctx, Vector2f out) {
    if (node instanceof LayoutItem item) {
      item.measure(out);
      return false;
    }

    LayoutBox box = (LayoutBox) node;
    boolean changed = box.reflow(ctx);
    out.set(box.size);

    return changed;
  }
}
