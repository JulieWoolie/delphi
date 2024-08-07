package net.arcadiusmc.delphidom;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphidom.event.AttributeMutation;
import net.arcadiusmc.delphidom.event.EventImpl;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphidom.event.Mutation;
import net.arcadiusmc.delphidom.parser.ErrorListener;
import net.arcadiusmc.delphidom.scss.DocumentSheetBuilder;
import net.arcadiusmc.delphidom.scss.DocumentStyles;
import net.arcadiusmc.delphidom.scss.Sheet;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.ComponentNode;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.event.AttributeAction;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventPhase;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MutationEvent;
import net.arcadiusmc.dom.style.Stylesheet;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DelphiDocument implements Document {

  private static final Logger LOGGER = Loggers.getDocumentLogger();
  public static final ErrorListener ERROR_LISTENER = ErrorListener.logging(LOGGER);

  final Map<String, String> options = new HashMap<>();
  final Map<String, DelphiElement> idLookup = new HashMap<>();

  @Getter
  final EventListenerList globalTarget;
  final EventListenerList documentListeners;

  @Getter
  DelphiElement body;

  public DelphiElement hovered;
  public DelphiElement clicked;

  @Getter @Setter
  ExtendedView view;

  @Getter
  final DocumentStyles styles;

  public DelphiDocument() {
    this.globalTarget = new EventListenerList();
    this.globalTarget.setRealTarget(this.globalTarget);
    this.globalTarget.setIgnorePropagationStops(true);
    this.globalTarget.setIgnoreCancelled(false);

    this.documentListeners = new EventListenerList();
    this.documentListeners.setRealTarget(this);
    this.documentListeners.setIgnorePropagationStops(false);
    this.documentListeners.setIgnoreCancelled(true);

    IdAttrListener attrListener = new IdAttrListener();
    IdMutationListener mutationListener = new IdMutationListener();
    globalTarget.addEventListener(EventTypes.MODIFY_ATTR, attrListener);
    globalTarget.addEventListener(EventTypes.APPEND_CHILD, mutationListener);
    globalTarget.addEventListener(EventTypes.REMOVE_CHILD, mutationListener);

    this.styles = new DocumentStyles(this);
    this.styles.init();
  }

  public void setBody(DelphiElement body) {
    if (this.body == body) {
      return;
    }

    if (this.body != null) {
      this.body.setAdded(false);
      this.body.removeFlag(NodeFlag.ROOT);
    }

    this.body = body;

    if (body != null) {
      body.addFlag(NodeFlag.ROOT);
      body.setAdded(true);
    }
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
  public DelphiElement createElement(@NotNull String tagName) {
    Objects.requireNonNull(tagName, "Null tag name");

    return switch (tagName) {
      case TagNames.ITEM -> new DelphiItemElement(this);
      default -> new DelphiElement(this, tagName);
    };
  }

  @Override
  public Text createText() {
    return new Text(this);
  }

  @Override
  public Text createText(@Nullable String content) {
    Text t = new Text(this);
    t.setTextContent(content);
    return t;
  }

  @Override
  public ComponentNode createComponent() {
    return new ChatNode(this);
  }

  @Override
  public ComponentNode createComponent(Component component) {
    ChatNode n = new ChatNode(this);
    n.setContent(component);
    return n;
  }

  @Override
  public @Nullable DelphiElement getActiveElement() {
    return clicked;
  }

  @Override
  public @Nullable DelphiElement getHoveredElement() {
    return hovered;
  }

  @Override
  public @Nullable DelphiElement getElementById(String elementId) {
    return idLookup.get(elementId);
  }

  @Override
  public void adopt(@NotNull Node node) {
    DelphiNode dnode = (DelphiNode) node;

    if (Objects.equals(dnode.document, this)) {
      return;
    }

    if (dnode.parent != null && !dnode.parent.document.equals(this)) {
      dnode.parent.removeChild(dnode);
    }

    dnode.document = this;

    if (node instanceof DelphiElement element) {
      for (DelphiNode delphiNode : element.childList()) {
        adopt(delphiNode);
      }
    }
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

  void addedChild(DelphiElement el, DelphiNode child, int idx) {
    Mutation mutation = new Mutation(EventTypes.APPEND_CHILD, this);
    mutation.initEvent(el, false, false, child, idx);

    el.dispatchEvent(mutation);
  }

  void removingChild(DelphiElement el, DelphiNode node, int idx) {
    Mutation mutation = new Mutation(EventTypes.REMOVE_CHILD, this);
    mutation.initEvent(el, false, false, node, idx);

    el.dispatchEvent(mutation);
  }

  ErrorListener getErrorListener() {
    return ERROR_LISTENER;
  }

  void inlineStyleError(ParserException exception) {
    LOGGER.error("Fatal error parsing inline style", exception);
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

  @Override
  public void addStylesheet(@NotNull Stylesheet stylesheet) {
    Objects.requireNonNull(stylesheet, "Null style sheet");
    styles.addSheet((Sheet) stylesheet);
  }

  @Override
  public @NotNull List<Stylesheet> getStylesheets() {
    return Collections.unmodifiableList(styles.stylesheets);
  }

  @Override
  public @NotNull DocumentSheetBuilder createStylesheet() {
    return new DocumentSheetBuilder(this);
  }

  void updateId(DelphiElement element, String previousId, String newId) {
    if (!Strings.isNullOrEmpty(previousId)) {
      Element referenced = idLookup.get(previousId);

      if (Objects.equals(referenced, element)) {
        idLookup.remove(previousId);
      }
    }

    if (Strings.isNullOrEmpty(newId)) {
      return;
    }

    // Do not override existing
    DelphiElement existingValue = idLookup.get(newId);
    if (existingValue != null) {
      return;
    }

    idLookup.put(newId, element);
  }

  class IdMutationListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      Node node = event.getNode();
      if (!(node instanceof DelphiElement element)) {
        return;
      }

      String type = event.getType();

      if (type.equals(EventTypes.APPEND_CHILD)) {
        updateId(element, null, element.getId());
      } else if (type.equals(EventTypes.REMOVE_CHILD)) {
        updateId(element, element.getId(), null);
      }
    }
  }

  class IdAttrListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      if (!Objects.equals(event.getKey(), Attributes.ID)) {
        return;
      }

      String previousId = event.getPreviousValue();
      String newId = event.getNewValue();
      DelphiElement element = (DelphiElement) event.getTarget();

      updateId(element, previousId, newId);
    }
  }
}
