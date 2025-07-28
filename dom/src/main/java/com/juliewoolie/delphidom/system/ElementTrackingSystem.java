package com.juliewoolie.delphidom.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.ExtendedView;
import com.juliewoolie.delphidom.event.EventListenerList;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.Node;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.MutationEvent;

public abstract class ElementTrackingSystem<T extends Element> implements ObjectModelSystem {

  private final Class<T> elementType;

  protected DelphiDocument document;
  protected ExtendedView view;
  protected final List<T> elements = new ArrayList<>();
  private final HeadListener listener = new HeadListener();

  public ElementTrackingSystem(Class<T> elementType) {
    this.elementType = elementType;
  }

  protected boolean filterContainer(Element element) {
    return true;
  }

  protected static boolean isInHeader(Document document, Element element) {
    return Objects.equals(document.getHead(), element);
  }

  @Override
  public void onAttach(DelphiDocument document) {
    this.document = document;

    EventListenerList t = document.getGlobalTarget();
    t.addEventListener(EventTypes.APPEND_CHILD, listener);
    t.addEventListener(EventTypes.REMOVE_CHILD, listener);
  }

  @Override
  public void onDetach() {
    EventListenerList t = document.getGlobalTarget();
    t.removeEventListener(EventTypes.APPEND_CHILD, listener);
    t.removeEventListener(EventTypes.REMOVE_CHILD, listener);

    for (T element : elements) {
      onDetachElement(element);
    }

    this.elements.clear();

    this.view = null;
    this.document = null;
  }

  @Override
  public void onViewAttach(ExtendedView view) {
    this.view = view;
  }

  @Override
  public void onViewDetach() {
    this.view = null;
  }

  protected void onDetachElement(T t) {

  }

  protected void onAppend(T t) {

  }

  protected void onRemove(T t) {

  }

  class HeadListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      Element target = event.getTarget();
      Node node = event.getNode();

      if (!elementType.isInstance(node)) {
        return;
      }
      T t = elementType.cast(node);

      if (!filterContainer(target)) {
        return;
      }

      if (event.getType().equals(EventTypes.APPEND_CHILD)) {
        elements.add(t);
        onAppend(t);
        return;
      }

      elements.remove(t);
      onRemove(t);
    }
  }
}
