package com.juliewoolie.dom.style;

import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Modifiable map of style properties
 */
public interface StyleProperties extends StylePropertiesReadonly {

  /**
   * Set the {@code color} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setColor(@Nullable String value);

  /**
   * Set the {@code color} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setColor(@Nullable Color value);

  /**
   * Set the {@code background-color} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBackgroundColor(@Nullable String value);

  /**
   * Set the {@code background-color} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBackgroundColor(@Nullable Color value);

  /**
   * Set the {@code border-color} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderColor(@Nullable String value);

  /**
   * Set the {@code border-color} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderColor(@Nullable Color value);

  /**
   * Set the {@code outline-color} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineColor(@Nullable String value);

  /**
   * Set the {@code outline-color} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineColor(@Nullable Color value);

  /**
   * Set the {@code text-shadow} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setTextShadow(@Nullable String value);

  /**
   * Set the {@code text-shadow} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setTextShadow(@Nullable Boolean value);

  /**
   * Set the {@code bold} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBold(@Nullable String value);

  /**
   * Set the {@code bold} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBold(@Nullable Boolean value);

  /**
   * Set the {@code italic} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setItalic(@Nullable String value);

  /**
   * Set the {@code italic} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setItalic(@Nullable Boolean value);

  /**
   * Set the {@code underlined} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setUnderlined(@Nullable String value);

  /**
   * Set the {@code underlined} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setUnderlined(@Nullable Boolean value);

  /**
   * Set the {@code strikethrough} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setStrikethrough(@Nullable String value);

  /**
   * Set the {@code strikethrough} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setStrikethrough(@Nullable Boolean value);

  /**
   * Set the {@code obfuscated} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setObfuscated(@Nullable String value);

  /**
   * Set the {@code obfuscated} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setObfuscated(@Nullable Boolean value);

  /**
   * Set the {@code display} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setDisplay(@Nullable String value);

  /**
   * Set the {@code display} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setDisplay(@Nullable DisplayType value);

  /**
   * Set the {@code scale} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setFontSize(@Nullable String value);

  /**
   * Set the {@code scale} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setFontSize(@Nullable Primitive value);

  /**
   * Set the {@code width} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setWidth(@Nullable String value);

  /**
   * Set the {@code width} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setWidth(@Nullable Primitive value);

  /**
   * Set the {@code height} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setHeight(@Nullable String value);

  /**
   * Set the {@code height} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setHeight(@Nullable Primitive value);

  /**
   * Set the {@code max-width} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMaxWidth(@Nullable String value);

  /**
   * Set the {@code max-width} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMaxWidth(@Nullable Primitive value);

  /**
   * Set the {@code min-width} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMinWidth(@Nullable String value);

  /**
   * Set the {@code min-width} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMinWidth(@Nullable Primitive value);

  /**
   * Set the {@code max-height} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMaxHeight(@Nullable String value);

  /**
   * Set the {@code max-height} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMaxHeight(@Nullable Primitive value);

  /**
   * Set the {@code min-height} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMinHeight(@Nullable String value);

  /**
   * Set the {@code min-height} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMinHeight(@Nullable Primitive value);

  /**
   * Set the {@code padding} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPadding(@Nullable String value);

  /**
   * Set the {@code padding} property.
   * @param value value padding
   * @return {@code this}
   */
  StyleProperties setPadding(Primitive value);

  /**
   * Set the {@code padding} property.
   * @param x x padding
   * @param y y padding
   * @return {@code this}
   */
  StyleProperties setPadding(Primitive x, Primitive y);

  /**
   * Set the {@code padding} property.
   * @param top top padding
   * @param x x padding
   * @param bottom bottom padding
   * @return {@code this}
   */
  StyleProperties setPadding(Primitive top, Primitive x, Primitive bottom);

  /**
   * Set the {@code padding} property.
   * @param top top padding
   * @param right right padding
   * @param bottom bottom padding
   * @param left left padding
   * @return {@code this}
   */
  StyleProperties setPadding(Primitive top, Primitive right, Primitive bottom, Primitive left);

