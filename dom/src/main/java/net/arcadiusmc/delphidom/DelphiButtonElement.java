package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.ButtonElement;
import net.arcadiusmc.dom.TagNames;

public class DelphiButtonElement extends DelphiElement implements ButtonElement {

  public DelphiButtonElement(DelphiDocument document) {
    super(document, TagNames.BUTTON);
  }
}
