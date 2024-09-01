package net.arcadiusmc.dom.style;

import org.jetbrains.annotations.Nullable;

public interface StylePropertiesReadonly {

  @Nullable String getMaxWidth();

  @Nullable String getMaxHeight();

  @Nullable String getMinWidth();

  @Nullable String getMinHeight();

  @Nullable String getPaddingLeft();

  @Nullable String getPaddingRight();

  @Nullable String getPaddingTop();

  @Nullable String getPaddingBottom();

  @Nullable String getBorderLeft();

  @Nullable String getBorderRight();

  @Nullable String getBorderTop();

  @Nullable String getBorderBottom();

  @Nullable String getOutlineLeft();

  @Nullable String getOutlineRight();

  @Nullable String getOutlineTop();

  @Nullable String getOutlineBottom();

  @Nullable String getMarginLeft();

  @Nullable String getMarginRight();

  @Nullable String getMarginTop();

  @Nullable String getMarginBottom();

  @Nullable String getScale();

  @Nullable String getZIndex();

  @Nullable String getTextColor();

  @Nullable String getBackgroundColor();

  @Nullable String getBorderColor();

  @Nullable String getOutlineColor();

  @Nullable String getTextShadow();

  @Nullable String getDisplay();

  @Nullable String getBold();

  @Nullable String getItalic();

  @Nullable String getUnderlined();

  @Nullable String getObfuscated();

  @Nullable String getStrikethrough();

  /**
   * Get the {@code flex-direction} property value.
   * @return Flex direction, or {@code null}, if not set.
   */
  @Nullable String getFlexDirection();

  /**
   * Get the {@code flex-wrap} property value.
   * @return Flex wrap, or {@code null}, if not set.
   */
  @Nullable String getFlexWrap();

  /**
   * Get the {@code justify-content} property value.
   * @return Justify content, or {@code null}, if not set.
   */
  @Nullable String getJustifyContent();

  /**
   * Get the {@code align-items} property value.
   * @return Item alignment, or {@code null}, if not set.
   */
  @Nullable String getAlignItems();

  /**
   * Get the {@code order} property value.
   * @return Order, or {@code 0} if not set
   */
  @Nullable String getOrder();
}
