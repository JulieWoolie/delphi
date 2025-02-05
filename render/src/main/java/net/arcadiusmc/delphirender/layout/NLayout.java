package net.arcadiusmc.delphirender.layout;

import static net.arcadiusmc.delphidom.Consts.CHAR_PX_SIZE_X;
import static net.arcadiusmc.delphidom.Consts.CHAR_PX_SIZE_Y;
import static net.arcadiusmc.delphidom.Consts.ITEM_SPRITE_SIZE;
import static net.arcadiusmc.delphidom.Consts.LEN0_PX;
import static net.arcadiusmc.delphirender.FullStyle.UNSET;
import static net.arcadiusmc.delphirender.FullStyle.toBukkitColor;
import static net.arcadiusmc.delphirender.FullStyle.toTextColor;
import static net.arcadiusmc.delphirender.object.RenderObject.GLOBAL_SCALAR;

import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.chimera.ValueOrAuto;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphirender.FontMeasureCallback;
import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.MetricTextMeasure;
import net.arcadiusmc.delphirender.SimpleTextMeasure;
import net.arcadiusmc.delphirender.TextMeasure;
import net.arcadiusmc.delphirender.TextUtil;
import net.arcadiusmc.delphirender.object.BoxRenderObject;
import net.arcadiusmc.delphirender.object.ElementRenderObject;
import net.arcadiusmc.delphirender.object.ItemRenderObject;
import net.arcadiusmc.delphirender.object.RenderObject;
import net.arcadiusmc.delphirender.object.TextRenderObject;
import net.arcadiusmc.dom.style.BoxSizing;
import net.arcadiusmc.dom.style.Primitive;
import net.kyori.adventure.text.Component;
import org.joml.Vector2f;

public class NLayout {

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
  }

  private static boolean measureElement(ElementRenderObject ro, MeasureContext ctx, Vector2f out) {
    ComputedStyleSet comp = ro.computedStyleSet;
    FullStyle style = ro.style;

    applyBasicStyle(style, comp);

    Vector2f preSize = new Vector2f();
    preSize.set(ro.size);

    style.size.x = resolve(comp.width, ctx, 0f, X);
    style.size.y = resolve(comp.height, ctx, 0f, Y);

    style.scale.x = resolve(comp.scaleX, ctx, 1f, X);
    style.scale.y = resolve(comp.scaleY, ctx, 1f, Y);

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
    }

    ctx.parentSizes.pop();

    return ro.size.x != preSize.x || ro.size.y != preSize.y;
  }

  static boolean measure(RenderObject object, MeasureContext ctx, Vector2f out) {
    switch (object) {
      case BoxRenderObject box -> {
        out.set(box.size);
        return false;
      }
      case TextRenderObject txt -> {
        measureText(txt, txt.text(), out);
        applyScalars(txt, out);
        return false;
      }
      case ItemRenderObject it -> {
        out.set(ITEM_SPRITE_SIZE);
        out.mul(GLOBAL_SCALAR);

        ElementRenderObject parent = object.parent;
        if (parent != null) {
          out.mul(parent.style.scale);
        }

        return false;
      }
      case ElementRenderObject el -> {
        return measureElement(el, ctx, out);
      }
      default -> throw new IllegalStateException();
    }
  }

  public static void measureText(
      TextRenderObject obj,
      Component text,
      Vector2f out
  ) {
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

  static void applyScalars(RenderObject obj, Vector2f vec) {
    vec.x *= GLOBAL_SCALAR;
    vec.y *= GLOBAL_SCALAR;

    ElementRenderObject parent = obj.parent;
    if (parent != null) {
      vec.mul(parent.style.scale);
    }
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
    return Math.clamp(v, min, max);
  }

  private static void applyMeasuredSize(ElementRenderObject element, Vector2f out) {
    FullStyle style = element.style;
    Rect rect = new Rect();

    out.x = 0;
    out.y = 0;

    if (style.size.x != UNSET) {
      out.x = clamp(style.size.x, style.minSize.x, style.maxSize.x);
    }
    if (style.size.y != UNSET) {
      out.y = clamp(style.size.y, style.minSize.y, style.maxSize.y);
    }

    if (!element.computedStyleSet.width.isAuto()) {
      out.x *= GLOBAL_SCALAR * style.scale.x;
    }
    if (!element.computedStyleSet.height.isAuto()) {
      out.y *= GLOBAL_SCALAR * style.scale.y;
    }

    float xGrow = 0.0f;
    float yGrow = 0.0f;

    rect.set(style.outline).max(0.0f);
    xGrow += rect.x();
    yGrow += rect.y();

    rect.set(style.border).max(0.0f);
    xGrow += rect.x();
    yGrow += rect.y();

    rect.set(style.padding).max(0.0f);
    xGrow += rect.x();
    yGrow += rect.y();

    xGrow *= GLOBAL_SCALAR * style.scale.x;
    yGrow *= GLOBAL_SCALAR * style.scale.y;

    out.x += xGrow;
    out.y += yGrow;
  }

  static LayoutAlgorithm getLayoutAlgo(ElementRenderObject obj) {
    return Algorithms.FLOW;
  }

  static void getBoxSizingOffset(ElementRenderObject obj, Vector2f out) {
    BoxSizing boxSizing = obj.style.boxSizing;
    if (boxSizing == BoxSizing.BORDER_BOX) {
      out.set(0);
      return;
    }

    Rect rect = new Rect(0);

    rect.set(obj.style.outline).max(0.0f);
    out.x += rect.x();
    out.y += rect.y();

    rect.set(obj.style.border).max(0f);
    out.x += rect.x();
    out.y += rect.y();

    rect.set(obj.style.padding).max(0f);
    out.x += rect.x();
    out.y += rect.y();

    out.mul(GLOBAL_SCALAR);
    out.mul(obj.style.scale);
  }
}
