package net.arcadiusmc.delphidom;

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
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.Getter;
import net.arcadiusmc.delphidom.event.EventImpl;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphidom.parser.StringUtil;
import net.arcadiusmc.delphidom.scss.InlineStyle;
import net.arcadiusmc.delphidom.scss.PropertySet;
import net.arcadiusmc.delphidom.scss.ReadonlyMap;
import net.arcadiusmc.delphidom.scss.ScssParser;
import net.arcadiusmc.delphidom.selector.SelectorGroup;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.NodeFlag;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.Visitor;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventPhase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiElement extends DelphiNode implements Element {

  @Getter
  final String tagName;
  final List<DelphiNode> children = new ArrayList<>();

  final Map<String, String> attributes = new HashMap<>();

  @Getter
  final EventListenerList listenerList;

  public final ReadonlyMap styleApi;

  @Getter
  public final InlineStyle inlineStyle;
  public boolean inlineUpdatesSuppressed = false;

  @Getter
  DelphiNode titleNode;

  public DelphiElement(DelphiDocument document, String tagName) {
    super(document);
    this.tagName = tagName;

    this.listenerList = new EventListenerList();
    this.listenerList.setIgnoreCancelled(true);
    this.listenerList.setIgnorePropagationStops(false);
    this.listenerList.setRealTarget(this);

    this.styleApi = new ReadonlyMap(styleSet);
    this.inlineStyle = new InlineStyle(new PropertySet(), this);
  }

  @Override
  public ReadonlyMap getCurrentStyle() {
    return styleApi;
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

    if (key.equals(Attributes.STYLE) && !inlineUpdatesSuppressed) {
      updateInlineStyle(value);
    }

    document.attributeChanged(this, key, previousValue, value);
  }

  void updateInlineStyle(String str) {
    inlineStyle.getBacking().clear();

    if (Strings.isNullOrEmpty(str)) {
      return;
    }

    ScssParser parser = new ScssParser(new StringBuffer(str));

    if (document.getView() != null) {
      parser.setVariables(document.getView().getStyleVariables());
    }

    parser.getErrors().setListener(document.getErrorListener());

    try {
      parser.inlineRules(inlineStyle.getBacking());
    } catch (ParserException exc) {
      document.inlineStyleError(exc);
    }
  }

  @Override
  public Set<String> getAttributeNames() {
    return Collections.unmodifiableSet(attributes.keySet());
  }

  @Override
  public Set<Entry<String, String>> getAttributeEntries() {
    return Collections.unmodifiableSet(attributes.entrySet());
  }

  @Override
  public DelphiNode getTooltip() {
    return titleNode;
  }

  @Override
  public void setTitleNode(@Nullable Node title) {
    DelphiNode old = this.titleNode;
    this.titleNode = (DelphiNode) title;

    if (titleNode != null) {
      document.styles.updateStyles(titleNode);
    }

    if (document.view != null) {
      document.view.titleChanged(this, old, titleNode);
    }
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
      node.getParent().removeChild(node);
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

    document.removingChild(this, node, childIndex);

    node.parent = null;
    node.setDepth(0);
    node.siblingIndex = -1;
    node.setAdded(false);

    children.remove(childIndex);
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
    SelectorGroup selector = SelectorGroup.parse(query);
    List<Element> elementList = new ArrayList<>();

    collectDescendants(elementList, element -> selector.test(this, element));

    return elementList;
  }

  @Override
  public @Nullable Element querySelector(@NotNull String query) {
    SelectorGroup selector = SelectorGroup.parse(query);
    return matchFirst(this, selector);
  }

  private DelphiElement matchFirst(DelphiElement root, SelectorGroup group) {
    if (group.test(root, this)) {
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
  public void addEventListener(String eventType, EventListener listener) {
    this.listenerList.addEventListener(eventType, listener);
  }

  @Override
  public boolean removeEventListener(String eventType, EventListener listener) {
    return this.listenerList.removeEventListener(eventType, listener);
  }

  @Override
  public void dispatchEvent(Event event) {
    this.listenerList.validateEventCall(event);

    if (!hasFlag(NodeFlag.ADDED)) {
      return;
    }

    EventImpl impl = (EventImpl) event;
    impl.setPhase(EventPhase.ORIGIN);

    this.listenerList.dispatchEvent(event);

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
}
