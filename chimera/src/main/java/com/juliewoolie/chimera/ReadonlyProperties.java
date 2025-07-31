package com.juliewoolie.chimera;

import com.google.common.base.Strings;
import java.util.HashSet;
import java.util.Set;
import com.juliewoolie.chimera.PropertySet.PropertyIterator;
import com.juliewoolie.dom.style.StylePropertiesReadonly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReadonlyProperties implements StylePropertiesReadonly {

  protected final PropertySet set;

  public ReadonlyProperties(PropertySet set) {
    this.set = set;
  }

  private <T> String get(Property<T> prop) {
    Value<T> value = set.orNull(prop);

    if (value == null) {
      return null;
    }

    return value.getTextValue();
  }

  @Override
  public @Nullable String getHeight() {
    return get(Properties.HEIGHT);
  }

  @Override
  public @Nullable String getWidth() {
    return get(Properties.WIDTH);
  }

  @Override
  public @Nullable String getMaxWidth() {
    return get(Properties.MAX_WIDTH);
  }

  @Override
  public @Nullable String getMaxHeight() {
    return get(Properties.MAX_HEIGHT);
  }

  @Override
  public @Nullable String getMinWidth() {
    return get(Properties.MIN_WIDTH);
  }

  @Override
  public @Nullable String getMinHeight() {
    return get(Properties.MIN_HEIGHT);
  }

  @Override
  public @Nullable String getPadding() {
    return get(Properties.PADDING);
  }

  @Override
  public @Nullable String getMargin() {
    return get(Properties.MARGIN);
  }

  @Override
  public @Nullable String getOutline() {
    return get(Properties.OUTLINE);
  }

  @Override
  public @Nullable String getBorder() {
    return get(Properties.BORDER);
  }

  @Override
  public @Nullable String getPaddingLeft() {
    return get(Properties.PADDING_LEFT);
  }

  @Override
  public @Nullable String getPaddingRight() {
    return get(Properties.PADDING_RIGHT);
  }

  @Override
  public @Nullable String getPaddingTop() {
    return get(Properties.PADDING_TOP);
  }

  @Override
  public @Nullable String getPaddingBottom() {
    return get(Properties.PADDING_BOTTOM);
  }

  @Override
  public @Nullable String getBorderLeft() {
    return get(Properties.BORDER_LEFT);
  }

  @Override
  public @Nullable String getBorderRight() {
    return get(Properties.BORDER_RIGHT);
  }

  @Override
  public @Nullable String getBorderTop() {
    return get(Properties.BORDER_TOP);
  }

  @Override
  public @Nullable String getBorderBottom() {
    return get(Properties.BORDER_TOP);
  }

  @Override
  public @Nullable String getOutlineLeft() {
    return get(Properties.OUTLINE_LEFT);
  }

  @Override
  public @Nullable String getOutlineRight() {
    return get(Properties.OUTLINE_RIGHT);
  }

  @Override
  public @Nullable String getOutlineTop() {
    return get(Properties.OUTLINE_TOP);
  }

  @Override
  public @Nullable String getOutlineBottom() {
    return get(Properties.OUTLINE_BOTTOM);
  }

  @Override
  public @Nullable String getMarginLeft() {
    return get(Properties.MARGIN_LEFT);
  }

  @Override
  public @Nullable String getMarginRight() {
    return get(Properties.MARGIN_RIGHT);
  }

  @Override
  public @Nullable String getMarginTop() {
    return get(Properties.MARGIN_TOP);
  }

  @Override
  public @Nullable String getMarginBottom() {
    return get(Properties.MARGIN_BOTTOM);
  }

  @Override
  public @Nullable String getFontSize() {
    return get(Properties.FONT_SIZE);
  }

  @Override
  public @Nullable String getZIndex() {
    return get(Properties.Z_INDEX);
  }

  @Override
  public @Nullable String getColor() {
    return get(Properties.COLOR);
  }

  @Override
  public @Nullable String getBackgroundColor() {
    return get(Properties.BACKGROUND_COLOR);
  }

  @Override
  public @Nullable String getBorderColor() {
    return get(Properties.BORDER_COLOR);
  }

  @Override
  public @Nullable String getOutlineColor() {
    return get(Properties.OUTLINE_COLOR);
  }

  @Override
  public @Nullable String getTextShadow() {
    return get(Properties.TEXT_SHADOW);
  }

  @Override
  public @Nullable String getDisplay() {
    return get(Properties.DISPLAY);
  }

  @Override
  public @Nullable String getBold() {
    return get(Properties.BOLD);
  }

  @Override
  public String getItalic() {
    return get(Properties.ITALIC);
  }

  @Override
  public String getUnderlined() {
    return get(Properties.UNDERLINED);
  }

  @Override
  public String getObfuscated() {
    return get(Properties.OBFUSCATED);
  }

  @Override
  public String getStrikethrough() {
    return get(Properties.STRIKETHROUGH);
  }

  @Override
  public @Nullable String getFlexDirection() {
    return get(Properties.FLEX_DIRECTION);
  }

  @Override
  public @Nullable String getFlexWrap() {
    return get(Properties.FLEX_WRAP);
  }

  @Override
  public @Nullable String getJustifyContent() {
    return get(Properties.JUSTIFY_CONTENT);
  }

  @Override
  public @Nullable String getAlignItems() {
    return get(Properties.ALIGN_ITEMS);
  }

  @Override
  public String getOrder() {
    return get(Properties.ORDER);
  }

  @Override
  public @Nullable String getBoxSizing() {
    return get(Properties.BOX_SIZING);
  }

  @Override
  public @Nullable String getMarginInlineStart() {
    return get(Properties.MARGIN_INLINE_START);
  }

  @Override
  public @Nullable String getMarginInlineEnd() {
    return get(Properties.MARGIN_INLINE_END);
  }

  @Override
  public @Nullable String getMarginInline() {
    return get(Properties.MARGIN_INLINE);
  }

  @Override
  public @Nullable String getFlexBasis() {
    return get(Properties.FLEX_BASIS);
  }

  @Override
  public @Nullable String getPropertyValue(String propertyName) {
    if (Strings.isNullOrEmpty(propertyName)) {
      return null;
    }

    Property<Object> prop = Properties.getByKey(propertyName);
    if (prop == null) {
      return null;
    }

    return get(prop);
  }

  @Override
  public @NotNull Set<String> getProperties() {
    Set<String> strings = new HashSet<>();
    PropertyIterator it = set.iterator();

    while (it.hasNext()) {
      it.next();
      strings.add(it.property().getKey());
    }

    return strings;
  }
}
