package com.juliewoolie.delphidom;

import com.google.common.base.Strings;
import com.google.common.xml.XmlEscapers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.Getter;
import com.juliewoolie.chimera.StringUtil;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.selector.Selector;
import com.juliewoolie.delphidom.event.DelegateTarget;
import com.juliewoolie.delphidom.event.EventImpl;
import com.juliewoolie.delphidom.event.EventListenerList;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.NodeFlag;
import com.juliewoolie.dom.Visitor;
import com.juliewoolie.dom.event.Event;
import com.juliewoolie.dom.event.EventPhase;
import com.juliewoolie.dom.style.StyleProperties;
import com.juliewoolie.dom.style.StylePropertiesReadonly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiElement extends DelphiNode implements Element, DelegateTarget {

  @Getter
  final String tagName;
  final List<DelphiNode> children = new ArrayList<>();

  final Map<String, String> attributes = new HashMap<>();

  @Getter
  final EventListenerList listenerList;

  @Getter
  DelphiElement titleNode;

  ClassList classList;
  boolean listUpdatesSupressed = false;

  public DelphiElement(DelphiDocument document, String tagName) {
    super(document);
    this.tagName = tagName;

    this.listenerList = new EventListenerList();
    this.listenerList.setIgnoreCancelled(true);
    this.listenerList.setIgnorePropagationStops(false);
    this.listenerList.setRealTarget(this);
  }

  @Override
  public StylePropertiesReadonly getCurrentStyle() {
    return document.getCurrentStyle(this);
  }

  @Override
  public StyleProperties getInlineStyle() {
    return document.getInlineStyle(this);
  }

  @Override
  public void setAdded(boolean added) {
    super.setAdded(added);

    for (DelphiNode child : children) {
      child.setAdded(added);
    }
  }

  @Override
  protected void setDepth(int depth) {
    super.setDepth(depth);

    for (DelphiNode child : children) {
      child.setDepth(depth + 1);
    }
  }

  @Override
  public @Nullable String getAttribute(String key) {
    return attributes.get(key);
  }

  @Override
  public boolean hasAttribute(String key) {
    if (Strings.isNullOrEmpty(key)) {
      return false;
    }

    return attributes.containsKey(key);
  }

  @Override
  public void setAttribute(@NotNull String key, @Nullable String value) {
    if (Strings.isNullOrEmpty(key)) {
      throw new NullPointerException("Null/empty key");
    }

    String previousValue = getAttribute(key);

    if (Objects.equals(previousValue, value)) {
      return;
    }

    if (value == null) {
      attributes.remove(key);
    } else {
      attributes.put(key, value);
    }

    if (key.equals(Attributes.CLASS) && classList != null && !listUpdatesSupressed) {
      ClassList.copyFromElement(classList);
    }

    document.attributeChanged(this, key, previousValue, value);
  }

  @Override
  public String removeAttribute(String attributeName) {
    if (Strings.isNullOrEmpty(attributeName)) {
      throw new NullPointerException("Null/empty attribute name");
    }

    String value = getAttribute(attributeName);
    setAttribute(attributeName, null);

    return value;
  }

  @Override
  public Set<String> getAttributeNames() {
    return Collections.unmodifiableSet(attributes.keySet());
  }

  @Override
  public Set<Entry<String, String>> getAttributeEntries() {
    return Collections.unmodifiableSet(attributes.entrySet());
  }

  @NotNull
  @Override
  public List<String> getClassList() {
    if (classList != null) {
      return classList;
    }

    classList = new ClassList(this, new ArrayList<>());
    ClassList.copyFromElement(classList);

    return classList;
  }

  @Override
  public DelphiElement getTooltip() {
    return titleNode;
  }

  @Override
  public void setTitleNode(@Nullable Element title) {
    DelphiElement old = this.titleNode;
    this.titleNode = (DelphiElement) title;

    if (old != null) {
      old.parent = null;
      old.removeFlagRecursive(NodeFlag.ADDED);
      old.removeFlagRecursive(NodeFlag.TOOLTIP);
    }

    if (titleNode != null) {
      orphan(titleNode);

      titleNode.setDepth(getDepth() + 1);
      titleNode.parent = this;
      titleNode.addFlagRecursive(NodeFlag.ADDED);
      titleNode.addFlagRecursive(NodeFlag.TOOLTIP);

      document.styles.updateDomStyle(titleNode);
    }

    if (document.view != null) {
      document.view.tooltipChanged(this, old, titleNode);
    }
  }

  private void orphan(DelphiNode node) {
    DelphiElement parent = node.parent;
    if (parent == null) {
      return;
    }

    if (parent.titleNode == node) {
      parent.setTitleNode(null);
    }

    parent.removeChild(node);
  }

  @Override
  public void appendChild(@NotNull Node node) {
    Objects.requireNonNull(node, "Null node");

    DelphiNode dnode = (DelphiNode) node;
    insertAt(dnode, children.size(), 0);
  }

  @Override
  public void prependChild(@NotNull Node node) {
    Objects.requireNonNull(node, "Null node");

    DelphiNode dnode = (DelphiNode) node;
    insertAt(dnode, 0, 0);
  }

  @Override
  public void insertBefore(@NotNull Node node, @NotNull Node before) {
    Objects.requireNonNull(node, "Null node");
    insertAt((DelphiNode) node, getIndex(before, "before"), 0);
  }

  @Override
  public void insertAfter(@NotNull Node node, @NotNull Node after) {
    Objects.requireNonNull(node, "Null node");
    insertAt((DelphiNode) node, getIndex(after, "after"), 1);
  }

  @Override
  public void replaceChild(int idx, @NotNull Node node) {
    Objects.requireNonNull(node, "Null node");
    Objects.checkIndex(idx, children.size());

    removeChild(idx);
    insertAt((DelphiNode) node, idx, 0);
  }

  @Override
  public void replaceChild(@NotNull Node child, @NotNull Node node) {
    Objects.requireNonNull(node, "Null node");
    int idx = getIndex(child, "reference");

    if (idx == -1) {
      return;
    }

    removeChild(idx);
    insertAt((DelphiNode) node, idx, 0);
  }

  private int getIndex(Node node, String name) {
    Objects.requireNonNull(node, "Null " + name + " node");
    Element parent = node.getParent();

    if (Objects.equals(this, parent)) {
      return node.getSiblingIndex();
    }

    return -1;
  }

  private void insertAt(DelphiNode node, int idx, int mod) {
    if (idx == -1 || !canHaveChildren()) {
      return;
    }

    int insertionIndex = idx + mod;

    if (node.getParent() == this && insertionIndex > node.siblingIndex) {
      insertionIndex--;
    }

    if (node.getParent() != null) {
      orphan(node);
    }

    if (!node.getDocument().equals(document)) {
      document.adopt(node);
    }

    node.parent = this;
    node.setDepth(depth + 1);
    node.siblingIndex = insertionIndex;

    if (hasFlag(NodeFlag.ADDED)) {
      node.setAdded(true);
    }

    children.add(insertionIndex, node);

    for (int i = (insertionIndex + 1); i < children.size(); i++) {
      DelphiNode child = children.get(i);
      child.siblingIndex++;
    }

    document.addedChild(this, node, insertionIndex);
  }

  @Override
  public boolean removeChild(@NotNull Node node) {
    Objects.requireNonNull(node, "Null node");
    int idx = indexOf(node);
    if (idx < 0) {
      return false;
    }

    removeChild(idx);
    return true;
  }

  @Override
  public void removeChild(int childIndex) throws IndexOutOfBoundsException {
    Objects.checkIndex(childIndex, children.size());

    DelphiNode node = children.get(childIndex);

    // Remove the child itself from this element, and then call the
    // event listener and finally nullify all the child's values
    //
    // This is so when the event is called, you can still access
    // information about siblings, parents and depth, but you can't
    // find it anymore among its parents children
    //
    // This is good, trust me bro
    //
    children.remove(childIndex);

    document.removingChild(this, node, childIndex);

    node.parent = null;
    node.setDepth(0);
    node.siblingIndex = -1;
    node.setAdded(false);

    for (int i = childIndex; i < children.size(); i++) {
      DelphiNode el = children.get(i);
      el.siblingIndex = i;
    }
  }

  @Override
  public void clearChildren() {
    Node c;

    while ((c = firstChild()) != null) {
      removeChild(c);
    }
  }

  public List<DelphiNode> childList() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public List<Node> getChildren() {
    return Collections.unmodifiableList(children);
  }

  @Override
  public int getChildCount() {
    return children.size();
  }

  @Override
  public boolean hasChildren() {
    return !children.isEmpty();
  }

  @Override
  public boolean canHaveChildren() {
    return true;
  }

  @Override
  public int indexOf(Node node) {
    if (node == null) {
      return -1;
    }

    DelphiNode dnode = (DelphiNode) node;
    if (Objects.equals(this, dnode.parent)) {
      return dnode.siblingIndex;
    }

    return -1;
  }

  @Override
  public boolean hasChild(@Nullable Node node) {
    int index = indexOf(node);
    return index >= 0;
  }

  @Override
  public Node getChild(int index) throws IndexOutOfBoundsException {
    Objects.checkIndex(index, children.size());
    return children.get(index);
  }

  @Override
  public @Nullable Node firstChild() {
    return children.isEmpty() ? null : children.getFirst();
  }

  @Override
  public @Nullable Node lastChild() {
    return children.isEmpty() ? null : children.getLast();
  }

  @Override
  public String getTextContent() {
    StringBuilder builder = new StringBuilder();
    appendTextContent(builder);
    return builder.toString();
  }

  private void appendTextContent(StringBuilder builder) {
    for (DelphiNode child : children) {
      if (child instanceof Text txt) {
        builder.append(txt.getTextContent());
        continue;
      }
      if (child instanceof DelphiElement el) {
        el.appendTextContent(builder);
      }
    }
  }

  @Override
  public void removeMatchingChildren(@NotNull Predicate<Node> predicate) {
    Objects.requireNonNull(predicate, "Null predicate");

    List<DelphiNode> toRemove = new ArrayList<>(children.size());
    for (int i = 0; i < children.size(); i++) {
      DelphiNode dnode = children.get(i);

      if (!predicate.test(dnode)) {
        continue;
      }

      toRemove.add(dnode);
    }

    if (toRemove.isEmpty()) {
      return;
    }

    for (int i = 0; i < toRemove.size(); i++) {
      removeChild(toRemove.get(i));
    }
  }

  @Override
  public void setTextContent(String content) {
    if (!canHaveChildren()) {
      return;
    }

    Text text = null;

    for (int i = 0; i < children.size(); i++) {
      if (!(children.get(i) instanceof Text txt)) {
        continue;
      }
      text = txt;
    }

    if (text == null) {
      clearChildren();
      appendText(content);
      return;
    }

    if (children.size() != 1) {
      Text finalText = text;
      removeMatchingChildren(node -> node != finalText);
    }

    text.setTextContent(content);
  }

  @Override
  public void forEachDescendant(@NotNull Consumer<Node> consumer) {
    Objects.requireNonNull(consumer, "Null consumer");
    if (children.isEmpty()) {
      return;
    }

    for (DelphiNode node : children) {
      consumer.accept(node);

      if (node instanceof DelphiElement el) {
        el.forEachDescendant(consumer);
      }
    }
  }

  @Override
  public @NotNull List<Element> getElementsByTagName(@NotNull String tagName) {
    Objects.requireNonNull(tagName, "Null tag name");

    List<Element> elementList = new ArrayList<>();

    collectDescendants(elementList, el -> el.tagName.equals(tagName));
    return elementList;
  }

  void collectDescendants(List<Element> out, Predicate<DelphiElement> filter) {
    if (filter.test(this)) {
      out.addLast(this);
    }

    if (children.isEmpty()) {
      return;
    }

    for (DelphiNode child : children) {
      if (!(child instanceof DelphiElement el)) {
        continue;
      }

      el.collectDescendants(out, filter);
    }
  }

  @Override
  public @NotNull List<Element> getElementsByClassName(@NotNull String className) {
    Objects.requireNonNull(className, "Null class name");

    List<Element> elementList = new ArrayList<>();

    collectDescendants(elementList, el -> {
      String classList = el.getAttribute(Attributes.CLASS);

      if (Strings.isNullOrEmpty(classList)) {
        return false;
      }

      return StringUtil.containsWord(classList, className);
    });

    return elementList;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public @NotNull List<Element> querySelectorAll(@NotNull String query) {
    Selector selector = Chimera.parseSelector(query);
    List<Element> elementList = new ArrayList<>();

    collectDescendants(elementList, selector::test);

    return elementList;
  }

  @Override
  public @Nullable DelphiElement querySelector(@NotNull String query) {
    Selector selector = Chimera.parseSelector(query);
    return matchFirst(this, selector);
  }

  @Override
  public boolean matches(String selector) {
    Selector compiled = Chimera.parseSelector(selector);
    return compiled.test(this);
  }

  public DelphiElement matchFirst(DelphiElement root, Selector group) {
    if (group.test(this)) {
      return this;
    }

    for (DelphiNode child : children) {
      if (!(child instanceof DelphiElement el)) {
        continue;
      }

      DelphiElement result = el.matchFirst(root, group);

      if (result != null) {
        return result;
      }
    }

    return null;
  }

  @Override
  public void dispatchEvent(Event event) {
    this.listenerList.validateEventCall(event);

    EventImpl impl = (EventImpl) event;
    impl.setPhase(EventPhase.ORIGIN);

    this.listenerList.dispatchEvent(event);

    if (!hasFlag(NodeFlag.ADDED)) {
      return;
    }

    if (!event.isPropagationStopped() && event.isBubbling()) {
      DelphiElement p = parent;
      impl.setPhase(EventPhase.BUBBLING);

      while (p != null) {
        p.listenerList.dispatchEvent(event);
        p = p.parent;

        if (event.isPropagationStopped()) {
          break;
        }
      }
    }

    document.dispatchGlobalEvent(event);
  }

  @Override
  public boolean isDescendant(@Nullable Node node) {
    if (!canHaveChildren() || node == null) {
      return false;
    }

    Node p = node.getParent();
    while (p != null) {
      if (Objects.equals(p, this)) {
        return true;
      }
      p = p.getParent();
    }

    return false;
  }

  @Override
  public void enterVisitor(Visitor visitor) {
    visitor.enterElement(this);
  }

  @Override
  public void exitVisitor(Visitor visitor) {
    visitor.exitElement(this);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<")
        .append(tagName);

    attributes.forEach((s, s2) -> {
      builder
          .append(' ')
          .append(s)
          .append('=')
          .append('"')
          .append(XmlEscapers.xmlAttributeEscaper().escape(s2))
          .append('"');
    });

    builder.append("/>");

    return builder.toString();
  }

  void classListChanged() {
    listUpdatesSupressed = true;
    try {
      StringJoiner joiner = new StringJoiner(" ");
      for (String s : classList.strings) {
        joiner.add(s);
      }
      setAttribute(Attributes.CLASS, joiner.toString());
    } finally {
      listUpdatesSupressed = false;
    }
  }

  @Override
  public void addFlagRecursive(NodeFlag nodeFlag) {
    super.addFlagRecursive(nodeFlag);
    for (DelphiNode child : children) {
      child.addFlagRecursive(nodeFlag);
    }
  }

  @Override
  public void removeFlagRecursive(NodeFlag nodeFlag) {
    super.removeFlagRecursive(nodeFlag);
    for (DelphiNode child : children) {
      child.removeFlagRecursive(nodeFlag);
    }
  }
}
