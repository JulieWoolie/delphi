package net.arcadiusmc.chimera;

import static net.arcadiusmc.chimera.Properties.ALIGN_ITEMS;
import static net.arcadiusmc.chimera.Properties.BACKGROUND_COLOR;
import static net.arcadiusmc.chimera.Properties.BOLD;
import static net.arcadiusmc.chimera.Properties.BORDER;
import static net.arcadiusmc.chimera.Properties.BORDER_BOTTOM;
import static net.arcadiusmc.chimera.Properties.BORDER_COLOR;
import static net.arcadiusmc.chimera.Properties.BORDER_LEFT;
import static net.arcadiusmc.chimera.Properties.BORDER_RIGHT;
import static net.arcadiusmc.chimera.Properties.BORDER_TOP;
import static net.arcadiusmc.chimera.Properties.BOX_SIZING;
import static net.arcadiusmc.chimera.Properties.COLOR;
import static net.arcadiusmc.chimera.Properties.DISPLAY;
import static net.arcadiusmc.chimera.Properties.FLEX_DIRECTION;
import static net.arcadiusmc.chimera.Properties.FLEX_WRAP;
import static net.arcadiusmc.chimera.Properties.HEIGHT;
import static net.arcadiusmc.chimera.Properties.ITALIC;
import static net.arcadiusmc.chimera.Properties.JUSTIFY_CONTENT;
import static net.arcadiusmc.chimera.Properties.MARGIN;
import static net.arcadiusmc.chimera.Properties.MARGIN_BOTTOM;
import static net.arcadiusmc.chimera.Properties.MARGIN_INLINE;
import static net.arcadiusmc.chimera.Properties.MARGIN_INLINE_END;
import static net.arcadiusmc.chimera.Properties.MARGIN_INLINE_START;
import static net.arcadiusmc.chimera.Properties.MARGIN_LEFT;
import static net.arcadiusmc.chimera.Properties.MARGIN_RIGHT;
import static net.arcadiusmc.chimera.Properties.MARGIN_TOP;
import static net.arcadiusmc.chimera.Properties.MAX_HEIGHT;
import static net.arcadiusmc.chimera.Properties.MAX_WIDTH;
import static net.arcadiusmc.chimera.Properties.MIN_HEIGHT;
import static net.arcadiusmc.chimera.Properties.MIN_WIDTH;
import static net.arcadiusmc.chimera.Properties.OBFUSCATED;
import static net.arcadiusmc.chimera.Properties.ORDER;
import static net.arcadiusmc.chimera.Properties.OUTLINE;
import static net.arcadiusmc.chimera.Properties.OUTLINE_BOTTOM;
import static net.arcadiusmc.chimera.Properties.OUTLINE_COLOR;
import static net.arcadiusmc.chimera.Properties.OUTLINE_LEFT;
import static net.arcadiusmc.chimera.Properties.OUTLINE_RIGHT;
import static net.arcadiusmc.chimera.Properties.OUTLINE_TOP;
import static net.arcadiusmc.chimera.Properties.PADDING;
import static net.arcadiusmc.chimera.Properties.PADDING_BOTTOM;
import static net.arcadiusmc.chimera.Properties.PADDING_LEFT;
import static net.arcadiusmc.chimera.Properties.PADDING_RIGHT;
import static net.arcadiusmc.chimera.Properties.PADDING_TOP;
import static net.arcadiusmc.chimera.Properties.SCALE;
import static net.arcadiusmc.chimera.Properties.STRIKETHROUGH;
import static net.arcadiusmc.chimera.Properties.TEXT_SHADOW;
import static net.arcadiusmc.chimera.Properties.UNDERLINED;
import static net.arcadiusmc.chimera.Properties.WIDTH;
import static net.arcadiusmc.chimera.Properties.Z_INDEX;

import lombok.ToString;
import net.arcadiusmc.chimera.Value.ValueType;
import net.arcadiusmc.dom.style.AlignItems;
import net.arcadiusmc.dom.style.BoxSizing;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.FlexDirection;
import net.arcadiusmc.dom.style.FlexWrap;
import net.arcadiusmc.dom.style.JustifyContent;
import net.arcadiusmc.dom.style.Primitive;

@ToString
public class ComputedStyleSet {

  public Color color;
  public Color backgroundColor;
  public Color borderColor;
  public Color outlineColor;

  public boolean textShadow;
  public boolean bold;
  public boolean italic;
  public boolean underlined;
  public boolean strikethrough;
  public boolean obfuscated;

  public DisplayType display;

  public ValueOrAuto scaleX;
  public ValueOrAuto scaleY;

  public ValueOrAuto width;
  public ValueOrAuto height;

  public ValueOrAuto minWidth;
  public ValueOrAuto minHeight;
  public ValueOrAuto maxWidth;
  public ValueOrAuto maxHeight;

  public ValueOrAuto paddingTop;
  public ValueOrAuto paddingRight;
  public ValueOrAuto paddingBottom;
  public ValueOrAuto paddingLeft;

