package net.arcadiusmc.chimera;

import net.arcadiusmc.chimera.system.ElementStyleNode;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Element;

public class InlineStyle extends PropertiesMap {

  private final ElementStyleNode node;

  public InlineStyle(PropertySet set, ElementStyleNode node) {
    super(set, node.getSystem());
    this.node = node;
  }

  @Override
  public PropertiesMap triggerChange() {
    Element element = node.getDomNode();

    if (element == null) {
      // Null during testing
      return this;
    }

    node.setSuppressingInlineUpdates(true);
    element.setAttribute(Attributes.STYLE, set.toParseString());
    node.setSuppressingInlineUpdates(false);

    return this;
  }
}
