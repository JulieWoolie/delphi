package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.HeaderElement;
import net.arcadiusmc.dom.TagNames;

public class DelphiHeaderElement extends DelphiElement implements HeaderElement {

  public DelphiHeaderElement(DelphiDocument document) {
    super(document, TagNames.HEAD);
  }
}