  public ValueOrAuto borderTop;
  public ValueOrAuto borderRight;
  public ValueOrAuto borderBottom;
  public ValueOrAuto borderLeft;

  public ValueOrAuto outlineTop;
  public ValueOrAuto outlineRight;
  public ValueOrAuto outlineBottom;
  public ValueOrAuto outlineLeft;

  public ValueOrAuto marginTop;
  public ValueOrAuto marginRight;
  public ValueOrAuto marginBottom;
  public ValueOrAuto marginLeft;

  public ValueOrAuto marginInlineStart;
  public ValueOrAuto marginInlineEnd;

  public int zindex;
  public AlignItems alignItems;
  public FlexDirection flexDirection;
  public FlexWrap flexWrap;
  public JustifyContent justifyContent;
  public int order;
  public BoxSizing boxSizing;

  public ComputedStyleSet() {
    clear();
  }

  public void clear() {
    color = COLOR.getDefaultValue();
    backgroundColor = BACKGROUND_COLOR.getDefaultValue();
    outlineColor = OUTLINE_COLOR.getDefaultValue();
    borderColor = BORDER_COLOR.getDefaultValue();

    textShadow = TEXT_SHADOW.getDefaultValue();
    bold = BOLD.getDefaultValue();
    italic = ITALIC.getDefaultValue();
    strikethrough = STRIKETHROUGH.getDefaultValue();
    underlined = UNDERLINED.getDefaultValue();
    obfuscated = OBFUSCATED.getDefaultValue();

    display = DisplayType.DEFAULT;

    scaleX = ValueOrAuto.ONE;
    scaleY = ValueOrAuto.ONE;

    width = ValueOrAuto.AUTO;
    height = ValueOrAuto.AUTO;
    minWidth = ValueOrAuto.AUTO;
    minHeight = ValueOrAuto.AUTO;
    maxWidth = ValueOrAuto.AUTO;
    maxHeight = ValueOrAuto.AUTO;

    paddingTop = ValueOrAuto.ZERO;
    paddingRight = ValueOrAuto.ZERO;
    paddingBottom = ValueOrAuto.ZERO;
    paddingLeft = ValueOrAuto.ZERO;

    borderTop = ValueOrAuto.ZERO;
    borderRight = ValueOrAuto.ZERO;
    borderBottom = ValueOrAuto.ZERO;
    borderLeft = ValueOrAuto.ZERO;

    outlineTop = ValueOrAuto.ZERO;
    outlineRight = ValueOrAuto.ZERO;
    outlineBottom = ValueOrAuto.ZERO;
    outlineLeft = ValueOrAuto.ZERO;

    marginTop = ValueOrAuto.ZERO;
    marginRight = ValueOrAuto.ZERO;
    marginBottom = ValueOrAuto.ZERO;
    marginLeft = ValueOrAuto.ZERO;

    marginInlineStart = ValueOrAuto.ZERO;
    marginInlineEnd = ValueOrAuto.ZERO;

    zindex = 0;
    alignItems = AlignItems.DEFAULT;
    flexDirection = FlexDirection.DEFAULT;
    flexWrap = FlexWrap.DEFAULT;
    justifyContent = JustifyContent.DEFAULT;
    order = 0;
    boxSizing = BoxSizing.CONTENT_BOX;
  }
  
