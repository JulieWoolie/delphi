package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.HeadElement;
import net.arcadiusmc.dom.TagNames;

public class DelphiHeadElement extends DelphiElement implements HeadElement {

  public DelphiHeadElement(DelphiDocument document) {
    super(document, TagNames.HEAD);
  }
}