  /**
   * Set the {@code padding-top} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPaddingTop(@Nullable String value);

  /**
   * Set the {@code padding-top} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPaddingTop(@Nullable Primitive value);

  /**
   * Set the {@code padding-right} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPaddingRight(@Nullable String value);

  /**
   * Set the {@code padding-right} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPaddingRight(@Nullable Primitive value);

  /**
   * Set the {@code padding-bottom} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPaddingBottom(@Nullable String value);

  /**
   * Set the {@code padding-bottom} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPaddingBottom(@Nullable Primitive value);

  /**
   * Set the {@code padding-left} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPaddingLeft(@Nullable String value);

  /**
   * Set the {@code padding-left} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setPaddingLeft(@Nullable Primitive value);

  /**
   * Set the {@code outline} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutline(@Nullable String value);

  /**
   * Set the {@code outline} property.
   * @param value value outline
   * @return {@code this}
   */
  StyleProperties setOutline(Primitive value);

  /**
   * Set the {@code outline} property.
   * @param x x outline
   * @param y y outline
   * @return {@code this}
   */
  StyleProperties setOutline(Primitive x, Primitive y);

  /**
   * Set the {@code outline} property.
   * @param top top outline
   * @param x x outline
   * @param bottom bottom outline
   * @return {@code this}
   */
  StyleProperties setOutline(Primitive top, Primitive x, Primitive bottom);

  /**
   * Set the {@code outline} property.
   * @param top top outline
   * @param right right outline
   * @param bottom bottom outline
   * @param left left outline
   * @return {@code this}
   */
  StyleProperties setOutline(Primitive top, Primitive right, Primitive bottom, Primitive left);

  /**
   * Set the {@code outline-top} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineTop(@Nullable String value);

  /**
   * Set the {@code outline-top} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineTop(@Nullable Primitive value);

  /**
   * Set the {@code outline-right} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineRight(@Nullable String value);

  /**
   * Set the {@code outline-right} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineRight(@Nullable Primitive value);

  /**
   * Set the {@code outline-bottom} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineBottom(@Nullable String value);

  /**
   * Set the {@code outline-bottom} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineBottom(@Nullable Primitive value);

  /**
   * Set the {@code outline-left} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineLeft(@Nullable String value);

  /**
   * Set the {@code outline-left} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOutlineLeft(@Nullable Primitive value);

  /**
   * Set the {@code border} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorder(@Nullable String value);

  /**
   * Set the {@code border} property.
   * @param value value border
   * @return {@code this}
   */
  StyleProperties setBorder(Primitive value);

  /**
   * Set the {@code border} property.
   * @param x x border
   * @param y y border
   * @return {@code this}
   */
  StyleProperties setBorder(Primitive x, Primitive y);

  /**
   * Set the {@code border} property.
   * @param top top border
   * @param x x border
   * @param bottom bottom border
   * @return {@code this}
   */
  StyleProperties setBorder(Primitive top, Primitive x, Primitive bottom);

  /**
   * Set the {@code border} property.
   * @param top top border
   * @param right right border
   * @param bottom bottom border
   * @param left left border
   * @return {@code this}
   */
  StyleProperties setBorder(Primitive top, Primitive right, Primitive bottom, Primitive left);

  /**
   * Set the {@code border-top} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderTop(@Nullable String value);

  /**
   * Set the {@code border-top} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderTop(@Nullable Primitive value);

  /**
   * Set the {@code border-right} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderRight(@Nullable String value);

  /**
   * Set the {@code border-right} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderRight(@Nullable Primitive value);

  /**
   * Set the {@code border-bottom} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderBottom(@Nullable String value);

  /**
   * Set the {@code border-bottom} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderBottom(@Nullable Primitive value);

  /**
   * Set the {@code border-left} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderLeft(@Nullable String value);

  /**
   * Set the {@code border-left} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBorderLeft(@Nullable Primitive value);

  /**
   * Set the {@code margin} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMargin(@Nullable String value);

  /**
   * Set the {@code margin} property.
   * @param value value margin
   * @return {@code this}
   */
  StyleProperties setMargin(Primitive value);

