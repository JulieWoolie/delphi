package net.arcadiusmc.delphi.dom;

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
import net.arcadiusmc.delphi.ExtendedView;
import net.arcadiusmc.delphi.Loggers;
import net.arcadiusmc.delphi.dom.event.AttributeMutation;
import net.arcadiusmc.delphi.dom.event.EventImpl;
import net.arcadiusmc.delphi.dom.event.EventListenerList;
import net.arcadiusmc.delphi.dom.event.Mutation;
import net.arcadiusmc.delphi.dom.scss.DocumentStyles;
import net.arcadiusmc.delphi.dom.scss.Sheet;
import net.arcadiusmc.delphi.parser.ErrorListener;
import net.arcadiusmc.dom.Attr;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.event.AttributeAction;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventPhase;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.style.Stylesheet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DelphiDocument implements Document {

  private static final Logger LOGGER = Loggers.getLogger("Document");
  public static final ErrorListener ERROR_LISTENER = ErrorListener.logging(LOGGER);

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
  ExtendedView view;

  final DocumentStyles styles;

  public DelphiDocument() {
    this.globalTarget = new EventListenerList();
    this.globalTarget.setRealTarget(this);
    this.globalTarget.setIgnorePropagationStops(true);
    this.globalTarget.setIgnoreCancelled(false);

    this.documentListeners = new EventListenerList();
    this.documentListeners.setRealTarget(this);
    this.documentListeners.setIgnorePropagationStops(false);
    this.documentListeners.setIgnoreCancelled(true);

    globalTarget.addEventListener(EventTypes.MODIFY_ATTR, new IdListener());

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
    return new DelphiElement(this, tagName);
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
  public @Nullable DelphiElement getActiveElement() {
    return clickedNode;
  }

  @Override
  public @Nullable DelphiElement getHoveredElement() {
    return hoveredNode;
  }

  @Override
  public @Nullable DelphiElement getElementById(String elementId) {
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
    if (view == null) {
      return;
    }

    view.textChanged(text);
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

  /* --------------------------- Inputs ---------------------------- */

  class IdListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      if (event.getAction() != AttributeAction.REMOVE && event.getAction() != AttributeAction.SET) {
        return;
      }
      if (!Objects.equals(event.getKey(), Attr.ID)) {
        return;
      }

      String previousId = event.getPreviousValue();
      String newId = event.getNewValue();
      DelphiElement element = (DelphiElement) event.getTarget();

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
  }
}
