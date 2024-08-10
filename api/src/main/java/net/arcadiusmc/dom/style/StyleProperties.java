package net.arcadiusmc.dom.style;

import org.jetbrains.annotations.Nullable;

public interface StyleProperties extends StylePropertiesReadonly {

  StyleProperties setMaxWidth(@Nullable Primitive value);

  StyleProperties setMaxHeight(@Nullable Primitive value);

  StyleProperties setMinWidth(@Nullable Primitive value);

  StyleProperties setMinHeight(@Nullable Primitive value);

  /**
   * Set the {@code padding-top} property value.
   * @param value top padding
   * @return {@code this}
   */
  StyleProperties setPaddingTop(@Nullable Primitive value);

  /**
   * Set the {@code padding-right} property value.
   * @param value right padding
   * @return {@code this}
   */
  StyleProperties setPaddingRight(@Nullable Primitive value);

  /**
   * Set the {@code padding-bottom} property value.
   * @param value bottom padding
   * @return {@code this}
   */
  StyleProperties setPaddingBottom(@Nullable Primitive value);

  /**
   * Set the {@code padding-left} property value.
   * @param value left padding
   * @return {@code this}
   */
  StyleProperties setPaddingLeft(@Nullable Primitive value);

  /**
   * Set the padding.
   *
   * @param value Padding, applied to all sides
   *
   * @return {@code this}.
   */
  default StyleProperties setPadding(Primitive value) {
    return this.setPaddingTop(value)
        .setPaddingRight(value)
        .setPaddingBottom(value)
        .setPaddingLeft(value);
  }

  /**
   * Set the padding.
   *
   * @param x left and right padding
   * @param y top and bottom padding
   *
   * @return {@code this}.
   */
  default StyleProperties setPadding(Primitive x, Primitive y) {
    return this.setPaddingTop(y)
        .setPaddingRight(x)
        .setPaddingBottom(y)
        .setPaddingLeft(x);
  }

  /**
   * Set the padding.
   *
   * @param top top padding
   * @param x left and right padding
   * @param bottom bottom padding
   *
   * @return {@code this}.
   */
  default StyleProperties setPadding(Primitive top, Primitive x, Primitive bottom) {
    return this.setPaddingTop(top)
        .setPaddingRight(x)
        .setPaddingBottom(bottom)
        .setPaddingLeft(x);
  }

  /**
   * Set the padding.
   *
   * @param top top padding
   * @param right right side padding
   * @param left left side padding
   * @param bottom bottom padding
   *
   * @return {@code this}.
   */
  default StyleProperties setPadding(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    return this.setPaddingTop(top)
        .setPaddingRight(right)
        .setPaddingBottom(bottom)
        .setPaddingLeft(left);
  }

  /**
   * Set the {@code border-top} property value.
   * @param value top border
   * @return {@code this}
   */
  StyleProperties setBorderTop(@Nullable Primitive value);

  /**
   * Set the {@code border-right} property value.
   * @param value right border
   * @return {@code this}
   */
  StyleProperties setBorderRight(@Nullable Primitive value);

  /**
   * Set the {@code border-bottom} property value.
   * @param value bottom border
   * @return {@code this}
   */
  StyleProperties setBorderBottom(@Nullable Primitive value);

  /**
   * Set the {@code border-left} property value.
   * @param value left border
   * @return {@code this}
   */
  StyleProperties setBorderLeft(@Nullable Primitive value);

  /**
   * Set the border.
   *
   * @param value Border, applied to all sides
   *
   * @return {@code this}.
   */
  default StyleProperties setBorder(Primitive value) {
    return this.setBorderTop(value)
        .setBorderRight(value)
        .setBorderBottom(value)
        .setBorderLeft(value);
  }

  /**
   * Set the border.
   *
   * @param x left and right border
   * @param y top and bottom border
   *
   * @return {@code this}.
   */
  default StyleProperties setBorder(Primitive x, Primitive y) {
    return this.setBorderTop(y)
        .setBorderRight(x)
        .setBorderBottom(y)
        .setBorderLeft(x);
  }

  /**
   * Set the border.
   *
   * @param top top border
   * @param x left and right border
   * @param bottom bottom border
   *
   * @return {@code this}.
   */
  default StyleProperties setBorder(Primitive top, Primitive x, Primitive bottom) {
    return this.setBorderTop(top)
        .setBorderRight(x)
        .setBorderBottom(bottom)
        .setBorderLeft(x);
  }

  /**
   * Set the border.
   *
   * @param top top border
   * @param right right side border
   * @param left left side border
   * @param bottom bottom border
   *
   * @return {@code this}.
   */
  default StyleProperties setBorder(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    return this.setBorderTop(top)
        .setBorderRight(right)
        .setBorderBottom(bottom)
        .setBorderLeft(left);
  }

  /**
   * Set the {@code outline-top} property value.
   * @param value top outline
   * @return {@code this}
   */
  StyleProperties setOutlineTop(@Nullable Primitive value);

  /**
   * Set the {@code outline-right} property value.
   * @param value right outline
   * @return {@code this}
   */
  StyleProperties setOutlineRight(@Nullable Primitive value);

