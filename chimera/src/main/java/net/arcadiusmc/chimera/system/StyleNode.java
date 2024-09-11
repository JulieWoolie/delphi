package net.arcadiusmc.chimera.system;

import lombok.Getter;
import net.arcadiusmc.chimera.ComputedStyleSet;
import net.arcadiusmc.chimera.Property;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.PropertySet.PropertyIterator;
import net.arcadiusmc.chimera.ReadonlyProperties;
import net.arcadiusmc.chimera.StyleUpdateCallbacks;
import net.arcadiusmc.chimera.Value;
import net.arcadiusmc.dom.Node;

@Getter
public class StyleNode {

  final Node domNode;
  final StyleObjectModel system;

  final PropertySet styleSet;
  final ReadonlyProperties currentStyle;

  final ComputedStyleSet computedSet;

  ElementStyleNode parent;

  public StyleNode(Node domNode, StyleObjectModel system) {
    this.domNode = domNode;
    this.system = system;

    this.styleSet = new PropertySet();
    this.currentStyle = new ReadonlyProperties(styleSet);

    this.computedSet = new ComputedStyleSet();
  }

  public void updateStyle() {
    PropertySet newSet = new PropertySet();
    applyCascading(newSet);

    int changes = styleSet.setAll(newSet);
    if (changes == 0) {
      return;
    }

    computedSet.putAll(styleSet);
    triggerCallback(changes);
  }

  void triggerCallback(int changes) {
    StyleUpdateCallbacks updateCallbacks = system.updateCallbacks;
    if (updateCallbacks == null) {
      return;
    }

    updateCallbacks.styleUpdated(this, changes);
  }

  void applyCascading(PropertySet out) {
    if (parent == null) {
      return;
    }

    PropertySet parentSet = parent.getStyleSet();
    PropertyIterator iterator = parentSet.iterator();

    while (iterator.hasNext()) {
      iterator.next();

      Property<Object> prop = iterator.property();

      if (!prop.isCascading() /*&& !(node instanceof ElementStyleNode)*/) {
        continue;
      }
      if (out.has(prop)) {
        continue;
      }

      Value<Object> value = iterator.value();
      out.setValue(prop, value);
    }
  }
}
