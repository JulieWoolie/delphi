package com.juliewoolie.delphidom;

import lombok.Getter;
import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.StyleElement;
import com.juliewoolie.dom.TagNames;

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
