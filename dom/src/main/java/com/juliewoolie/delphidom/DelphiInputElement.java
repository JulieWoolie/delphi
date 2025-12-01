package com.juliewoolie.delphidom;

import com.google.common.base.Strings;
import java.util.Objects;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.InputElement;
import com.juliewoolie.dom.TagNames;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiInputElement extends DisableableElement implements InputElement {

  private String value;

  public DelphiInputElement(DelphiDocument document) {
    super(document, TagNames.INPUT);
  }

  @Override
  public boolean canHaveChildren() {
    return false;
  }

  @Override
  public String getPrompt() {
    return getAttribute(Attributes.PROMPT);
  }

  @Override
  public void setPrompt(@Nullable String prompt) {
    setAttribute(Attributes.PROMPT, prompt);
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

    boolean cancelled = true;
    try {
      cancelled = document.valueChanged(this, value, previousValue, player);
    } finally {
      if (cancelled) {
        this.value = previousValue;
      }
    }
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
