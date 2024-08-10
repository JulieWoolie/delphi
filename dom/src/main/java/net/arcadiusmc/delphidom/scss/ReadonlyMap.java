package net.arcadiusmc.delphidom.scss;

import lombok.Getter;
import net.arcadiusmc.dom.style.AlignItems;
import net.arcadiusmc.dom.style.Color;
import net.arcadiusmc.dom.style.DisplayType;
import net.arcadiusmc.dom.style.FlexDirection;
import net.arcadiusmc.dom.style.FlexWrap;
import net.arcadiusmc.dom.style.JustifyContent;
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
    return backing.orNull(Properties.MAX_WIDTH);
  }

  @Override
  public @Nullable Primitive getMaxHeight() {
    return backing.orNull(Properties.MAX_HEIGHT);
  }

  @Override
  public @Nullable Primitive getMinWidth() {
    return backing.orNull(Properties.MIN_WIDTH);
  }

  @Override
  public @Nullable Primitive getMinHeight() {
    return backing.orNull(Properties.MIN_HEIGHT);
  }

  @Override
  public @Nullable Primitive getPaddingLeft() {
    return backing.orNull(Properties.PADDING_LEFT);
  }

  @Override
  public @Nullable Primitive getPaddingRight() {
    return backing.orNull(Properties.PADDING_RIGHT);
  }

  @Override
  public @Nullable Primitive getPaddingTop() {
    return backing.orNull(Properties.PADDING_TOP);
  }

  @Override
  public @Nullable Primitive getPaddingBottom() {
    return backing.orNull(Properties.PADDING_BOTTOM);
  }

  @Override
  public @Nullable Primitive getBorderLeft() {
    return backing.orNull(Properties.BORDER_LEFT);
  }

  @Override
  public @Nullable Primitive getBorderRight() {
    return backing.orNull(Properties.BORDER_RIGHT);
  }

  @Override
  public @Nullable Primitive getBorderTop() {
    return backing.orNull(Properties.BORDER_TOP);
  }

  @Override
  public @Nullable Primitive getBorderBottom() {
    return backing.orNull(Properties.BORDER_TOP);
  }

  @Override
  public @Nullable Primitive getOutlineLeft() {
    return backing.orNull(Properties.OUTLINE_LEFT);
  }

  @Override
  public @Nullable Primitive getOutlineRight() {
    return backing.orNull(Properties.OUTLINE_RIGHT);
  }

  @Override
  public @Nullable Primitive getOutlineTop() {
    return backing.orNull(Properties.OUTLINE_TOP);
  }

  @Override
  public @Nullable Primitive getOutlineBottom() {
    return backing.orNull(Properties.OUTLINE_BOTTOM);
  }

  @Override
  public @Nullable Primitive getMarginLeft() {
    return backing.orNull(Properties.MARGIN_LEFT);
  }

  @Override
  public @Nullable Primitive getMarginRight() {
    return backing.orNull(Properties.MARGIN_RIGHT);
  }

  @Override
  public @Nullable Primitive getMarginTop() {
    return backing.orNull(Properties.MARGIN_TOP);
  }

  @Override
  public @Nullable Primitive getMarginBottom() {
    return backing.orNull(Properties.MARGIN_BOTTOM);
  }

  @Override
  public @Nullable Primitive getScale() {
    return backing.orNull(Properties.SCALE);
  }

  @Override
  public int getZIndex() {
    return backing.get(Properties.Z_INDEX);
  }

  @Override
  public @Nullable Color getTextColor() {
    return backing.orNull(Properties.TEXT_COLOR);
  }

  @Override
  public @Nullable Color getBackgroundColor() {
    return backing.orNull(Properties.BACKGROUND_COLOR);
  }

  @Override
  public @Nullable Color getBorderColor() {
    return backing.orNull(Properties.BORDER_COLOR);
  }

  @Override
  public @Nullable Color getOutlineColor() {
    return backing.orNull(Properties.OUTLINE_COLOR);
  }

  @Override
  public boolean getTextShadow() {
    return backing.get(Properties.TEXT_SHADOW);
  }

  @Override
  public @Nullable DisplayType getDisplay() {
    return backing.orNull(Properties.DISPLAY);
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

  @Override
  public @Nullable FlexDirection getFlexDirection() {
    return backing.orNull(Properties.FLEX_DIRECTION);
  }

  @Override
  public @Nullable FlexWrap getFlexWrap() {
    return backing.orNull(Properties.FLEX_WRAP);
  }

  @Override
  public @Nullable JustifyContent getJustifyContent() {
    return backing.orNull(Properties.JUSTIFY_CONTENT);
  }

  @Override
  public @Nullable AlignItems getAlignItems() {
    return backing.orNull(Properties.ALIGN_ITEMS);
  }

  @Override
  public int getOrder() {
    return backing.get(Properties.ORDER);
  }
}
