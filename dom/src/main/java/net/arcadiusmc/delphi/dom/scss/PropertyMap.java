package net.arcadiusmc.delphi.dom.scss;

import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.StyleProperties;
import org.jetbrains.annotations.Nullable;

public class PropertyMap extends ReadonlyMap implements StyleProperties {

  private boolean suppressSignals = false;
  private int changes = 0;

  public PropertyMap(PropertySet backing) {
    super(backing);
  }

  private StyleProperties signalChanged() {
    if (suppressSignals) {
      return this;
    }

    onChange();
    return this;
  }

  protected void onChange() {

  }

  private <T> void set(Property<T> property, T value) {
    // If nothing changed
    if (!backing.set(property, value)) {
      return;
    }

    changes |= property.getDirtyBits();
  }

  @Override
  public StyleProperties setMaxWidth(@Nullable Primitive value) {
    set(Properties.MAX_WIDTH, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setMaxHeight(@Nullable Primitive value) {
    set(Properties.MAX_HEIGHT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setMinWidth(@Nullable Primitive value) {
    set(Properties.MIN_WIDTH, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setMinHeight(@Nullable Primitive value) {
    set(Properties.MIN_HEIGHT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setPaddingTop(@Nullable Primitive value) {
    set(Properties.PADDING_TOP, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setPaddingRight(@Nullable Primitive value) {
    set(Properties.PADDING_RIGHT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setPaddingBottom(@Nullable Primitive value) {
    set(Properties.PADDING_BOTTOM, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setPaddingLeft(@Nullable Primitive value) {
    set(Properties.PADDING_LEFT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setPadding(Primitive value) {
    set(Properties.PADDING_TOP, value);
    set(Properties.PADDING_RIGHT, value);
    set(Properties.PADDING_BOTTOM, value);
    set(Properties.PADDING_LEFT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setPadding(Primitive x, Primitive y) {
    set(Properties.PADDING_TOP, y);
    set(Properties.PADDING_RIGHT, x);
    set(Properties.PADDING_BOTTOM, y);
    set(Properties.PADDING_LEFT, x);
    return signalChanged();
  }

  @Override
  public StyleProperties setPadding(Primitive top, Primitive x, Primitive bottom) {
    set(Properties.PADDING_TOP, top);
    set(Properties.PADDING_RIGHT, x);
    set(Properties.PADDING_BOTTOM, bottom);
    set(Properties.PADDING_LEFT, x);
    return signalChanged();
  }

  @Override
  public StyleProperties setPadding(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    set(Properties.PADDING_TOP, top);
    set(Properties.PADDING_RIGHT, right);
    set(Properties.PADDING_BOTTOM, bottom);
    set(Properties.PADDING_LEFT, left);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorderTop(@Nullable Primitive value) {
    set(Properties.BORDER_TOP, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorderRight(@Nullable Primitive value) {
    set(Properties.BORDER_RIGHT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorderBottom(@Nullable Primitive value) {
    set(Properties.BORDER_BOTTOM, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorderLeft(@Nullable Primitive value) {
    set(Properties.BORDER_LEFT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorder(Primitive value) {
    set(Properties.BORDER_TOP, value);
    set(Properties.BORDER_RIGHT, value);
    set(Properties.BORDER_BOTTOM, value);
    set(Properties.BORDER_LEFT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorder(Primitive x, Primitive y) {
    set(Properties.BORDER_TOP, y);
    set(Properties.BORDER_RIGHT, x);
    set(Properties.BORDER_BOTTOM, y);
    set(Properties.BORDER_LEFT, x);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorder(Primitive top, Primitive x, Primitive bottom) {
    set(Properties.BORDER_TOP, top);
    set(Properties.BORDER_RIGHT, x);
    set(Properties.BORDER_BOTTOM, bottom);
    set(Properties.BORDER_LEFT, x);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorder(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    set(Properties.BORDER_TOP, top);
    set(Properties.BORDER_RIGHT, right);
    set(Properties.BORDER_BOTTOM, bottom);
    set(Properties.BORDER_LEFT, left);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutlineTop(@Nullable Primitive value) {
    set(Properties.OUTLINE_TOP, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutlineRight(@Nullable Primitive value) {
    set(Properties.OUTLINE_RIGHT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutlineBottom(@Nullable Primitive value) {
    set(Properties.OUTLINE_BOTTOM, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutlineLeft(@Nullable Primitive value) {
    set(Properties.OUTLINE_LEFT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutline(Primitive value) {
    set(Properties.OUTLINE_TOP, value);
    set(Properties.OUTLINE_RIGHT, value);
    set(Properties.OUTLINE_BOTTOM, value);
    set(Properties.OUTLINE_LEFT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutline(Primitive x, Primitive y) {
    set(Properties.OUTLINE_TOP, y);
    set(Properties.OUTLINE_RIGHT, x);
    set(Properties.OUTLINE_BOTTOM, y);
    set(Properties.OUTLINE_LEFT, x);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutline(Primitive top, Primitive x, Primitive bottom) {
    set(Properties.OUTLINE_TOP, top);
    set(Properties.OUTLINE_RIGHT, x);
    set(Properties.OUTLINE_BOTTOM, bottom);
    set(Properties.OUTLINE_LEFT, x);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutline(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    set(Properties.OUTLINE_TOP, top);
    set(Properties.OUTLINE_RIGHT, right);
    set(Properties.OUTLINE_BOTTOM, bottom);
    set(Properties.OUTLINE_LEFT, left);
    return signalChanged();
  }

  @Override
  public StyleProperties setMarginTop(@Nullable Primitive value) {
    set(Properties.MARGIN_TOP, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setMarginRight(@Nullable Primitive value) {
    set(Properties.MARGIN_RIGHT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setMarginBottom(@Nullable Primitive value) {
    set(Properties.MARGIN_BOTTOM, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setMarginLeft(@Nullable Primitive value) {
    set(Properties.MARGIN_LEFT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setMargin(Primitive value) {
    set(Properties.MARGIN_TOP, value);
    set(Properties.MARGIN_RIGHT, value);
    set(Properties.MARGIN_BOTTOM, value);
    set(Properties.MARGIN_LEFT, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setMargin(Primitive x, Primitive y) {
    set(Properties.MARGIN_TOP, y);
    set(Properties.MARGIN_RIGHT, x);
    set(Properties.MARGIN_BOTTOM, y);
    set(Properties.MARGIN_LEFT, x);
    return signalChanged();
  }

  @Override
  public StyleProperties setMargin(Primitive top, Primitive x, Primitive bottom) {
    set(Properties.MARGIN_TOP, top);
    set(Properties.MARGIN_RIGHT, x);
    set(Properties.MARGIN_BOTTOM, bottom);
    set(Properties.MARGIN_LEFT, x);
    return signalChanged();
  }

  @Override
  public StyleProperties setMargin(Primitive top, Primitive right, Primitive bottom, Primitive left) {
    set(Properties.MARGIN_TOP, top);
    set(Properties.MARGIN_RIGHT, right);
    set(Properties.MARGIN_BOTTOM, bottom);
    set(Properties.MARGIN_LEFT, left);
    return signalChanged();
  }

  @Override
  public StyleProperties setZIndex(@Nullable Integer value) {
    set(Properties.Z_INDEX, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setScale(@Nullable Primitive value) {
    set(Properties.SCALE, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setTextColor(@Nullable Color value) {
    set(Properties.TEXT_COLOR, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setBackgroundColor(@Nullable Color value) {
    set(Properties.BACKGROUND_COLOR, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setBorderColor(@Nullable Color value) {
    set(Properties.BORDER_COLOR, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setOutlineColor(@Nullable Color value) {
    set(Properties.OUTLINE_COLOR, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setTextShadow(@Nullable Boolean value) {
    set(Properties.TEXT_SHADOW, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setDisplay(@Nullable DisplayType value) {
    set(Properties.DISPLAY, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setBold(@Nullable Boolean value) {
    set(Properties.BOLD, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setItalic(@Nullable Boolean value) {
    set(Properties.ITALIC, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setUnderlined(@Nullable Boolean value) {
    set(Properties.UNDERLINED, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setObfuscated(@Nullable Boolean value) {
    set(Properties.OBFUSCATED, value);
    return signalChanged();
  }

  @Override
  public StyleProperties setStrikethrough(@Nullable Boolean value) {
    set(Properties.STRIKETHROUGH, value);
    return signalChanged();
  }
}
