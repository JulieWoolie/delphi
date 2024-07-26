package net.arcadiusmc.dom.style;

import org.jetbrains.annotations.Nullable;

public interface StyleProperties extends StylePropertiesReadonly {

  StyleProperties setMaxWidth(@Nullable Primitive value);

  StyleProperties setMaxHeight(@Nullable Primitive value);

  StyleProperties setMinWidth(@Nullable Primitive value);

  StyleProperties setMinHeight(@Nullable Primitive value);

  /**
   * Sets the top padding.
   * @param value top padding
   * @return {@code this}
   */
  StyleProperties setPaddingTop(@Nullable Primitive value);

  /**
   * Sets the right padding.
   * @param value right padding
   * @return {@code this}
   */
  StyleProperties setPaddingRight(@Nullable Primitive value);

  /**
   * Sets the bottom padding.
   * @param value bottom padding
   * @return {@code this}
   */
  StyleProperties setPaddingBottom(@Nullable Primitive value);

  /**
   * Sets the left padding.
   * @param value left padding
   * @return {@code this}
   */
  StyleProperties setPaddingLeft(@Nullable Primitive value);

  /**
   * Sets the padding.
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
   * Sets the padding.
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
   * Sets the padding.
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
   * Sets the padding.
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
   * Sets the top border.
   * @param value top border
   * @return {@code this}
   */
  StyleProperties setBorderTop(@Nullable Primitive value);

  /**
   * Sets the right border.
   * @param value right border
   * @return {@code this}
   */
  StyleProperties setBorderRight(@Nullable Primitive value);

  /**
   * Sets the bottom border.
   * @param value bottom border
   * @return {@code this}
   */
  StyleProperties setBorderBottom(@Nullable Primitive value);

  /**
   * Sets the left border.
   * @param value left border
   * @return {@code this}
   */
  StyleProperties setBorderLeft(@Nullable Primitive value);

  /**
   * Sets the border.
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
   * Sets the border.
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
   * Sets the border.
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
   * Sets the border.
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
   * Sets the top outline.
   * @param value top outline
   * @return {@code this}
   */
  StyleProperties setOutlineTop(@Nullable Primitive value);

  /**
   * Sets the right outline.
   * @param value right outline
   * @return {@code this}
   */
  StyleProperties setOutlineRight(@Nullable Primitive value);

  /**
   * Sets the bottom outline.
   * @param value bottom outline
   * @return {@code this}
   */
  StyleProperties setOutlineBottom(@Nullable Primitive value);

  /**
   * Sets the left outline.
   * @param value left outline
   * @return {@code this}
   */
  StyleProperties setOutlineLeft(@Nullable Primitive value);

  /**
   * Sets the outline.
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
   * Sets the outline.
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
   * Sets the outline.
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
   * Sets the outline.
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
   * Sets the top margin.
   * @param value top margin
   * @return {@code this}
   */
  StyleProperties setMarginTop(@Nullable Primitive value);

  /**
   * Sets the right margin.
   * @param value right margin
   * @return {@code this}
   */
  StyleProperties setMarginRight(@Nullable Primitive value);

  /**
   * Sets the bottom margin.
   * @param value bottom margin
   * @return {@code this}
   */
  StyleProperties setMarginBottom(@Nullable Primitive value);

  /**
   * Sets the left margin.
   * @param value left margin
   * @return {@code this}
   */
  StyleProperties setMarginLeft(@Nullable Primitive value);

  /**
   * Sets the margin.
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
   * Sets the margin.
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
   * Sets the margin.
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
   * Sets the margin.
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
}
