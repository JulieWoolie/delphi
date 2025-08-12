package com.juliewoolie.delphirender.layout;

import static com.juliewoolie.delphirender.Consts.CHAR_PX_SIZE_X;
import static com.juliewoolie.delphirender.Consts.CHAR_PX_SIZE_Y;
import static com.juliewoolie.delphirender.Consts.ITEM_SPRITE_SIZE;
import static com.juliewoolie.delphirender.Consts.LEN0_PX;
import static com.juliewoolie.delphirender.FullStyle.UNSET;
import static com.juliewoolie.delphirender.FullStyle.toBukkitColor;
import static com.juliewoolie.delphirender.FullStyle.toTextColor;

import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.ValueOrAuto;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.delphidom.Rect;
import com.juliewoolie.delphirender.FontMeasureCallback;
import com.juliewoolie.delphirender.FullStyle;
import com.juliewoolie.delphirender.MetricTextMeasure;
import com.juliewoolie.delphirender.SimpleTextMeasure;
import com.juliewoolie.delphirender.TextMeasure;
import com.juliewoolie.delphirender.TextUtil;
import com.juliewoolie.delphirender.object.BoxRenderObject;
import com.juliewoolie.delphirender.object.CanvasRenderObject;
import com.juliewoolie.delphirender.object.ElementRenderObject;
import com.juliewoolie.delphirender.object.ItemRenderObject;
import com.juliewoolie.delphirender.object.RenderObject;
import com.juliewoolie.delphirender.object.TextRenderObject;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.DisplayType;
import com.juliewoolie.dom.style.FlexDirection;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import net.kyori.adventure.text.Component;
import org.joml.Vector2f;
import org.slf4j.Logger;

public class NLayout {

  private static final Logger LOGGER = Loggers.getLogger();

  public static final float GLOBAL_FONT_SIZE = 0.5f;

  static final byte X = 0;
  static final byte Y = 1;

  public static void layout(ElementRenderObject ro) {
    measure(ro);
    performLayout(ro);
  }

  public static void performLayout(ElementRenderObject ro) {
    LayoutAlgorithm algo = getLayoutAlgo(ro);
    algo.layout(ro);

    for (RenderObject childObject : ro.getChildObjects()) {
      if (!(childObject instanceof ElementRenderObject el)) {
        continue;
      }

      performLayout(el);
    }
  }

  public static void measure(ElementRenderObject obj) {
    MeasureContext ctx = new MeasureContext(obj.system, obj.screen);
    ctx.screen.getDimensions(ctx.screenSize);
    ctx.parentSizes.push(ctx.screenSize);
    measureElement(obj, ctx, obj.size);
  }

  public static void applyBasicStyle(FullStyle style, ComputedStyleSet styleSet) {
    style.textColor = toTextColor(styleSet.color);
    style.backgroundColor = toBukkitColor(styleSet.backgroundColor);
    style.borderColor = toBukkitColor(styleSet.borderColor);
    style.outlineColor = toBukkitColor(styleSet.outlineColor);

    style.textShadowed = styleSet.textShadow;
    style.bold = styleSet.bold;
    style.italic = styleSet.italic;
    style.underlined = styleSet.underlined;
    style.strikethrough = styleSet.strikethrough;
    style.obfuscated = styleSet.obfuscated;

    style.display = styleSet.display;

    style.zindex = styleSet.zindex;
    style.alignItems = styleSet.alignItems;
    style.flexDirection = styleSet.flexDirection;
    style.flexWrap = styleSet.flexWrap;
    style.justify = styleSet.justifyContent;
    style.order = styleSet.order;
    style.boxSizing = styleSet.boxSizing;
    style.visibility = styleSet.visibility;
  }

  public static void applyComplexStyle(FullStyle style, ComputedStyleSet comp, MeasureContext ctx) {
    style.size.x = resolve(comp.width, ctx, 0f, X);
    style.size.y = resolve(comp.height, ctx, 0f, Y);

    style.fontSize = resolveFontSize(comp.fontSize);

    style.minSize.x = resolve(comp.minWidth, ctx, UNSET, X);
    style.minSize.y = resolve(comp.minHeight, ctx, UNSET, Y);
    style.maxSize.x = resolve(comp.maxWidth, ctx, UNSET, X);
    style.maxSize.y = resolve(comp.maxHeight, ctx, UNSET, Y);

    style.margin.top = resolve(comp.marginTop, ctx, 0, Y);
    style.margin.right = resolve(comp.marginRight, ctx, 0, X);
    style.margin.bottom = resolve(comp.marginBottom, ctx, 0, Y);
    style.margin.left = resolve(comp.marginLeft, ctx, 0, X);

    style.outline.top = resolve(comp.outlineTop, ctx, 0, Y);
    style.outline.right = resolve(comp.outlineRight, ctx, 0, X);
    style.outline.bottom = resolve(comp.outlineBottom, ctx, 0, Y);
    style.outline.left = resolve(comp.outlineLeft, ctx, 0, X);

    style.border.top = resolve(comp.borderTop, ctx, 0, Y);
    style.border.right = resolve(comp.borderRight, ctx, 0, X);
    style.border.bottom = resolve(comp.borderBottom, ctx, 0, Y);
    style.border.left = resolve(comp.borderLeft, ctx, 0, X);

    style.padding.top = resolve(comp.paddingTop, ctx, 0, Y);
    style.padding.right = resolve(comp.paddingRight, ctx, 0, X);
    style.padding.bottom = resolve(comp.paddingBottom, ctx, 0, Y);
    style.padding.left = resolve(comp.paddingLeft, ctx, 0, X);

    style.marginInlineStart = resolve(comp.marginInlineStart, ctx, 0, X);
    style.marginInlineEnd = resolve(comp.marginInlineEnd, ctx, 0, X);

    byte flexBasisAxis = comp.flexDirection == FlexDirection.DEFAULT ? X : Y;
    style.flexBasis = resolve(comp.flexBasis, ctx, 0, flexBasisAxis);
    style.gap = resolve(comp.gap, ctx, 0, flexBasisAxis);
  }

