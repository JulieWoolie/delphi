package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.ButtonElement;
import net.arcadiusmc.dom.TagNames;

public class DelphiButtonElement extends DelphiElement implements ButtonElement {

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
}
