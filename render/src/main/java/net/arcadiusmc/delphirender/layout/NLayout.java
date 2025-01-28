package net.arcadiusmc.delphirender.layout;

import static net.arcadiusmc.delphidom.Consts.CHAR_PX_SIZE_X;
import static net.arcadiusmc.delphidom.Consts.GLOBAL_SCALAR;
import static net.arcadiusmc.delphidom.Consts.LEN0_PX;
import static net.arcadiusmc.delphirender.FullStyle.UNSET;
import static net.arcadiusmc.delphirender.FullStyle.toBukkitColor;
import static net.arcadiusmc.delphirender.FullStyle.toTextColor;

import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.chimera.ValueOrAuto;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.RenderSystem;
import net.arcadiusmc.delphirender.tree.ContentRenderElement;
import net.arcadiusmc.delphirender.tree.ElementRenderElement;
import net.arcadiusmc.delphirender.tree.RenderElement;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.joml.Vector2f;
import org.slf4j.Logger;

public class NLayout {
  private static final Logger LOGGER = Loggers.getLogger();

  /*
   * ==== LAYOUT PROCEDURE ====
   *
   * PASS 1: Intrinsic Measurement
   *   Measure every style property that can be measured (everything that
   *   isn't 'auto' or a percent value.), this also includes node content,
   *   if present.
   *   This pass is done bottom to top
   *
   * PASS 2: Resolve parent based sizes, if possible
   *   Iterate from root downwards, attempt to resolve all possible relative
   *   sizes (% unit values)
   *
   * PASS 3: Layout
   *   Iterate from the bottom up and apply the appropriate layout algorithms
   *   for each element. Skip content nodes, and nodes without child objects.
   *
   *   As this step is being executed, the sizes of each element will be set
   *   as well. This means, this will require an additional step after to
   *   finally resolve all %-based values. (I think)
   *
   *   Use the appropriate algorithm for each layout mode:
   *   - 'display: flex' - Flex layout
   *   - 'position: absolute' - Position Layout
   *   - Otherwise: Flow layout
   *
   *
   *   == FLOW ==
   *   Split elements into 'lines' of elements. Iterate over child elements.
   *   If element is 'display: inline' it is part of the current line.
   *   If the currentLineWidth + element.size.x + element.margin.x() is
   *   greater than maxWidth => line break.
   *   If 'display: inline-block' => line break after element.
   *   If 'position: absolute' => skip, do not include in line.
   *
   *   When iterating over elements, keep track of the largest margin
   *   separately. This is because all elements will sit on the same
   *   baseline, but the margin of each on the bottom can vary, and the
   *   largest margin becomes the line's bottom margin.
   *
   *   After all elements are split into lines, lay them out in the way
   *   specified by the lines. Include bottom margin when moving from 1 line
   *   to another.
   *
   *
   *   == POSITION ==
   *   Move current element to position specified by 'left', and 'top'
   *   properties, ignore all existing elements and do not count element in
   *   parent element's size measurement.
   *
   *
   *   == FLEX ==
   *   Create a vec2 mainDir, and a vec2 crossDir with values appropriate
   *   to the 'flex-direction' property. 'flex-direction: row | row-reverse'
   *   mean mainDir = (1, 0), crossDir = (0, 1), and 'column |
   *   column-reverse' means mainDir = (0, 1), crossDir = (1, 0)
   *
   * PASS 4: Post Processing
   *   This pass is performed in the same order as PASS 3: Layout. This step
   *   involves applying post layout operations like handling 'margin: auto'
   *   properties.
   *
   *
   *   == FLOW ==
   *   After Flow layout is conducted, measure element size (if width was not
   *   explicitly set) and re-iterate over 'display: block' elements to apply
   *   any 'auto' margin values. Left and right margins determine the x-axis
   *   layout of the element, if both are 'auto', then the
   *   x margins = (parentContentArea.x - elementSize.x) / 2.
   *   Otherwise, the auto margin = parentContentArea.x - elementSize.x
   *   - oppositeMargin. The other 2 margins determine the vertical
   *   layout of the element. For these calculations, we'll need a
   *   variable, N, which is equal to parentContentArea.y - usedSpace.y
   *   + elementSize.y.
   *   If both 'top' and 'bottom' are 'auto', then
   *   y margin = (N - elementSize.y) / 2
   *   Otherwise, the auto margin = N - elementSize.y.
   *
   * SOURCES / DOCS / TUTORIALS:
   *   https://www.quirksmode.org/css/flexbox-algorithm.html
   *   https://www.w3.org/TR/css-flexbox-1/
   *   https://tchayen.com/how-to-write-a-flexbox-layout-engine
   *   https://css-tricks.com/snippets/css/a-guide-to-flexbox/
   */

