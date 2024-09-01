package net.arcadiusmc.chimera.system;

import lombok.Getter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.ReadonlyProperties;
import net.arcadiusmc.dom.Node;

@Getter
public class StyleNode {

  private final Node domNode;
  private final StyleSystem system;

  private final PropertySet styleSet;
  private final ReadonlyProperties currentStyle;

  private final ComputedStyleSet computedSet;

  ElementStyleNode parent;

  public StyleNode(Node domNode, StyleSystem system) {
    this.domNode = domNode;
    this.system = system;

    this.styleSet = new PropertySet();
    this.currentStyle = new ReadonlyProperties(styleSet);

    this.computedSet = new ComputedStyleSet();
  }
}
