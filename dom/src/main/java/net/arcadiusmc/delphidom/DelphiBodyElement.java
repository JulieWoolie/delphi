package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.BodyElement;
import net.arcadiusmc.dom.TagNames;

public class DelphiBodyElement extends DelphiElement implements BodyElement {

  public DelphiBodyElement(DelphiDocument document) {
    super(document, TagNames.BODY);
  }
}
