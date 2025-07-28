package com.juliewoolie.delphidom;

import com.juliewoolie.dom.HeadElement;
import com.juliewoolie.dom.TagNames;

public class DelphiHeadElement extends DelphiElement implements HeadElement {

  public DelphiHeadElement(DelphiDocument document) {
    super(document, TagNames.HEAD);
  }
}