  public static void nlayout(RenderElement element) {
    measure(element);
    layout(element);
  }

  private static void layout(RenderElement element) {
    if (!(element instanceof ElementRenderElement el)) {
      return;
    }

    for (RenderElement child : el.getChildren()) {
      layout(child);
    }

    LayoutStyle s = getLayoutStyle(el);

    s.firstLayoutPass(el);
    s.secondLayoutPass(el);
  }

  private static boolean measure(RenderElement element) {
    FullStyle style = element.getStyle();
    ComputedStyleSet comp = element.getStyleSet();

    style.size.set(UNSET);
    element.size.set(UNSET);

    applyBasicStyle(style, comp);

    RenderSystem system = element.getSystem();
    Vector2f screenSize = new Vector2f();
    Vector2f parentSize = new Vector2f();
    Vector2f parentScale = new Vector2f(1);

    system.getScreen().getDimensions(screenSize);
    boolean canResolvePercents;

    if (element.parent == null) {
      parentSize.set(screenSize);
      canResolvePercents = true;
    } else {
      parentScale.set(element.parent.getStyle().scale);

      if (element.parent.getStyle().size.x != UNSET) {
        parentSize.set(element.parent.getStyle().size);
        canResolvePercents = true;
      } else {
        parentSize.set(screenSize);
        canResolvePercents = false;
      }
    }

    style.size.x = resolveValue(comp.width, UNSET, parentSize.x, screenSize);
    style.size.y = resolveValue(comp.height, UNSET, parentSize.y, screenSize);

    style.scale.x = resolveValue(comp.scaleX, 1.0f, parentScale.x, parentScale);
    style.scale.y = resolveValue(comp.scaleY, 1.0f, parentScale.y, parentScale);

    style.minSize.x = resolveValue(comp.minWidth, UNSET, parentSize.x, screenSize);
    style.minSize.y = resolveValue(comp.minHeight, UNSET, parentSize.y, screenSize);
    style.maxSize.x = resolveValue(comp.maxWidth, UNSET, parentSize.x, screenSize);
    style.maxSize.y = resolveValue(comp.maxHeight, UNSET, parentSize.y, screenSize);

    style.margin.top = resolveValue(comp.marginTop, 0, parentSize.y, screenSize);
    style.margin.right = resolveValue(comp.marginRight, 0, parentSize.x, screenSize);
    style.margin.bottom = resolveValue(comp.marginBottom, 0, parentSize.y, screenSize);
    style.margin.left = resolveValue(comp.marginLeft, 0, parentSize.x, screenSize);

    style.outline.top = resolveValue(comp.outlineTop, 0, parentSize.y, screenSize);
    style.outline.right = resolveValue(comp.outlineRight, 0, parentSize.x, screenSize);
    style.outline.bottom = resolveValue(comp.outlineBottom, 0, parentSize.y, screenSize);
    style.outline.left = resolveValue(comp.outlineLeft, 0, parentSize.x, screenSize);

    style.border.top = resolveValue(comp.borderTop, 0, parentSize.y, screenSize);
    style.border.right = resolveValue(comp.borderRight, 0, parentSize.x, screenSize);
    style.border.bottom = resolveValue(comp.borderBottom, 0, parentSize.y, screenSize);
    style.border.left = resolveValue(comp.borderLeft, 0, parentSize.x, screenSize);

    style.padding.top = resolveValue(comp.paddingTop, 0, parentSize.y, screenSize);
    style.padding.right = resolveValue(comp.paddingRight, 0, parentSize.x, screenSize);
    style.padding.bottom = resolveValue(comp.paddingBottom, 0, parentSize.y, screenSize);
    style.padding.left = resolveValue(comp.paddingLeft, 0, parentSize.x, screenSize);

    if (element instanceof ContentRenderElement ce) {
      Vector2f measuredContentSize = new Vector2f();
      ce.getContent().measureContent(measuredContentSize, style);
      measuredContentSize.mul(GLOBAL_SCALAR);
      measuredContentSize.mul(style.scale);

      if (style.size.x == UNSET) {
        style.size.x = measuredContentSize.x;
      }
      if (style.size.y == UNSET) {
        style.size.y = measuredContentSize.y;
      }
    } else if (element instanceof ElementRenderElement el) {
      boolean needsIteration = false;
      for (RenderElement child : el.getChildren()) {
        needsIteration |= !measure(child);
      }

      if (needsIteration) {
        LayoutStyle lStyle = getLayoutStyle(el);
        Vector2f measuredSize = new Vector2f();
        lStyle.measure(el, measuredSize);

        if (style.size.x == UNSET) {
          style.size.x = measuredSize.x;
        }
        if (style.size.y == UNSET) {
          style.size.y = measuredSize.y;
        }

        for (RenderElement child : el.getChildren()) {
          resolvePercents(child);
        }
      }
    }

    applyMeasuredSize(element, element.size);

    return canResolvePercents;
  }

