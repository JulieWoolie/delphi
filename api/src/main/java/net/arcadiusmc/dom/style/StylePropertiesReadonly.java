package net.arcadiusmc.dom.style;

import org.jetbrains.annotations.Nullable;

public interface StylePropertiesReadonly {

  @Nullable Primitive getMaxWidth();

  @Nullable Primitive getMaxHeight();

  @Nullable Primitive getMinWidth();

  @Nullable Primitive getMinHeight();

  @Nullable Primitive getPaddingLeft();

  @Nullable Primitive getPaddingRight();

  @Nullable Primitive getPaddingTop();

  @Nullable Primitive getPaddingBottom();

  @Nullable Primitive getBorderLeft();

  @Nullable Primitive getBorderRight();

  @Nullable Primitive getBorderTop();

  @Nullable Primitive getBorderBottom();

  @Nullable Primitive getOutlineLeft();

  @Nullable Primitive getOutlineRight();

  @Nullable Primitive getOutlineTop();

  @Nullable Primitive getOutlineBottom();

  @Nullable Primitive getMarginLeft();

  @Nullable Primitive getMarginRight();

  @Nullable Primitive getMarginTop();

  @Nullable Primitive getMarginBottom();

  @Nullable Primitive getScale();

  int getZIndex();

  @Nullable Color getTextColor();

  @Nullable Color getBackgroundColor();

  @Nullable Color getBorderColor();

  @Nullable Color getOutlineColor();

  boolean getTextShadow();

  @Nullable DisplayType getDisplay();

  boolean getBold();

  boolean getItalic();

  boolean getUnderlined();

  boolean getObfuscated();

  boolean getStrikethrough();

  /**
   * Get the {@code flex-direction} property value.
   * @return Flex direction, or {@code null}, if not set.
   */
  @Nullable FlexDirection getFlexDirection();

  /**
   * Get the {@code flex-wrap} property value.
   * @return Flex wrap, or {@code null}, if not set.
   */
  @Nullable FlexWrap getFlexWrap();

  /**
   * Get the {@code justify-content} property value.
   * @return Justify content, or {@code null}, if not set.
   */
  @Nullable JustifyContent getJustifyContent();

  /**
   * Get the {@code align-items} property value.
   * @return Item alignment, or {@code null}, if not set.
   */
  @Nullable AlignItems getAlignItems();

  /**
   * Get the {@code order} property value.
   * @return Order, or {@code 0} if not set
   */
  int getOrder();
}
