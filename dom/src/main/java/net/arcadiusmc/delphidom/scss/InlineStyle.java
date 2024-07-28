package net.arcadiusmc.delphidom.scss;

import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.Attr;

public class InlineStyle extends PropertyMap {

  private final DelphiElement element;

  public InlineStyle(PropertySet backing, DelphiElement element) {
    super(backing);
    this.element = element;
  }

  @Override
  protected void onChange() {
    String str = backing.toParseString();
    element.inlineUpdatesSuppressed = true;
    element.setAttribute(Attr.STYLE, str);
    element.inlineUpdatesSuppressed = false;
  }
}
