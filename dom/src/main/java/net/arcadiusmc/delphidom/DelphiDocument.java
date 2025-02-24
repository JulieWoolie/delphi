package net.arcadiusmc.delphidom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import net.arcadiusmc.chimera.ChimeraSheetBuilder;
import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.system.StyleObjectModel;
import net.arcadiusmc.delphidom.event.AttributeMutation;
import net.arcadiusmc.delphidom.event.EventImpl;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.delphidom.event.Mutation;
import net.arcadiusmc.delphidom.parser.ErrorListener;
import net.arcadiusmc.delphidom.system.ComponentElementSystem;
import net.arcadiusmc.delphidom.system.IdSystem;
import net.arcadiusmc.delphidom.system.ItemElementSystem;
import net.arcadiusmc.delphidom.system.ObjectModelSystem;
import net.arcadiusmc.delphidom.system.OptionElementSystem;
import net.arcadiusmc.delphidom.system.StyleElementSystem;
import net.arcadiusmc.dom.ComponentElement;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.ItemElement;
import net.arcadiusmc.dom.Node;
import net.arcadiusmc.dom.NodeFlag;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.event.AttributeAction;
import net.arcadiusmc.dom.event.Event;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventPhase;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.style.StyleProperties;
import net.arcadiusmc.dom.style.StylePropertiesReadonly;
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

  @Getter
  final EventListenerList documentListeners;

  @Getter
  DelphiDocumentElement documentElement;

  public DelphiElement hovered;
  public DelphiElement clicked;

  @Getter
  ExtendedView view;

  @Getter
  final StyleObjectModel styles;
  final ObjectModelSystem[] systems = new ObjectModelSystem[10];

  public DelphiDocument() {
    this.globalTarget = new EventListenerList();
    this.globalTarget.setRealTarget(this.globalTarget);
    this.globalTarget.setIgnorePropagationStops(true);
    this.globalTarget.setIgnoreCancelled(false);

    this.documentListeners = new EventListenerList();
    this.documentListeners.setRealTarget(this);
    this.documentListeners.setIgnorePropagationStops(false);
    this.documentListeners.setIgnoreCancelled(true);

    styles = new StyleObjectModel(this);
    styles.initialize();

    addSystem(new IdSystem());
    addSystem(new OptionElementSystem());
    addSystem(new StyleElementSystem());
    addSystem(new ItemElementSystem());
    addSystem(new ComponentElementSystem());
  }

  public static DelphiDocument createEmpty() {
    DelphiDocument doc = new DelphiDocument();

    DelphiDocumentElement root = new DelphiDocumentElement(doc);
    root.appendElement(TagNames.HEAD);
    root.appendElement(TagNames.BODY);

    doc.setRoot(root);

    return doc;
  }

  public void addSystem(ObjectModelSystem system) {
    for (int i = 0; i < systems.length; i++) {
      ObjectModelSystem system1 = systems[i];
      if (system1 != null) {
        continue;
      }

      systems[i] = system;

      system.onAttach(this);
      if (view != null) {
        system.onViewAttach(view);
      }

      return;
    }

    throw new RuntimeException("Tried to add more than " + systems.length + " systems to document");
  }

  public void removeSystem(ObjectModelSystem system) {
    for (int i = 0; i < systems.length; i++) {
      ObjectModelSystem sys = systems[i];
      if (!Objects.equals(sys, system)) {
        continue;
      }

      sys.onViewDetach();
      sys.onDetach();

      systems[i] = null;
      return;
    }
  }

  public <T> T getSystem(Class<T> type) {
    for (ObjectModelSystem system : systems) {
      if (system == null) {
        continue;
      }
      if (type.isInstance(system)) {
        return type.cast(system);
      }
    }
    return null;
  }

  public void shutdownSystems() {
    for (ObjectModelSystem system : systems) {
      if (system == null) {
        continue;
      }

      system.onViewDetach();
      system.onDetach();
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

    tagName = tagName.toLowerCase();

    return switch (tagName) {
      case TagNames.ITEM -> new DelphiItemElement(this);
      case TagNames.BUTTON -> new DelphiButtonElement(this);
      case TagNames.COMPONENT -> new ChatElement(this);
      case TagNames.BODY -> new DelphiBodyElement(this);
      case TagNames.HEAD -> new DelphiHeadElement(this);
      case TagNames.OPTION -> new DelphiOptionElement(this);
      case TagNames.STYLE -> new DelphiStyleElement(this);
      case TagNames.JAVA_OBJECT -> new DelphiJavaObjectElement(this);
      case TagNames.ROOT -> new DelphiDocumentElement(this);

      default -> new DelphiElement(this, tagName);
    };
  }

  @Override
  public ItemElement createItemElement() {
    return new DelphiItemElement(this);
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
  public ComponentElement createComponent() {
    return new ChatElement(this);
  }

  @Override
  public ComponentElement createComponent(Component component) {
    ChatElement n = new ChatElement(this);
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
    if (documentElement == null) {
      return new ArrayList<>();
    }
    return documentElement.getElementsByTagName(tagName);
  }

  @Override
  public @NotNull List<Element> getElementsByClassName(@NotNull String className) {
    if (documentElement == null) {
      return new ArrayList<>();
    }
    return documentElement.getElementsByClassName(className);
  }

  @Override
  public @NotNull List<Element> querySelectorAll(@NotNull String query) throws ParserException {
    if (documentElement == null) {
      return new ArrayList<>();
    }
    return documentElement.querySelectorAll(query);
  }

  @Override
  public @Nullable Element querySelector(@NotNull String query) throws ParserException {
    if (documentElement == null) {
      return null;
    }

    return documentElement.querySelector(query);
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
    styles.addStylesheet((ChimeraStylesheet) stylesheet);
  }

  @Override
  public @NotNull List<Stylesheet> getStylesheets() {
    return Collections.unmodifiableList(styles.getSheets());
  }

  @Override
  public @NotNull ChimeraSheetBuilder createStylesheet() {
    if (styles == null) {
      return new ChimeraSheetBuilder(null);
    }

    return styles.newBuilder();
  }

  public StyleProperties getInlineStyle(DelphiElement el) {
    if (styles == null) {
      return null;
    }

    return styles.getInlineStyle(el);
  }

  public StylePropertiesReadonly getCurrentStyle(DelphiNode el) {
    if (styles == null) {
      return null;
    }

    return styles.getCurrentStyle(el);
  }

  public void setView(ExtendedView view) {
    this.view = view;

    for (ObjectModelSystem system : systems) {
      if (system == null) {
        continue;
      }

      if (view != null) {
        system.onViewAttach(view);
      } else {
        system.onViewDetach();
      }
    }
  }

  public void setRoot(DelphiDocumentElement elem) {
    if (elem == null) {
      if (this.documentElement != null) {
        documentElement.removeFlagRecursive(NodeFlag.ADDED);
        styles.removeNode(documentElement);
      }

      this.documentElement = null;
      return;
    }

    if (this.documentElement != null) {
      return;
    }

    this.documentElement = elem;
    elem.addFlagRecursive(NodeFlag.ADDED);

    styles.initRoot(elem);
  }

  @Override
  public DelphiBodyElement getBody() {
    return documentElement == null ? null : documentElement.getBody();
  }

  @Override
  public DelphiHeadElement getHead() {
    return documentElement == null ? null : documentElement.getHead();
  }
}