  private static void resolvePercents(RenderElement element) {
    FullStyle style = element.getStyle();
    ComputedStyleSet comp = element.getStyleSet();

    Vector2f parentSize = new Vector2f();
    parentSize.set(element.getStyle().size);

    style.size.x = resolveIfPercent(comp.width, style.size.x, parentSize.x);
    style.size.y = resolveIfPercent(comp.height, style.size.y, parentSize.y);

    style.minSize.x = resolveIfPercent(comp.minWidth, style.minSize.x, parentSize.x);
    style.minSize.y = resolveIfPercent(comp.minHeight, style.minSize.y, parentSize.y);
    style.maxSize.x = resolveIfPercent(comp.maxWidth, style.maxSize.x, parentSize.x);
    style.maxSize.y = resolveIfPercent(comp.maxHeight, style.maxSize.y, parentSize.y);

    style.margin.top = resolveIfPercent(comp.marginTop, style.margin.top, parentSize.y);
    style.margin.right = resolveIfPercent(comp.marginRight, style.margin.right, parentSize.x);
    style.margin.bottom = resolveIfPercent(comp.marginBottom, style.margin.bottom, parentSize.y);
    style.margin.left = resolveIfPercent(comp.marginLeft, style.margin.left, parentSize.x);

    style.outline.top = resolveIfPercent(comp.outlineTop, style.outline.top, parentSize.y);
    style.outline.right = resolveIfPercent(comp.outlineRight, style.outline.right, parentSize.x);
    style.outline.bottom = resolveIfPercent(comp.outlineBottom, style.outline.bottom, parentSize.y);
    style.outline.left = resolveIfPercent(comp.outlineLeft, style.outline.left, parentSize.x);

    style.border.top = resolveIfPercent(comp.borderTop, style.border.top, parentSize.y);
    style.border.right = resolveIfPercent(comp.borderRight, style.border.right, parentSize.x);
    style.border.bottom = resolveIfPercent(comp.borderBottom, style.border.bottom, parentSize.y);
    style.border.left = resolveIfPercent(comp.borderLeft, style.border.left, parentSize.x);

    style.padding.top = resolveIfPercent(comp.paddingTop, style.padding.top, parentSize.y);
    style.padding.right = resolveIfPercent(comp.paddingRight, style.padding.right, parentSize.x);
    style.padding.bottom = resolveIfPercent(comp.paddingBottom, style.padding.bottom, parentSize.y);
    style.padding.left = resolveIfPercent(comp.paddingLeft, style.padding.left, parentSize.x);
  }

  private static float resolveIfPercent(ValueOrAuto v, float current, float parent) {
    if (v.isAuto()) {
      return current;
    }
    if (v.primitive().getUnit() != Unit.PERCENT) {
      return current;
    }
    return v.primitive().getValue() * 0.01f * parent;
  }

  private static float clamp(float v, float lower, float upper) {
    float min = lower == UNSET ? Float.MIN_VALUE : lower;
    float max = upper == UNSET ? Float.MAX_VALUE : upper;
    return Math.clamp(v, min, max);
  }

  private static void applyMeasuredSize(RenderElement element, Vector2f out) {
    FullStyle style = element.getStyle();
    Rect rect = new Rect();

    out.x = 0;
    out.y = 0;

    if (style.size.x != UNSET) {
      out.x = clamp(style.size.x, style.minSize.x, style.maxSize.x);
    }
    if (style.size.y != UNSET) {
      out.y = clamp(style.size.y, style.minSize.y, style.maxSize.y);
    }

    rect.set(style.outline).max(0.0f);
    out.x += rect.x();
    out.y += rect.y();

    rect.set(style.border).max(0.0f);
    out.x += rect.x();
    out.y += rect.y();

    rect.set(style.padding).max(0.0f);
    out.x += rect.x();
    out.y += rect.y();
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
  }

  private static LayoutStyle getLayoutStyle(ElementRenderElement ele) {
    return Layouts.FLOW;
  }

