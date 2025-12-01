package com.juliewoolie.delphidom;

import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.Disableable;

public abstract class DisableableElement extends DelphiElement implements Disableable {

  public DisableableElement(DelphiDocument document, String tagName) {
    super(document, tagName);
  }

  @Override
  public boolean isEnabled() {
    if (hasAttribute(Attributes.DISABLED)) {
      // Declaring a void attribute will make it have its name as the value
      String disabled = getAttribute(Attributes.DISABLED);
      return !Attributes.DISABLED.equals(disabled);
    }

    return Attributes.boolAttribute(getAttribute(Attributes.ENABLED), true);
  }

  @Override
  public boolean isDisabled() {
    return !isEnabled();
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (hasAttribute(Attributes.ENABLED)) {
      removeAttribute(Attributes.ENABLED);
    }

    if (enabled) {
      removeAttribute(Attributes.DISABLED);
    } else {
      setAttribute(Attributes.DISABLED, Attributes.DISABLED);
    }
  }

  @Override
  public void setDisabled(boolean disabled) {
    setEnabled(!disabled);
  }
}
