package net.arcadiusmc.delphi.dom;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import lombok.Getter;
import net.arcadiusmc.delphi.dom.event.EventImpl;
import net.arcadiusmc.delphi.dom.event.EventListenerList;
import net.arcadiusmc.delphi.dom.selector.Selector;
import net.arcadiusmc.delphi.parser.Parser;
import net.arcadiusmc.delphi.parser.ParserErrors;
import net.arcadiusmc.delphi.parser.ParserErrors.Error;
import net.arcadiusmc.delphi.parser.StringUtil;
import net.arcadiusmc.dom.Attr;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.ParserException;
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

  final EventListenerList listenerList;

  @Getter
  DelphiNode titleNode;

  private int flags = 0;

  public DelphiElement(DelphiDocument document, String tagName) {
    super(document);
    this.tagName = tagName;

    this.listenerList = new EventListenerList();
    this.listenerList.setIgnoreCancelled(true);
    this.listenerList.setIgnorePropagationStops(false);
    this.listenerList.setRealTarget(this);
  }

  public boolean hasFlag(NodeFlag flag) {
    return (flags & flag.mask) == flag.mask;
  }

  public void addFlag(NodeFlag flag) {
    this.flags |= flag.mask;
  }

  public void removeFlag(NodeFlag flag) {
    this.flags &= ~flag.mask;
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

    owningDocument.attributeChanged(this, key, previousValue, value);
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
  public @Nullable Node getTooltip() {
    return titleNode;
  }

  @Override
  public void setTitleNode(@Nullable Node title) {
    DelphiNode old = this.titleNode;
    this.titleNode = (DelphiNode) title;

    owningDocument.titleNodeChanged(this, old, this.titleNode);
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
    if (idx == -1) {
      return;
    }

    int insertionIndex = idx + mod;

    if (node.getParent() != null) {
      node.getParent().removeChild(node);
    }

    if (!node.getOwningDocument().equals(owningDocument)) {
      owningDocument.adopt(node);
    }

    node.parent = this;
    node.setDepth(depth + 1);
    node.siblingIndex = insertionIndex;

    children.add(insertionIndex, node);

    for (int i = (insertionIndex + 1); i < children.size(); i++) {
      DelphiNode child = children.get(i);
      child.siblingIndex++;
    }

    owningDocument.childAdded(this, node);
  }

  @Override
  public boolean removeChild(@NotNull Node node) {
    Objects.requireNonNull(node, "Null node");
    Element parent = node.getParent();

    if (!Objects.equals(this, parent)) {
      return false;
    }

    removeChild(node.getSiblingIndex());
    return true;
  }

  @Override
  public void removeChild(int childIndex) throws IndexOutOfBoundsException {
    Objects.checkIndex(childIndex, children.size());

    DelphiNode node = children.get(childIndex);

    owningDocument.childRemoving(this, node);

    node.parent = null;
    node.setDepth(0);
    node.siblingIndex = -1;

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
  public @Nullable Node firstChild() {
    return children.isEmpty() ? null : children.getFirst();
  }

  @Override
  public @Nullable Node lastChild() {
    return children.isEmpty() ? null : children.getLast();
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
      String classList = el.getAttribute(Attr.CLASS);
      return StringUtil.containsWord(classList, className);
    });

    return elementList;
  }

  private Selector parseSelector(String query) {
    StringBuffer buf = new StringBuffer(query);
    Parser parser = new Parser(buf);
    ParserErrors errors = parser.getErrors();

    Selector selector = parser.selector();

    if (errors.isErrorPresent()) {
      StringBuilder builder = new StringBuilder();
      builder.append("Error(s) during selector parsing");

      for (Error error : errors.getErrors()) {
        builder.append('\n')
            .append('[')
            .append(error.level())
            .append("] ")
            .append(error.message());
      }

      throw new ParserException(builder.toString());
    }

    return selector;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public @NotNull List<Element> querySelectorAll(@NotNull String query) {
    Selector selector = parseSelector(query);
    return (List) selector.selectAll(this);
  }

  @Override
  public @Nullable Element querySelector(@NotNull String query) {
    Selector selector = parseSelector(query);
    return selector.selectOne(this);
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

    owningDocument.dispatchGlobalEvent(event);
  }
}
