package com.juliewoolie.chimera.system;

import lombok.Getter;
import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.Property;
import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.PropertySet.PropertyIterator;
import com.juliewoolie.chimera.ReadonlyProperties;
import com.juliewoolie.chimera.StyleUpdateCallbacks;
import com.juliewoolie.chimera.Value;
import com.juliewoolie.dom.Node;

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