  /**
   * Set the {@code outline-bottom} property value.
   * @param value bottom outline
   * @return {@code this}
   */
  StyleProperties setOutlineBottom(@Nullable Primitive value);

  /**
   * Set the {@code outline-left} property value.
   * @param value left outline
   * @return {@code this}
   */
  StyleProperties setOutlineLeft(@Nullable Primitive value);

  /**
   * Set the outline.
   *
   * @param value Outline, applied to all sides
   *
   * @return {@code this}.
   */
  default StyleProperties setOutline(Primitive value) {
    return this.setOutlineTop(value)
        .setOutlineRight(value)
        .setOutlineBottom(value)
        .setOutlineLeft(value);
  }

  /**
   * Set the outline.
   *
   * @param x left and right outline
   * @param y top and bottom outline
   *
   * @return {@code this}.
   */
  default StyleProperties setOutline(Primitive x, Primitive y) {
    return this.setOutlineTop(y)
        .setOutlineRight(x)
        .setOutlineBottom(y)
        .setOutlineLeft(x);
  }

  /**
   * Set the outline.
   *
   * @param top top outline
   * @param x left and right outline
   * @param bottom bottom outline
   *
   * @return {@code this}.
   */
  default StyleProperties setOutline(Primitive top, Primitive x, Primitive bottom) {
    return this.setOutlineTop(top)
        .setOutlineRight(x)
        .setOutlineBottom(bottom)
        .setOutlineLeft(x);
  }

  /**
   * Set the outline.
   *
   * @param top top outline
   * @param right right side outline
   * @param left left side outline
   * @param bottom bottom outline
   *
   * @return {@code this}.
   */
  default StyleProperties setOutline(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    return this.setOutlineTop(top)
        .setOutlineRight(right)
        .setOutlineBottom(bottom)
        .setOutlineLeft(left);
  }

  /**
   * Set the {@code margin-top} property value.
   * @param value top margin
   * @return {@code this}
   */
  StyleProperties setMarginTop(@Nullable Primitive value);

  /**
   * Set the {@code margin-right} property value.
   * @param value right margin
   * @return {@code this}
   */
  StyleProperties setMarginRight(@Nullable Primitive value);

  /**
   * Set the {@code margin-bottom} property value.
   * @param value bottom margin
   * @return {@code this}
   */
  StyleProperties setMarginBottom(@Nullable Primitive value);

  /**
   * Set the {@code margin-left} property value.
   * @param value left margin
   * @return {@code this}
   */
  StyleProperties setMarginLeft(@Nullable Primitive value);

  /**
   * Set the margin.
   *
   * @param value Margin, applied to all sides
   *
   * @return {@code this}.
   */
  default StyleProperties setMargin(Primitive value) {
    return this.setMarginTop(value)
        .setMarginRight(value)
        .setMarginBottom(value)
        .setMarginLeft(value);
  }

  /**
   * Set the margin.
   *
   * @param x left and right margin
   * @param y top and bottom margin
   *
   * @return {@code this}.
   */
  default StyleProperties setMargin(Primitive x, Primitive y) {
    return this.setMarginTop(y)
        .setMarginRight(x)
        .setMarginBottom(y)
        .setMarginLeft(x);
  }

  /**
   * Set the margin.
   *
   * @param top top margin
   * @param x left and right margin
   * @param bottom bottom margin
   *
   * @return {@code this}.
   */
  default StyleProperties setMargin(Primitive top, Primitive x, Primitive bottom) {
    return this.setMarginTop(top)
        .setMarginRight(x)
        .setMarginBottom(bottom)
        .setMarginLeft(x);
  }

  /**
   * Set the margin.
   *
   * @param top top margin
   * @param right right side margin
   * @param left left side margin
   * @param bottom bottom margin
   *
   * @return {@code this}.
   */
  default StyleProperties setMargin(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    return this.setMarginTop(top)
        .setMarginRight(right)
        .setMarginBottom(bottom)
        .setMarginLeft(left);
  }

  StyleProperties setZIndex(@Nullable Integer value);

  StyleProperties setScale(@Nullable Primitive value);

  StyleProperties setTextColor(@Nullable Color value);

  StyleProperties setBackgroundColor(@Nullable Color value);

  StyleProperties setBorderColor(@Nullable Color value);

  StyleProperties setOutlineColor(@Nullable Color value);

  StyleProperties setTextShadow(@Nullable Boolean value);

  StyleProperties setDisplay(@Nullable DisplayType value);

  StyleProperties setBold(@Nullable Boolean value);

  StyleProperties setItalic(@Nullable Boolean value);

  StyleProperties setUnderlined(@Nullable Boolean value);

  StyleProperties setObfuscated(@Nullable Boolean value);

  StyleProperties setStrikethrough(@Nullable Boolean value);
  
  StyleProperties setFlexDirection(@Nullable FlexDirection value);
  
  StyleProperties setFlexWrap(@Nullable FlexWrap value);
  
  StyleProperties setJustifyContent(@Nullable JustifyContent value);
  
  StyleProperties setAlignItems(@Nullable AlignItems items);
  
  StyleProperties setOrder(@Nullable Integer order);
}
