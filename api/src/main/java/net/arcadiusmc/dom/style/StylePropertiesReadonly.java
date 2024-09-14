package net.arcadiusmc.dom.style;

import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Unmodifiable map of style properties
 */
public interface StylePropertiesReadonly {

  @Nullable String getWidth();

  @Nullable String getHeight();

  @Nullable String getMaxWidth();

  @Nullable String getMaxHeight();

  @Nullable String getMinWidth();

  @Nullable String getMinHeight();

  @Nullable String getPadding();

  @Nullable String getMargin();

  @Nullable String getOutline();

  @Nullable String getBorder();

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

  @Nullable String getColor();

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

  /**
   * Get a set of CSS property names that are explicitly set
   * by this property set.
   *
   * @return Property names
   */
  @NotNull Set<String> getProperties();

  /**
   * Get the value of a CSS property.
   * <p>
   * If the specified {@code propertyName} is {@code null}, or if the property was not found,
   * or its value was not set, then this method will return {@code null}.
   *
   * @param propertyName CSS property name
   * @return Property value, or {@code null}, if the property is not set in this object.
   */
  @Contract("null -> null")
  @Nullable String getPropertyValue(String propertyName);
}
