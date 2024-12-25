package net.arcadiusmc.delphirender.layout;

import static net.arcadiusmc.delphidom.Consts.CHAR_PX_SIZE;
import static net.arcadiusmc.delphidom.Consts.LEN0_PX;
import static net.arcadiusmc.delphirender.FullStyle.UNSET;
import static net.arcadiusmc.delphirender.FullStyle.toBukkitColor;
import static net.arcadiusmc.delphirender.FullStyle.toTextColor;

import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.chimera.ValueOrAuto;
import net.arcadiusmc.delphidom.Rect;
import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.RenderSystem;
import net.arcadiusmc.delphirender.content.ElementContent;
import net.arcadiusmc.delphirender.tree.ContentRenderElement;
import net.arcadiusmc.delphirender.tree.ElementRenderElement;
import net.arcadiusmc.delphirender.tree.RenderElement;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import org.joml.Vector2f;

public class NLayout {
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

  public static void reflow(RenderElement element) {
    pass1_intrinsicMeasurement(element);
    pass2_topdownMeasurement(element);
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

  /* ----------------- Pass 1: Measurement ----------------- */

  public static void pass1_intrinsicMeasurement(RenderElement element) {
    if (element instanceof ElementRenderElement el) {
      for (RenderElement child : el.getChildren()) {
        pass1_intrinsicMeasurement(child);
      }
    }

    Vector2f screenSize = element.getSystem().getScreen().getDimensions();

    FullStyle style = element.getStyle();
    ComputedStyleSet styleSet = element.getStyleSet();

    applyBasicStyle(style, styleSet);

    style.margin.left = resolveValue(styleSet.marginLeft, UNSET, screenSize);
    style.margin.top = resolveValue(styleSet.marginTop, UNSET, screenSize);
    style.margin.right = resolveValue(styleSet.marginRight, UNSET, screenSize);
    style.margin.bottom = resolveValue(styleSet.marginBottom, UNSET, screenSize);

    style.outline.left = resolveValue(styleSet.outlineLeft, 0f, screenSize);
    style.outline.top = resolveValue(styleSet.outlineTop, 0f, screenSize);
    style.outline.right = resolveValue(styleSet.outlineRight, 0f, screenSize);
    style.outline.bottom = resolveValue(styleSet.outlineBottom, 0f, screenSize);

    style.border.left = resolveValue(styleSet.borderLeft, 0f, screenSize);
    style.border.top = resolveValue(styleSet.borderTop, 0f, screenSize);
    style.border.right = resolveValue(styleSet.borderRight, 0f, screenSize);
    style.border.bottom = resolveValue(styleSet.borderBottom, 0f, screenSize);

    style.padding.left = resolveValue(styleSet.paddingLeft, 0f, screenSize);
    style.padding.top = resolveValue(styleSet.paddingTop, 0f, screenSize);
    style.padding.right = resolveValue(styleSet.paddingRight, 0f, screenSize);
    style.padding.bottom = resolveValue(styleSet.paddingBottom, 0f, screenSize);

    style.scale.x = resolveValue(styleSet.scaleX, 1.0f, screenSize);
    style.scale.y = resolveValue(styleSet.scaleY, 1.0f, screenSize);

    style.size.x = resolveValue(styleSet.width, UNSET, screenSize);
    style.size.y = resolveValue(styleSet.height, UNSET, screenSize);

    style.minSize.x = resolveValue(styleSet.minWidth, UNSET, screenSize);
    style.minSize.y = resolveValue(styleSet.minHeight, UNSET, screenSize);

    style.maxSize.x = resolveValue(styleSet.maxWidth, UNSET, screenSize);
    style.maxSize.y = resolveValue(styleSet.maxHeight, UNSET, screenSize);

    if (element instanceof ContentRenderElement content) {
      measureContent(content);
    }
  }

  private static void measureContent(ContentRenderElement element) {
    Vector2f out = new Vector2f();
    ElementContent content = element.getContent();
    FullStyle style = element.getStyle();

    if (content != null) {
      content.measureContent(out, style);
    }

    Rect r = new Rect().set(style.outline).max(0.0f);
    out.x += r.x();
    out.y += r.y();

    r.set(style.border).max(0.0f);
    out.x += r.x();
    out.y += r.y();

    r.set(style.padding).max(0.0f);
    out.x += r.x();
    out.y += r.y();

    element.size.set(out);
  }

  private static float resolvePrimitive(Primitive prim, float parentSize, Vector2f screenSize) {
    float percent = prim.getValue() * 100.0f;
    float value = prim.getValue();

    return switch (prim.getUnit()) {
      case NONE, M -> value;
      case PX -> value * CHAR_PX_SIZE;
      case CH -> value * LEN0_PX;
      case VW -> (percent) * screenSize.x;
      case VH -> (percent) * screenSize.y;
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

  private static float resolveValue(ValueOrAuto valueOrAuto, float auto, Vector2f screenSize) {
    return resolveValue(valueOrAuto, auto, UNSET, screenSize);
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
    RenderSystem system = element.getSystem();

    if (element.parent == null) {
      system.getScreen().getDimensions(parentSize);
    } else {
      parentSize.set(element.parent.size);
    }

    ComputedStyleSet styleSet = element.getStyleSet();
    FullStyle style = element.getStyle();



    if (element instanceof ElementRenderElement el) {
      for (RenderElement child : el.getChildren()) {
        pass2_topdownMeasurement(child);
      }
    }
  }

  public static boolean isPercent(ValueOrAuto auto) {
    if (auto.isAuto()) {
      return false;
    }
    return auto.primitive().getUnit() == Unit.PERCENT;
  }
}