  private static float resolvePrimitive(Primitive prim, float parentSize, Vector2f screenSize) {
    float percent = prim.getValue() * 0.01f;
    float value = prim.getValue();

    return switch (prim.getUnit()) {
      case NONE, M -> value;
      case PX -> value * CHAR_PX_SIZE_X;
      case CH -> value * LEN0_PX;
      case VW -> percent * screenSize.x;
      case VH -> percent * screenSize.y;
      case CM -> percent;

      case PERCENT -> {
        if (parentSize == UNSET) {
          yield UNSET;
        }
        yield parentSize * value;
      }

      default -> UNSET;
    };
  }

  private static float resolveValue(
      ValueOrAuto valueOrAuto,
      float auto,
      float parentSize,
      Vector2f screenSize
  ) {
    if (valueOrAuto.isAuto()) {
      return auto;
    }

    Primitive prim = valueOrAuto.primitive();
    return resolvePrimitive(prim, parentSize, screenSize);
  }

  /* ----------------- Pass 2: Top down measurement ----------------- */

  public static void pass2_topdownMeasurement(RenderElement element) {
    Vector2f parentSize = new Vector2f();
    Vector2f screenSize = element.getScreen().getDimensions();
    RenderSystem system = element.getSystem();

    if (element.parent == null) {
      system.getScreen().getDimensions(parentSize);
    } else {
      parentSize.set(element.parent.size);
    }

    ComputedStyleSet styleSet = element.getStyleSet();
    FullStyle style = element.getStyle();

    style.margin.left = resolveValue(styleSet.marginLeft, UNSET, parentSize.x, screenSize);
    style.margin.top = resolveValue(styleSet.marginTop, UNSET, parentSize.y, screenSize);
    style.margin.right = resolveValue(styleSet.marginRight, UNSET, parentSize.x, screenSize);
    style.margin.bottom = resolveValue(styleSet.marginBottom, UNSET, parentSize.y, screenSize);

    style.outline.left = resolveValue(styleSet.outlineLeft, 0f, parentSize.x, screenSize);
    style.outline.top = resolveValue(styleSet.outlineTop, 0f, parentSize.y, screenSize);
    style.outline.right = resolveValue(styleSet.outlineRight, 0f, parentSize.x, screenSize);
    style.outline.bottom = resolveValue(styleSet.outlineBottom, 0f, parentSize.y, screenSize);

    style.border.left = resolveValue(styleSet.borderLeft, 0f, parentSize.x, screenSize);
    style.border.top = resolveValue(styleSet.borderTop, 0f, parentSize.y, screenSize);
    style.border.right = resolveValue(styleSet.borderRight, 0f, parentSize.x, screenSize);
    style.border.bottom = resolveValue(styleSet.borderBottom, 0f, parentSize.y, screenSize);

    style.padding.left = resolveValue(styleSet.paddingLeft, 0f, parentSize.x, screenSize);
    style.padding.top = resolveValue(styleSet.paddingTop, 0f, parentSize.y, screenSize);
    style.padding.right = resolveValue(styleSet.paddingRight, 0f, parentSize.x, screenSize);
    style.padding.bottom = resolveValue(styleSet.paddingBottom, 0f, parentSize.y, screenSize);

    style.scale.x = resolveValue(styleSet.scaleX, 1.0f, parentSize.x, screenSize);
    style.scale.y = resolveValue(styleSet.scaleY, 1.0f, parentSize.y, screenSize);

    style.size.x = resolveValue(styleSet.width, UNSET, parentSize.x, screenSize);
    style.size.y = resolveValue(styleSet.height, UNSET, parentSize.y, screenSize);

    style.minSize.x = resolveValue(styleSet.minWidth, UNSET, parentSize.x, screenSize);
    style.minSize.y = resolveValue(styleSet.minHeight, UNSET, parentSize.y, screenSize);

    style.maxSize.x = resolveValue(styleSet.maxWidth, UNSET, parentSize.x, screenSize);
    style.maxSize.y = resolveValue(styleSet.maxHeight, UNSET, parentSize.y, screenSize);

    if (element instanceof ElementRenderElement el) {
      for (RenderElement child : el.getChildren()) {
        pass2_topdownMeasurement(child);
      }
    }
  }

  /* ----------------- PASS 3: Layout ----------------- */

  public static void pass3_layout(RenderElement element) {
    if (!(element instanceof ElementRenderElement el)) {
      return;
    }

    for (RenderElement child : el.getChildren()) {
      pass3_layout(child);
    }

    LayoutStyle style = getLayoutStyle(el);
    style.firstLayoutPass(el);
    style.secondLayoutPass(el);
  }
}
