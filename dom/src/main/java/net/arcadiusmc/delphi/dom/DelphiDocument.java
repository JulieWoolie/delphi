package net.arcadiusmc.delphi.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.dom.event.AttributeMutation;
import net.arcadiusmc.delphi.dom.event.EventImpl;
import net.arcadiusmc.delphi.dom.event.EventListenerList;
import net.arcadiusmc.delphi.dom.event.Mutation;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.TextNode;
import net.arcadiusmc.dom.event.AttributeAction;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventPhase;
import net.arcadiusmc.dom.event.EventTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiDocument implements Document {

  final Map<String, String> options = new HashMap<>();
  final Map<String, DelphiElement> idLookup = new HashMap<>();

  @Getter
  final EventListenerList globalTarget;
  final EventListenerList documentListeners;

  @Getter
  DelphiElement body;

  DelphiElement hoveredNode;

  DelphiElement clickedNode;
  int clickTicks = 0;

  @Getter @Setter
  DocumentView view;

  public DelphiDocument() {
    this.globalTarget = new EventListenerList();
    this.globalTarget.setRealTarget(this);
    this.globalTarget.setIgnorePropagationStops(true);
    this.globalTarget.setIgnoreCancelled(false);

    this.documentListeners = new EventListenerList();
    this.documentListeners.setRealTarget(this);
    this.documentListeners.setIgnorePropagationStops(false);
    this.documentListeners.setIgnoreCancelled(true);
  }

  @Override
  public @Nullable String getOption(String optionKey) {
    return options.get(optionKey);
  }

  @Override
  public void setOption(@NotNull String optionKey, @Nullable String value) {
    Objects.requireNonNull(optionKey, "Null option key");

    String prevValue = getOption(optionKey);

    if (Objects.equals(prevValue, value)) {
      return;
    }

    if (value == null) {
      options.remove(optionKey);
    } else {
      options.put(optionKey, value);
    }

    optionChanged(optionKey, prevValue, value);
  }

  @Override
  public void removeOption(@NotNull String optionKey) {
    Objects.requireNonNull(optionKey, "Null option key");
    setOption(optionKey, null);
  }

  @Override
  public Set<String> getOptionKeys() {
    return Collections.unmodifiableSet(options.keySet());
  }

  @Override
  public Element createElement(@NotNull String tagName) {
    return new DelphiElement(this, tagName);
  }

  @Override
  public TextNode createText() {
    return new Text(this);
  }

  @Override
  public TextNode createText(@Nullable String content) {
    Text t = new Text(this);
    t.setTextContent(content);
    return t;
  }

  @Override
  public @Nullable Element getActiveElement() {
    return clickedNode;
  }

  @Override
  public @Nullable Element getHoveredElement() {
    return hoveredNode;
  }

  @Override
  public @Nullable Element getElementById(String elementId) {
    return idLookup.get(elementId);
  }

  @Override
  public void adopt(@NotNull Node node) {
    DelphiNode dnode = (DelphiNode) node;

    if (Objects.equals(dnode.owningDocument, this)) {
      return;
    }

    if (dnode.parent != null) {
      dnode.parent.removeChild(dnode);
    }

    dnode.owningDocument = this;
  }

  @Override
  public void realign() {
    // TODO
    throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
  }

  @Override
  public @NotNull List<Element> getElementsByTagName(@NotNull String tagName) {
    if (body == null) {
      return new ArrayList<>();
    }
    return body.getElementsByTagName(tagName);
  }

  @Override
  public @NotNull List<Element> getElementsByClassName(@NotNull String className) {
    if (body == null) {
      return new ArrayList<>();
    }
    return body.getElementsByClassName(className);
  }

  @Override
  public @NotNull List<Element> querySelectorAll(@NotNull String query) throws ParserException {
    if (body == null) {
      return new ArrayList<>();
    }
    return body.querySelectorAll(query);
  }

  @Override
  public @Nullable Element querySelector(@NotNull String query) throws ParserException {
    if (body == null) {
      return null;
    }

    return body.querySelector(query);
  }

  void optionChanged(String key, String prevValue, String newValue) {
    AttributeMutation mutation = new AttributeMutation(EventTypes.MODIFY_OPTION, this);
    AttributeAction act = deriveAction(prevValue, newValue);

    mutation.initEvent(null, false, false, key, prevValue, newValue, act);

    dispatchEvent(mutation);
  }

  void attributeChanged(DelphiElement el, String key, String prevValue, String newValue) {
    AttributeMutation mutation = new AttributeMutation(EventTypes.MODIFY_ATTR, this);
    AttributeAction act = deriveAction(prevValue, newValue);

    mutation.initEvent(el, false, false, key, prevValue, newValue, act);

    el.dispatchEvent(mutation);
  }

  AttributeAction deriveAction(String prev, String newValue) {
    if (prev == null) {
      return AttributeAction.ADD;
    } else if (newValue == null) {
      return AttributeAction.REMOVE;
    }

    return AttributeAction.SET;
  }

  void titleNodeChanged(DelphiElement el, DelphiNode old, DelphiNode newNode) {

  }

  void textChanged(Text text, String textContent) {

  }

  void childAdded(DelphiElement el, DelphiNode child) {
    Mutation mutation = new Mutation(EventTypes.APPEND_CHILD, this);
    mutation.initEvent(el, false, false, child);

    el.dispatchEvent(mutation);
  }

  void childRemoving(DelphiElement el, DelphiNode node) {
    Mutation mutation = new Mutation(EventTypes.REMOVE_CHILD, this);
    mutation.initEvent(el, false, false, node);

    el.dispatchEvent(mutation);
  }

  @Override
  public void addEventListener(String eventType, EventListener listener) {
    documentListeners.addEventListener(eventType, listener);
  }

  @Override
  public boolean removeEventListener(String eventType, EventListener listener) {
    return documentListeners.removeEventListener(eventType, listener);
  }

  @Override
  public void dispatchEvent(Event event) {
    documentListeners.dispatchEvent(event);
    dispatchGlobalEvent(event);
  }

  public void dispatchGlobalEvent(Event event) {
    EventImpl impl = (EventImpl) event;
    impl.setPhase(EventPhase.GLOBAL);
    globalTarget.dispatchEvent(event);
  }

  /* --------------------------- Inputs ---------------------------- */

}
