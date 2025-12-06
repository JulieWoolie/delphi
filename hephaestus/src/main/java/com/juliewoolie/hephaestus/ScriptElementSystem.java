package com.juliewoolie.hephaestus;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import com.juliewoolie.delphi.resource.DelphiException;
import com.juliewoolie.delphi.resource.ViewResources;
import com.juliewoolie.delphi.util.Result;
import com.juliewoolie.delphidom.ContentSource;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiScriptElement;
import com.juliewoolie.delphidom.ExtendedView;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.delphidom.event.EventListenerList;
import com.juliewoolie.delphidom.system.ParsedDataElementSystem;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.event.AttributeAction;
import com.juliewoolie.dom.event.AttributeMutateEvent;
import com.juliewoolie.dom.event.Event;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;

public class ScriptElementSystem extends ParsedDataElementSystem<DelphiScriptElement> {

  private static final Logger LOGGER = Loggers.getLogger();
  private Context context;
  private Value jsScope;

  private boolean domLoaded = false;
  private final LoadListener loadListener = new LoadListener();
  private final List<DeferredScript> deferredScripts = new ArrayList<>();

  private final AttributeChangeListener eventAttrListener = new AttributeChangeListener();

  public ScriptElementSystem() {
    super(DelphiScriptElement.class);
  }

  @Override
  public void onAttach(DelphiDocument document) {
    super.onAttach(document);

    this.context = Scripting.setupContext();
    this.jsScope = context.getBindings(Scripting.JS_LANGUAGE);
    this.domLoaded = false;

    jsScope.putMember("document", document);

    Scripting.initDocumentScope(jsScope, document);

    EventListenerList target = document.getGlobalTarget();
    target.addEventListener(EventTypes.DOM_LOADED, loadListener);
    target.addEventListener(EventTypes.MODIFY_ATTR, eventAttrListener);
  }

  @Override
  public void onViewAttach(ExtendedView view) {
    super.onViewAttach(view);
    jsScope.putMember("view", view);
    Scripting.initViewScope(jsScope, view);
  }

  @Override
  protected void onRemove(DelphiScriptElement delphiScriptElement) {
    super.onRemove(delphiScriptElement);
  }

  @Override
  public void onDetach() {
    jsScope.removeMember("document");

    context.close(true);

    context = null;
    jsScope = null;
    domLoaded = false;

    EventListenerList target = document.getGlobalTarget();
    target.removeEventListener(EventTypes.DOM_LOADED, loadListener);
    target.removeEventListener(EventTypes.MODIFY_ATTR, eventAttrListener);

    super.onDetach();
  }

  @Override
  public void onViewDetach() {
    super.onViewDetach();

    jsScope.removeMember("view");

    jsScope.removeMember("setInterval");
    jsScope.removeMember("setTimeout");
    jsScope.removeMember("clearInterval");
    jsScope.removeMember("clearTimeout");
  }

  @Override
  protected void parseFromContent(DelphiScriptElement element) {
    String textContent = element.getTextContent();
    scriptExec(element, "<script>", textContent);
  }

  @Override
  protected void loadFromSrc(DelphiScriptElement element, String uri) {
    ViewResources resources = document.getView().getResources();
    Result<StringBuffer, DelphiException> result = resources.loadBuffer(uri);

    if (result.isError()) {
      LOGGER.error("Failed to load script source: {}", result.error().orElseThrow().getMessage());
      return;
    }

    resources.resolve(uri).ifSuccess(rpath -> element.resourcePath = rpath);

    String content = result.value().orElseThrow().toString();
    scriptExec(element, uri, content);
  }

  @Override
  protected ContentSource getSource(DelphiScriptElement element) {
    return element.source;
  }

  @Override
  protected void setSource(DelphiScriptElement element, ContentSource source) {
    element.source = source;
  }

  private void execDeferred() {
    for (DeferredScript deferredScript : deferredScripts) {
      evaluate(deferredScript.uri, deferredScript.source);
    }
    deferredScripts.clear();
  }

  private void scriptExec(DelphiScriptElement element, String uri, String source) {
    if (domLoaded || !element.isDeferred()) {
      evaluate(uri, source);
      return;
    }

    DeferredScript found = null;
    for (int i = 0; i < deferredScripts.size(); i++) {
      DeferredScript deferred = deferredScripts.get(i);
      if (deferred.element == element) {
        found = deferred;
        break;
      }
    }

    if (found == null) {
      found = new DeferredScript(element);
      deferredScripts.addLast(found);
    }

    found.uri = uri;
    found.source = source;
  }