  /**
   * Set the {@code margin} property.
   * @param x x margin
   * @param y y margin
   * @return {@code this}
   */
  StyleProperties setMargin(Primitive x, Primitive y);

  /**
   * Set the {@code margin} property.
   * @param top top margin
   * @param x x margin
   * @param bottom bottom margin
   * @return {@code this}
   */
  StyleProperties setMargin(Primitive top, Primitive x, Primitive bottom);

  /**
   * Set the {@code margin} property.
   * @param top top margin
   * @param right right margin
   * @param bottom bottom margin
   * @param left left margin
   * @return {@code this}
   */
  StyleProperties setMargin(Primitive top, Primitive right, Primitive bottom, Primitive left);

  /**
   * Set the {@code margin-top} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginTop(@Nullable String value);

  /**
   * Set the {@code margin-top} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginTop(@Nullable Primitive value);

  /**
   * Set the {@code margin-right} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginRight(@Nullable String value);

  /**
   * Set the {@code margin-right} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginRight(@Nullable Primitive value);

  /**
   * Set the {@code margin-bottom} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginBottom(@Nullable String value);

  /**
   * Set the {@code margin-bottom} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginBottom(@Nullable Primitive value);

  /**
   * Set the {@code margin-left} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginLeft(@Nullable String value);

  /**
   * Set the {@code margin-left} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginLeft(@Nullable Primitive value);

  /**
   * Set the {@code z-index} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setZIndex(@Nullable String value);

  /**
   * Set the {@code z-index} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setZIndex(@Nullable Integer value);

  /**
   * Set the {@code align-items} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setAlignItems(@Nullable String value);

  /**
   * Set the {@code align-items} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setAlignItems(@Nullable AlignItems value);

  /**
   * Set the {@code flex-direction} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setFlexDirection(@Nullable String value);

  /**
   * Set the {@code flex-direction} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setFlexDirection(@Nullable FlexDirection value);

  /**
   * Set the {@code flex-wrap} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setFlexWrap(@Nullable String value);

  /**
   * Set the {@code flex-wrap} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setFlexWrap(@Nullable FlexWrap value);

  /**
   * Set the {@code justify-content} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setJustifyContent(@Nullable String value);

  /**
   * Set the {@code justify-content} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setJustifyContent(@Nullable JustifyContent value);

  /**
   * Set the {@code order} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOrder(@Nullable String value);

  /**
   * Set the {@code order} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setOrder(@Nullable Integer value);

  /**
   * Set the {@code box-sizing} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBoxSizing(@Nullable BoxSizing value);

  /**
   * Set the {@code box-sizing} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setBoxSizing(@Nullable String value);

  /**
   * Set the {@code margin-inline-start} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginInlineStart(@Nullable Primitive value);

  /**
   * Set the {@code margin-inline-start} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginInlineStart(@Nullable String value);

  /**
   * Set the {@code margin-inline-end} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginInlineEnd(@Nullable Primitive value);

  /**
   * Set the {@code margin-inline-end} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginInlineEnd(@Nullable String value);

  /**
   * Set the {@code margin-inline} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginInline(@Nullable String value);

  /**
   * Set the {@code margin-inline} property.
   * @param value Value
   * @return {@code this}
   */
  StyleProperties setMarginInline(@Nullable Primitive value);

  /**
   * Set the {@code margin-inline} property.
   * @param start Margin inline start
   * @param end Margin inline end
   * @return {@code this}
   */
  StyleProperties setMarginInline(Primitive start, Primitive end);

  /**
   * Set the {@code flex-basis} property.
   * @param value Flex basis value
   * @return {@code this}
   */
  StyleProperties setFlexBasis(@Nullable Primitive value);

  /**
   * Set the {@code flex-basis} property.
   * @param value Flex basis value
   * @return {@code this}
   */
  StyleProperties setFlexBasis(@Nullable String value);

  /**
   * Set the value of a CSS property.
   *
   * @param propertyName CSS property name
   * @param value Property value
   *
   * @return {@code this}
   *
   * @throws NullPointerException If {@code propertyName} is {@code null}
   */
  StyleProperties setProperty(@NotNull String propertyName, @Nullable String value);
}