  private static boolean measureElement(ElementRenderObject ro, MeasureContext ctx, Vector2f out) {
    ComputedStyleSet comp = ro.computedStyleSet;
    FullStyle style = ro.style;

    applyBasicStyle(style, comp);
    applyComplexStyle(style, comp, ctx);

    Vector2f preSize = new Vector2f();
    preSize.set(ro.size);

    Vector2f childObjectsSize = new Vector2f(0);
    LayoutAlgorithm lStyle = getLayoutAlgo(ro);

    boolean first = true;

    if (comp.width.isAuto()) {
      style.size.x = ctx.screenSize.x;
    }
    if (comp.height.isAuto()){
      style.size.y = ctx.screenSize.y;
    }

    Vector2f psize = new Vector2f();
    applyMeasuredSize(ro, psize);
    subtractExtraSpace(style, psize);

    ctx.parentSizes.push(psize);

    while (lStyle.measure(ro, ctx, childObjectsSize) || first) {
      first = false;

      if (comp.width.isAuto()) {
        style.size.x = childObjectsSize.x;
      }
      if (comp.height.isAuto()) {
        style.size.y = childObjectsSize.y;
      }

      applyMeasuredSize(ro, out);

      psize.set(out);
      subtractExtraSpace(style, psize);
    }

    ctx.parentSizes.pop();

    return ro.size.x != preSize.x || ro.size.y != preSize.y;
  }

  public static void subtractExtraSpace(FullStyle style, Vector2f out) {
    Rect outline = style.outline;
    Rect border = style.border;
    Rect padding = style.padding;

    out.x -= outline.x() + border.x() + padding.x();
    out.y -= outline.y() + border.y() + padding.y();
  }

  static boolean measure(RenderObject object, MeasureContext ctx, Vector2f out) {
    switch (object) {
      case BoxRenderObject box -> {
        out.set(box.size);
        return false;
      }
      case TextRenderObject txt -> {
        measureText(txt, txt.text(), out);

        if (txt.parent != null) {
          out.mul(GLOBAL_FONT_SIZE).mul(txt.parent.style.fontSize);
        }

        return false;
      }
      case ItemRenderObject it -> {
        out.set(ITEM_SPRITE_SIZE);
        return false;
      }
      case ElementRenderObject el -> {
        return measureElement(el, ctx, out);
      }
      case CanvasRenderObject canvas -> {
        out.x = canvas.canvas.getWidth() * CHAR_PX_SIZE_X;
        out.y = canvas.canvas.getHeight() * CHAR_PX_SIZE_Y;
        return false;
      }
      default -> throw new IllegalStateException();
    }
  }

  public static void measureText(TextRenderObject obj, Component text, Vector2f out) {
    TextMeasure measure;
    FontMeasureCallback metrics = obj.system.getFontMetrics();

    if (metrics == null) {
      measure = new SimpleTextMeasure();
    } else {
      measure = new MetricTextMeasure(metrics);
    }

    TextUtil.FLATTENER.flatten(text, measure);

    measure.outputSize(out);

    out.x *= CHAR_PX_SIZE_X;
    out.y *= CHAR_PX_SIZE_Y;
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

  static float resolve(ValueOrAuto v, MeasureContext ctx, float auto, byte axis) {
    if (v.isAuto()) {
      return auto;
    }
    return resolvePrimitive(v.primitive(), ctx, axis);
  }

  private static float resolvePrimitive(Primitive prim, MeasureContext ctx, byte axis) {
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

  private static float clamp(float v, float lower, float upper) {
    float min = lower == UNSET ? Float.MIN_VALUE : lower;
    float max = upper == UNSET ? Float.MAX_VALUE : upper;

    if (v < min) {
      return min;
    }
    if (v > max) {
      return max;
    }

    return v;
  }

  private static void applyMeasuredSize(ElementRenderObject element, Vector2f out) {
    FullStyle style = element.style;

    out.x = 0;
    out.y = 0;

    ComputedStyleSet comp = element.computedStyleSet;
    ValueOrAuto width = comp.width;
    ValueOrAuto height = comp.height;

    getContentSize(out, style);

    Vector2f growth = new Vector2f();
    getBordersSizes(style, growth);

    BoxSizing sizing = element.style.boxSizing;

    if (width.isAuto() || sizing == BoxSizing.CONTENT_BOX) {
      out.x += growth.x;
    }
    if (height.isAuto() || sizing == BoxSizing.CONTENT_BOX) {
      out.y += growth.y;
    }
  }

  static void getContentSize(Vector2f out, FullStyle style) {
    if (style.size.x != UNSET) {
      out.x = clamp(style.size.x, style.minSize.x, style.maxSize.x);
    }
    if (style.size.y != UNSET) {
      out.y = clamp(style.size.y, style.minSize.y, style.maxSize.y);
    }
  }

  static void getBordersSizes(FullStyle style, Vector2f out) {
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

  static LayoutAlgorithm getLayoutAlgo(ElementRenderObject obj) {
    if (obj.style.display == DisplayType.FLEX) {
      return Algorithms.FLEX;
    }

    return Algorithms.FLOW;
  }
}