  private void evaluate(String uri, String src) {
    Source source = Source.newBuilder(Scripting.JS_LANGUAGE, src, uri).buildLiteral();

    try {
      context.eval(source);
    } catch (PolyglotException exc) {
      LOGGER.error("Failed to compile JS from {}:", uri, exc);
    } catch (Exception exc) {
      LOGGER.error("JS Evaluation error: ", exc);
    }
  }

  private static Value executeSafely(Value value, Object... args) {
    try {
      return value.execute(args);
    } catch (Exception e) {
      LOGGER.error("JavaScript invocation failure", e);
      return null;
    }
  }

  @RequiredArgsConstructor
  static class DeferredScript {
    final DelphiScriptElement element;
    String source;
    String uri;
  }

  class LoadListener implements EventListener {

    @Override
    public void onEvent(Event event) {
      domLoaded = true;
      execDeferred();
    }
  }

  class AttributeChangeListener implements EventListener.Typed<AttributeMutateEvent> {

    final Map<Element, Map<String, ScriptedAttribute>> trackingMap = new Object2ObjectOpenHashMap<>();

    String toEventType(String attrKey) {
      if (attrKey.startsWith("on-")) {
        attrKey = attrKey.substring(3);
      } else if (attrKey.startsWith("on")) {
        attrKey = attrKey.substring(2);
      }

      return switch (attrKey) {
        case "mouseenter" -> EventTypes.MOUSE_ENTER;
        case "mouseexit" -> EventTypes.MOUSE_LEAVE;
        case "mousemove" -> EventTypes.MOUSE_MOVE;
        case "clickexpire" -> EventTypes.CLICK_EXPIRE;
        case "append" -> EventTypes.APPEND_CHILD;
        case "removechild" -> EventTypes.REMOVE_CHILD;
        case "modifyattr" -> EventTypes.MODIFY_ATTR;
        case "modifyoption" -> EventTypes.MODIFY_OPTION;
        case "spawn" -> EventTypes.DOM_SPAWNED;
        case "contentchanged" -> EventTypes.CONTENT_CHANGED;
        case "viewmoved" -> EventTypes.VIEW_MOVED;
        case "tooltip" -> EventTypes.TOOLTIP_CHANGED;
        case "playeradd", "onplayeradded" -> EventTypes.PLAYER_ADDED;
        case "playerremove", "onplayerremoved" -> EventTypes.PLAYER_REMOVED;
        default -> attrKey;
      };
    }

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      String key = event.getKey();
      if (!key.startsWith("on")) {
        return;
      }

      Element target = event.getTarget();
      assert target != null;

      if (event.getAction() == AttributeAction.REMOVE) {
        String eventType = toEventType(key);

        Map<String, ScriptedAttribute> map = trackingMap.get(target);
        if (map == null) {
          return;
        }

        ScriptedAttribute removed = map.remove(eventType);
        if (removed == null) {
          return;
        }

        target.removeEventListener(eventType, removed);

        if (map.isEmpty()) {
          trackingMap.remove(target);
        }
        return;
      }

      String scriptCode = event.getNewValue();
      String uri = String.format("<%s/>::%s", target.getTagName(), key);
      Value compiled;

      try {
        Source source = Source.newBuilder(Scripting.JS_LANGUAGE, scriptCode, uri).buildLiteral();
        compiled = context.parse(source);
      } catch (PolyglotException exc) {
        LOGGER.error("Failed to compile event listener {}:", uri, exc);
        return;
      }

      String eventType = toEventType(key);

      Map<String, ScriptedAttribute> map = trackingMap.get(target);
      ScriptedAttribute scriptedAttribute = new ScriptedAttribute(compiled);

      if (map == null) {
        map = new Object2ObjectOpenHashMap<>();
        map.put(eventType, scriptedAttribute);
        trackingMap.put(target, map);
      } else {
        ScriptedAttribute attr = map.put(eventType, scriptedAttribute);
        if (attr != null) {
          target.removeEventListener(eventType, attr);
        }
      }

      target.addEventListener(eventType, scriptedAttribute);
    }
  }

  static class ScriptedAttribute implements EventListener {

    private Value compiled;
    private Value func;

    public ScriptedAttribute(Value compiled) {
      this.compiled = compiled;
      this.func = null;
    }

    @Override
    public void onEvent(Event event) {
      if (func != null) {
        executeSafely(func, event);
        return;
      }

      Value v = executeSafely(compiled);
      if (v == null || !v.canExecute()) {
        return;
      }

      func = v;
      executeSafely(func, event);
    }
  }
}
