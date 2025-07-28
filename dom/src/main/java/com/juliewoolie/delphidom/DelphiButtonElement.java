package com.juliewoolie.delphidom;

import com.google.common.base.Strings;
import java.util.Objects;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.ButtonElement;
import com.juliewoolie.dom.TagNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiButtonElement extends DelphiElement implements ButtonElement {

  char TYPE_CMD_SEPARATOR = ':';

  public DelphiButtonElement(DelphiDocument document) {
    super(document, TagNames.BUTTON);
  }

  @Override
  public boolean isEnabled() {
    return Attributes.boolAttribute(getAttribute(Attributes.ENABLED), true);
  }

  @Override
  public void setEnabled(boolean enabled) {
    setAttribute(Attributes.ENABLED, String.valueOf(enabled));
  }

  @Override
  public @NotNull ButtonTrigger getTrigger() {
    String attrValue = getAttribute(Attributes.ACTION_TRIGGER);

    for (ButtonTrigger value : ButtonTrigger.values()) {
      if (!Objects.equals(value.getAttributeValue(), attrValue)) {
        continue;
      }

      return value;
    }

    return ButtonTrigger.DEFAULT;
  }

  @Override
  public void setTrigger(@Nullable ButtonTrigger trigger) {
    if (trigger == null) {
      removeAttribute(Attributes.ACTION_TRIGGER);
      return;
    }

    setAttribute(Attributes.ACTION_TRIGGER, trigger.getAttributeValue());
  }

  @Override
  public @Nullable ButtonAction getAction() {
    String attrValue = getAttribute(Attributes.BUTTON_ACTION);
    if (Strings.isNullOrEmpty(attrValue)) {
      return null;
    }

    int idx = attrValue.indexOf(TYPE_CMD_SEPARATOR);
    String typeStr;
    String command;

    if (idx == -1){
      typeStr = attrValue.toLowerCase();
      command = null;
    } else {
      typeStr = attrValue.substring(0, idx).toLowerCase();
      command = Strings.emptyToNull(attrValue.substring(idx + 1).trim());
    }

    for (ButtonActionType value : ButtonActionType.values()) {
      if (!Objects.equals(value.getAttributeValue(), typeStr)) {
        continue;
      }

      return new ButtonAction(value, command);
    }

    return null;
  }

  @Override
  public void setAction(@Nullable ButtonAction action) {
    if (action == null) {
      removeAttribute(Attributes.BUTTON_ACTION);
      return;
    }

    Objects.requireNonNull(action.type(), "Null action type");

    StringBuilder builder = new StringBuilder();
    builder.append(action.type().getAttributeValue());

    if (!Strings.isNullOrEmpty(action.command())) {
      builder.append(TYPE_CMD_SEPARATOR).append(' ');
      builder.append(action.command());
    }

    setAttribute(Attributes.BUTTON_ACTION, builder.toString());
  }
}
