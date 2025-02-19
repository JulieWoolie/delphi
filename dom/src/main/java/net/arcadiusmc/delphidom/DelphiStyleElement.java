package net.arcadiusmc.delphidom;

import lombok.Getter;
import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.StyleElement;
import net.arcadiusmc.dom.TagNames;

public class DelphiStyleElement extends DelphiElement implements StyleElement {

  @Getter
  public ChimeraStylesheet stylesheet;
  public ContentSource source = ContentSource.NONE;

  public DelphiStyleElement(DelphiDocument document) {
    super(document, TagNames.STYLE);
  }

  @Override
  public String getSource() {
    return getAttribute(Attributes.SOURCE);
  }
}
