package com.juliewoolie.delphidom;

import com.juliewoolie.dom.BodyElement;
import com.juliewoolie.dom.TagNames;

public class DelphiBodyElement extends DelphiElement implements BodyElement {

  public DelphiBodyElement(DelphiDocument document) {
    super(document, TagNames.BODY);
  }
}
