package net.arcadiusmc.delphidom;

import com.google.common.base.Strings;
import java.util.Objects;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.InputElement;
import net.arcadiusmc.dom.TagNames;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiInputElement extends DelphiElement implements InputElement {

  private String value;

  public DelphiInputElement(DelphiDocument document) {
    super(document, TagNames.INPUT);
  }

  @Override
  public boolean canHaveChildren() {
    return false;
  }

  @Override
  public boolean isDisabled() {
    return !Attributes.boolAttribute(getAttribute(Attributes.ENABLED), true);
  }

  @Override
  public void setDisabled(boolean disabled) {
    if (!disabled) {
      setAttribute(Attributes.ENABLED, null);
      return;
    }

    setAttribute(Attributes.ENABLED, "false");
  }

  @Override
  public void setType(@Nullable InputType type) {
    if (type == null) {
      setAttribute(Attributes.TYPE, null);
      return;
    }

    setAttribute(Attributes.TYPE, type.getKeyword());
  }

  @Override
  public @NotNull InputType getType() {
    String typeAttr = getAttribute(Attributes.TYPE);
    if (Strings.isNullOrEmpty(typeAttr)) {
      return InputType.TEXT;
    }

    String lower = typeAttr.toLowerCase();
    for (InputType inputType : InputType.values()) {
      if (!Objects.equals(inputType.getKeyword(), lower)) {
        continue;
      }

      return inputType;
    }

    return InputType.TEXT;
  }

  public String getDisplayText() {
    if (Strings.isNullOrEmpty(value)) {
      return getPlaceholder();
    }

    InputType type = getType();
    if (type == InputType.PASSWORD) {
      return "*".repeat(value.length());
    }

    return value;
  }

  @Override
  public @Nullable String getValue() {
    return value;
  }

  @Override
  public void setValue(@Nullable String value) {
    changeValue(value, null);
  }

  @Override
  public void setValue(@Nullable String value, @NotNull Player player) {
    Objects.requireNonNull(player, "Null player");
    changeValue(value, player);
  }

  private void changeValue(String value, Player player) {
    String previousValue = this.value;
    this.value = value;

    document.valueChanged(this, value, previousValue, player);
  }

  @Override
  public @NotNull String getPlaceholder() {
    String attr = getAttribute(Attributes.PLACEHOLDER);
    if (attr == null) {
      return DEFAULT_PLACEHOLDER;
    }
    return attr;
  }

  @Override
  public void setPlaceholder(@Nullable String placeholder) {
    setAttribute(Attributes.PLACEHOLDER, placeholder);
  }
}
