package net.arcadiusmc.chimera.system;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraSheetBuilder;
import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.Property;
import net.arcadiusmc.chimera.PropertySet;
import net.arcadiusmc.chimera.PropertySet.RuleIterator;
import net.arcadiusmc.chimera.Rule;
import net.arcadiusmc.chimera.StyleUpdateCallbacks;
import net.arcadiusmc.chimera.Value;
import net.arcadiusmc.chimera.Value.ValueType;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.style.StyleProperties;
import net.arcadiusmc.dom.style.StylePropertiesReadonly;

@Getter
public class StyleSystem {

  private final Document document;

  private final List<ChimeraStylesheet> sheets = new ArrayList<>();
  private final List<Rule> rules = new ArrayList<>();

  private final Map<Node, StyleNode> styleNodes = new HashMap<>();

  private final Map<String, Object> variables = new HashMap<>();

  @Setter
  private ChimeraStylesheet defaultStyleSheet;

  @Setter
  private StyleUpdateCallbacks updateCallbacks;

  public StyleSystem(Document document) {
    this.document = document;

    // Can be null during testing
    if (document != null) {
      initialize();
    }
  }

  private void initialize() {
    EventTarget l = document.getGlobalTarget();

    l.addEventListener(EventTypes.MODIFY_ATTR, new InlineMutateListener());

    StyleUpdateListener updateListener = new StyleUpdateListener();
    l.addEventListener(EventTypes.APPEND_CHILD, updateListener);
    l.addEventListener(EventTypes.REMOVE_CHILD, updateListener);

    l.addEventListener(EventTypes.MODIFY_ATTR, updateListener);

    l.addEventListener(EventTypes.CLICK_EXPIRE, updateListener);
    l.addEventListener(EventTypes.CLICK, updateListener);
  }

  public void addStylesheet(ChimeraStylesheet stylesheet) {
    sheets.addLast(stylesheet);

    for (int i = 0; i < stylesheet.getLength(); i++) {
      Rule r = stylesheet.getRule(i);
      rules.addLast(r);
    }

    rules.sort(Comparator.naturalOrder());
  }

  private void applyCascading(StyleNode node, PropertySet out) {
    ElementStyleNode p = node.getParent();
    if (p == null) {
      return;
    }

    PropertySet parentSet = p.getStyleSet();
    RuleIterator iterator = parentSet.iterator();

    while (iterator.hasNext()) {
      iterator.next();

      Property<Object> prop = iterator.property();

      if (!prop.isCascading() && !(node instanceof ElementStyleNode)) {
        continue;
      }

      Value<Object> value = iterator.value();
      out.setValue(prop, value);
    }
  }

  public void updateStyle(StyleNode node) {
    PropertySet current = node.getStyleSet();
    PropertySet newSet = new PropertySet();

    applyCascading(node, newSet);

    if (node instanceof ElementStyleNode el) {
      Element domElement = el.getDomNode();

      for (Rule rule : rules) {
        if (!rule.getSelectorObject().test(domElement, null)) {
          continue;
        }

        resolveSetTo(rule.getPropertySet(), newSet, node);
      }

      resolveSetTo(el.getInlineStyleSet(), newSet, node);
    }

    int changes = current.setAll(newSet);

    if (changes != 0) {
      node.getComputedSet().putAll(node.getStyleSet());

      if (updateCallbacks != null) {
        updateCallbacks.styleUpdated(node.getDomNode(), changes);
      }
    }

    if (!(node instanceof ElementStyleNode el)) {
      return;
    }

    for (StyleNode child : el.getChildren()) {
      updateStyle(child);
    }
  }


  private void resolveSetTo(PropertySet source, PropertySet target, StyleNode node) {
    RuleIterator it = source.iterator();

    while (it.hasNext()) {
      it.next();

      Property<Object> property = it.property();
      Value<Object> value = resolveValue(property, it.value(), node);

      if (value == null) {
        target.remove(property);
        continue;
      }

      target.setValue(property, value);
    }
  }

  private <T> Value<T> resolveValue(Property<T> property, Value<T> value, StyleNode node) {
    if (value == null) {
      return null;
    }

    ValueType type = value.getType();
    if (type == ValueType.EXPLICIT || type == ValueType.AUTO) {
      return value;
    }

    if (type == ValueType.INHERIT) {
      ElementStyleNode parent = node.getParent();
      if (parent == null) {
        return null;
      }

      return parent.getStyleSet().get(property);
    }
    if (type == ValueType.UNSET) {
      return Value.create(property.getDefaultValue());
    }

    // type = INITIAL
    if (defaultStyleSheet == null) {
      return null;
    }

    Element el = ((ElementStyleNode) node).getDomNode();

    for (int i = 0; i < defaultStyleSheet.getLength(); i++) {
      Rule r = defaultStyleSheet.getRule(i);
      if (!r.getSelectorObject().test(el, null)) {
        continue;
      }

      Value<T> val = r.getPropertySet().orNull(property);
      if (val == null) {
        continue;
      }

      return val;
    }

    return null;
  }

  public StylePropertiesReadonly getCurrentStyle(Node node) {
    StyleNode style = getStyleNode(node);
    if (style == null) {
      style = createNode(node);
    }

    return style.getCurrentStyle();
  }

  public StyleProperties getInlineStyle(Element element) {
    StyleNode node = getStyleNode(element);
    if (node == null) {
      node = createNode(element);
    }

    return ((ElementStyleNode) node).getInlineApi();
  }

  public StyleNode createNode(Node node) {
    StyleNode n;

    if (node instanceof Element element) {
      n = new ElementStyleNode(element, this);
    } else {
      n = new StyleNode(node, this);
    }

    styleNodes.put(node, n);
    return n;
  }

  public StyleNode getStyleNode(Node node) {
    return styleNodes.get(node);
  }

  public ChimeraSheetBuilder newBuilder() {
    return new ChimeraSheetBuilder(this);
  }

  class InlineMutateListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      if (!event.getKey().equals(Attributes.STYLE)) {
        return;
      }

      Element target = event.getTarget();
      ElementStyleNode node = (ElementStyleNode) getStyleNode(target);

      if (node == null) {
        return;
      }

      node.setInline(event.getNewValue());
    }
  }

  class StyleUpdateListener implements EventListener {

    @Override
    public void onEvent(Event event) {
      StyleNode styleNode = getStyleNode(event.getTarget());
      if (styleNode != null) {
        updateStyle(styleNode);
      }
    }
  }
}