  public void putAll(PropertySet set) {
    color = getExplicit(set, COLOR);
    backgroundColor = getExplicit(set, BACKGROUND_COLOR);
    borderColor = getExplicit(set, BORDER_COLOR);
    outlineColor = getExplicit(set, OUTLINE_COLOR);

    textShadow = getExplicit(set, TEXT_SHADOW);
    bold = getExplicit(set, BOLD);
    italic = getExplicit(set, ITALIC);
    strikethrough = getExplicit(set, STRIKETHROUGH);
    obfuscated = getExplicit(set, OBFUSCATED);
    underlined = getExplicit(set, UNDERLINED);

    display = getExplicit(set, DISPLAY);

    ValueOrAuto scale = getPrimitive(set, SCALE);
    scaleX = scale;
    scaleY = scale;

    width = getPrimitive(set, WIDTH);
    height = getPrimitive(set, HEIGHT);
    minWidth = getPrimitive(set, MIN_WIDTH);
    minHeight = getPrimitive(set, MIN_HEIGHT);
    maxWidth = getPrimitive(set, MAX_WIDTH);
    maxHeight = getPrimitive(set, MAX_HEIGHT);

    zindex = getExplicit(set, Z_INDEX);
    alignItems = getExplicit(set, ALIGN_ITEMS);
    flexDirection = getExplicit(set, FLEX_DIRECTION);
    flexWrap = getExplicit(set, FLEX_WRAP);
    justifyContent = getExplicit(set, JUSTIFY_CONTENT);
    order = getExplicit(set, ORDER);
    boxSizing = getExplicit(set, BOX_SIZING);

    // Margin inline
    PrimitiveLeftRight lr = getExplicit(set, MARGIN_INLINE);
    marginInlineStart = ValueOrAuto.valueOf(lr.getLeft());
    marginInlineEnd = ValueOrAuto.valueOf(lr.getRight());
    if (set.has(MARGIN_INLINE_START)) {
      marginInlineStart = getPrimitive(set, MARGIN_INLINE_START);
    }
    if (set.has(MARGIN_INLINE_END)) {
      marginInlineStart = getPrimitive(set, MARGIN_INLINE_END);
    }

    // Padding
    PrimitiveRect rect = getExplicit(set, PADDING);
    paddingTop = ValueOrAuto.valueOf(rect.getTop());
    paddingRight = ValueOrAuto.valueOf(rect.getRight());
    paddingBottom = ValueOrAuto.valueOf(rect.getBottom());
    paddingLeft = ValueOrAuto.valueOf(rect.getLeft());

    if (set.has(PADDING_TOP)) {
      paddingTop = getPrimitive(set, PADDING_TOP);
    }
    if (set.has(PADDING_RIGHT)) {
      paddingRight = getPrimitive(set, PADDING_RIGHT);
    }
    if (set.has(PADDING_BOTTOM)) {
      paddingBottom = getPrimitive(set, PADDING_BOTTOM);
    }
    if (set.has(PADDING_LEFT)) {
      paddingLeft = getPrimitive(set, PADDING_LEFT);
    }

    // Outline
    rect = getExplicit(set, OUTLINE);
    outlineTop = ValueOrAuto.valueOf(rect.getTop());
    outlineRight = ValueOrAuto.valueOf(rect.getRight());
    outlineBottom = ValueOrAuto.valueOf(rect.getBottom());
    outlineLeft = ValueOrAuto.valueOf(rect.getLeft());

    if (set.has(OUTLINE_TOP)) {
      outlineTop = getPrimitive(set, OUTLINE_TOP);
    }
    if (set.has(OUTLINE_RIGHT)) {
      outlineRight = getPrimitive(set, OUTLINE_RIGHT);
    }
    if (set.has(OUTLINE_BOTTOM)) {
      outlineBottom = getPrimitive(set, OUTLINE_BOTTOM);
    }
    if (set.has(OUTLINE_LEFT)) {
      outlineLeft = getPrimitive(set, OUTLINE_LEFT);
    }

    // Border
    rect = getExplicit(set, BORDER);
    borderTop = ValueOrAuto.valueOf(rect.getTop());
    borderRight = ValueOrAuto.valueOf(rect.getRight());
    borderBottom = ValueOrAuto.valueOf(rect.getBottom());
    borderLeft = ValueOrAuto.valueOf(rect.getLeft());

    if (set.has(BORDER_TOP)) {
      borderTop = getPrimitive(set, BORDER_TOP);
    }
    if (set.has(BORDER_RIGHT)) {
      borderRight = getPrimitive(set, BORDER_RIGHT);
    }
    if (set.has(BORDER_BOTTOM)) {
      borderBottom = getPrimitive(set, BORDER_BOTTOM);
    }
    if (set.has(BORDER_LEFT)) {
      borderLeft = getPrimitive(set, BORDER_LEFT);
    }

    // Margin
    rect = getExplicit(set, MARGIN);
    marginTop = ValueOrAuto.valueOf(rect.getTop());
    marginRight = ValueOrAuto.valueOf(rect.getRight());
    marginBottom = ValueOrAuto.valueOf(rect.getBottom());
    marginLeft = ValueOrAuto.valueOf(rect.getLeft());

    if (set.has(MARGIN_TOP)) {
      marginTop = getPrimitive(set, MARGIN_TOP);
    }
    if (set.has(MARGIN_RIGHT)) {
      marginRight = getPrimitive(set, MARGIN_RIGHT);
    }
    if (set.has(MARGIN_BOTTOM)) {
      marginBottom = getPrimitive(set, MARGIN_BOTTOM);
    }
    if (set.has(MARGIN_LEFT)) {
      marginLeft = getPrimitive(set, MARGIN_LEFT);
    }
  }

  private static ValueOrAuto getPrimitive(PropertySet set, Property<Primitive> property) {
    Value<Primitive> v = set.orNull(property);

    if (v == null) {
      return ValueOrAuto.AUTO;
    }

    if (v.getType() == ValueType.AUTO) {
      return ValueOrAuto.AUTO;
    }
    if (v.getType() != ValueType.EXPLICIT) {
      return ValueOrAuto.valueOf(property.getDefaultValue());
    }

    return ValueOrAuto.valueOf(v.getValue());
  }

  private static <T> T getExplicit(PropertySet set, Property<T> property) {
    Value<T> value = set.orNull(property);
    if (value == null) {
      return property.getDefaultValue();
    }

    if (value.getType() != ValueType.EXPLICIT) {
      return property.getDefaultValue();
    }

    return value.getValue();
  }
}
