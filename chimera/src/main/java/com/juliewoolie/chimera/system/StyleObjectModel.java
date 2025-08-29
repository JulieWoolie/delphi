package com.juliewoolie.chimera.system;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.chimera.ChimeraSheetBuilder;
import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.Rule;
import com.juliewoolie.chimera.StyleUpdateCallbacks;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.event.AttributeMutateEvent;
import com.juliewoolie.dom.event.Event;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTarget;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.MutationEvent;
import com.juliewoolie.dom.style.StyleProperties;
import com.juliewoolie.dom.style.StylePropertiesReadonly;

@Getter
public class StyleObjectModel {

  private final Document document;

  final List<ChimeraStylesheet> sheets = new ArrayList<>();
  final List<Rule> rules = new ArrayList<>();

  private final Map<Node, StyleNode> styleNodes = new HashMap<>();
  private ElementStyleNode rootNode;

  final Map<String, Object> variables = new HashMap<>();

  @Setter
  ChimeraStylesheet defaultStyleSheet;

  @Setter
  StyleUpdateCallbacks updateCallbacks;

  public StyleObjectModel(Document document) {
    this.document = document;
  }

  public void initialize() {
    EventTarget l = document.getGlobalTarget();

    // Listen to changes in the DOM tree
    DomMutationListener domListener = new DomMutationListener();
    l.addEventListener(EventTypes.APPEND_CHILD, domListener);
    l.addEventListener(EventTypes.REMOVE_CHILD, domListener);

    // Listen to mutations of the 'style' attribute
    // This has to be after the dom mutation listeners, otherwise
    // the style nodes won't exist when the initial inline style
    // is loaded
    l.addEventListener(EventTypes.MODIFY_ATTR, new InlineMutateListener());

    // Listen to any changes that may affect element styling
    StyleUpdateListener updateListener = new StyleUpdateListener();
    l.addEventListener(EventTypes.APPEND_CHILD, updateListener);
    l.addEventListener(EventTypes.REMOVE_CHILD, updateListener);

    l.addEventListener(EventTypes.MODIFY_ATTR, updateListener);

    l.addEventListener(EventTypes.CLICK_EXPIRE, updateListener);
    l.addEventListener(EventTypes.CLICK, updateListener);

    l.addEventListener(EventTypes.INPUT, updateListener);

    if (document.getDocumentElement() != null) {
      initRoot(document.getDocumentElement());
    }
  }

  public void addStylesheet(ChimeraStylesheet stylesheet) {
    sheets.addLast(stylesheet);

    for (int i = 0; i < stylesheet.getLength(); i++) {
      Rule r = stylesheet.getRule(i);
      rules.addLast(r);
    }

    rules.sort(Comparator.naturalOrder());

    if (rootNode != null) {
      rootNode.updateStyle();
    }
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
    StyleNode n = getStyleNode(node);

    if (n != null) {
      return n;
    }

    if (node instanceof Element element) {
      ElementStyleNode elNode = new ElementStyleNode(element, this);
      n = elNode;

      for (Node child : element.getChildren()) {
        elNode.addChild(createNode(child), elNode.getChildren().size());
      }
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

  public void updateFromRoot() {
    if (rootNode == null) {
      return;
    }
    rootNode.updateStyle();
  }

  public void updateDomStyle(Node domNode) {
    StyleNode node = getStyleNode(domNode);
    if (node == null) {
      return;
    }

    node.updateStyle();
  }

  public void replaceStylesheet(ChimeraStylesheet old, ChimeraStylesheet stylesheet) {
    if (old != null) {
      sheets.remove(old);
      for (int i = 0; i < old.getLength(); i++) {
        Rule r = old.getRule(i);
        rules.remove(r);
      }
    }

    addStylesheet(stylesheet);
  }

  public void removeStylesheet(ChimeraStylesheet stylesheet) {
    sheets.remove(stylesheet);
    for (int i = 0; i < stylesheet.getLength(); i++) {
      Rule r = stylesheet.getRule(i);
      rules.remove(r);
    }

    if (rootNode != null) {
      rootNode.updateStyle();
    }
  }

  public void removeNode(Node node) {
    StyleNode remove = styleNodes.remove(node);
    if (remove == null) {
      return;
    }

    ElementStyleNode parent = remove.parent;
    if (parent != null) {
      parent.removeChild(remove);
    }

    if (node instanceof Element el) {
      for (Node child : el.getChildren()) {
        removeNode(child);
      }
    }
  }

  public void initRoot(Element root) {
    rootNode = (ElementStyleNode) createNode(root);
  }

  class DomMutationListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      ElementStyleNode parentNode;

      if (event.getTarget() != null) {
        parentNode = (ElementStyleNode) getStyleNode(event.getTarget());

        if (parentNode == null) {
          return;
        }
      } else {
        parentNode = null;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        StyleNode childNode = createNode(event.getNode());

        if (parentNode != null) {
          parentNode.addChild(childNode, event.getMutationIndex());
        } else {
          rootNode = (ElementStyleNode) childNode;
        }
        return;
      }

      // type = remove child
      if (parentNode != null) {
        removeNode(event.getNode());
      }
    }
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
        styleNode.updateStyle();
      }
    }
  }
}
