package net.arcadiusmc.delphi.dom.scss;

import lombok.Getter;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.StylePropertiesReadonly;
import org.jetbrains.annotations.Nullable;

public class ReadonlyMap implements StylePropertiesReadonly {

  @Getter
  protected final PropertySet backing;

  public ReadonlyMap(PropertySet backing) {
    this.backing = backing;
  }

  @Override
  public @Nullable Primitive getMaxWidth() {
    return backing.get(Properties.MAX_WIDTH);
  }

  @Override
  public @Nullable Primitive getMaxHeight() {
    return backing.get(Properties.MAX_HEIGHT);
  }

  @Override
  public @Nullable Primitive getMinWidth() {
    return backing.get(Properties.MIN_WIDTH);
  }

  @Override
  public @Nullable Primitive getMinHeight() {
    return backing.get(Properties.MIN_HEIGHT);
  }

  @Override
  public @Nullable Primitive getPaddingLeft() {
    return backing.get(Properties.PADDING_LEFT);
  }

  @Override
  public @Nullable Primitive getPaddingRight() {
    return backing.get(Properties.PADDING_RIGHT);
  }

  @Override
  public @Nullable Primitive getPaddingTop() {
    return backing.get(Properties.PADDING_TOP);
  }

  @Override
  public @Nullable Primitive getPaddingBottom() {
    return backing.get(Properties.PADDING_BOTTOM);
  }

  @Override
  public @Nullable Primitive getBorderLeft() {
    return backing.get(Properties.BORDER_LEFT);
  }

  @Override
  public @Nullable Primitive getBorderRight() {
    return backing.get(Properties.BORDER_RIGHT);
  }

  @Override
  public @Nullable Primitive getBorderTop() {
    return backing.get(Properties.BORDER_TOP);
  }

  @Override
  public @Nullable Primitive getBorderBottom() {
    return backing.get(Properties.BORDER_TOP);
  }

  @Override
  public @Nullable Primitive getOutlineLeft() {
    return backing.get(Properties.OUTLINE_LEFT);
  }

  @Override
  public @Nullable Primitive getOutlineRight() {
    return backing.get(Properties.OUTLINE_RIGHT);
  }

  @Override
  public @Nullable Primitive getOutlineTop() {
    return backing.get(Properties.OUTLINE_TOP);
  }

  @Override
  public @Nullable Primitive getOutlineBottom() {
    return backing.get(Properties.OUTLINE_BOTTOM);
  }

  @Override
  public @Nullable Primitive getMarginLeft() {
    return backing.get(Properties.MARGIN_LEFT);
  }

  @Override
  public @Nullable Primitive getMarginRight() {
    return backing.get(Properties.MARGIN_RIGHT);
  }

  @Override
  public @Nullable Primitive getMarginTop() {
    return backing.get(Properties.MARGIN_TOP);
  }

  @Override
  public @Nullable Primitive getMarginBottom() {
    return backing.get(Properties.MARGIN_BOTTOM);
  }

  @Override
  public @Nullable Primitive getScale() {
    return backing.get(Properties.SCALE);
  }

  @Override
  public int getZIndex() {
    return backing.get(Properties.Z_INDEX);
  }

  @Override
  public @Nullable Color getTextColor() {
    return backing.get(Properties.TEXT_COLOR);
  }

  @Override
  public @Nullable Color getBackgroundColor() {
    return backing.get(Properties.BACKGROUND_COLOR);
  }

  @Override
  public @Nullable Color getBorderColor() {
    return backing.get(Properties.BORDER_COLOR);
  }

  @Override
  public @Nullable Color getOutlineColor() {
    return backing.get(Properties.OUTLINE_COLOR);
  }

  @Override
  public boolean getTextShadow() {
    return backing.get(Properties.TEXT_SHADOW);
  }

  @Override
  public @Nullable DisplayType getDisplay() {
    return backing.get(Properties.DISPLAY);
  }

  @Override
  public boolean getBold() {
    return backing.get(Properties.BOLD);
  }

  @Override
  public boolean getItalic() {
    return backing.get(Properties.ITALIC);
  }

  @Override
  public boolean getUnderlined() {
    return backing.get(Properties.UNDERLINED);
  }

  @Override
  public boolean getObfuscated() {
    return backing.get(Properties.OBFUSCATED);
  }

  @Override
  public boolean getStrikethrough() {
    return backing.get(Properties.STRIKETHROUGH);
  }
}
