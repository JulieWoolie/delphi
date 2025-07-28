package com.juliewoolie.delphidom.system;

import com.google.common.base.Strings;
import com.juliewoolie.delphidom.ContentSource;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.event.AttributeAction;
import com.juliewoolie.dom.event.AttributeMutateEvent;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.MutationEvent;

public abstract class ParsedDataElementSystem<T extends Element> extends ElementTrackingSystem<T> {

  final SrcAttrListener attrListener = new SrcAttrListener();
  final ContentListener contentListener = new ContentListener();

  public ParsedDataElementSystem(Class<T> elementType) {
    super(elementType);
  }

  protected abstract void parseFromContent(T element);

  protected abstract void loadFromSrc(T element, String uri);

  protected abstract ContentSource getSource(T element);

  protected abstract void setSource(T element, ContentSource source);

  @Override
  protected void onAppend(T t) {
    t.addEventListener(EventTypes.MODIFY_ATTR, attrListener);
    t.addEventListener(EventTypes.APPEND_CHILD, contentListener);
    t.addEventListener(EventTypes.REMOVE_CHILD, contentListener);
  }

  @Override
  protected void onRemove(T t) {
    t.removeEventListener(EventTypes.MODIFY_ATTR, attrListener);
    t.removeEventListener(EventTypes.APPEND_CHILD, contentListener);
    t.removeEventListener(EventTypes.REMOVE_CHILD, contentListener);
  }

  @Override
  protected void onDetachElement(T t) {
    t.removeEventListener(EventTypes.APPEND_CHILD, contentListener);
    t.removeEventListener(EventTypes.REMOVE_CHILD, contentListener);
    t.removeEventListener(EventTypes.MODIFY_ATTR, attrListener);
  }

  protected void onUnset(T element) {

  }

  class SrcAttrListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      T target = (T) event.getTarget();
      assert target != null;

      if (!event.getKey().equals(Attributes.SOURCE)) {
        return;
      }

      if (getSource(target) == ContentSource.TEXT_CONTENT) {
        return;
      }

      if (event.getAction() == AttributeAction.REMOVE) {
        setSource(target, ContentSource.NONE);
        onUnset(target);

        String content = target.getTextContent();
        if (!Strings.isNullOrEmpty(content)) {
          parseFromContent(target);
        }

        return;
      }

      if (view == null) {
        return;
      }

      String uri = event.getNewValue();
      setSource(target, ContentSource.SRC_ATTR);
      loadFromSrc(target, uri);
    }
  }

  class ContentListener implements EventListener.Typed<MutationEvent> {

    @Override
    public void handleEvent(MutationEvent event) {
      T target = (T) event.getTarget();
      assert target != null;

      if (getSource(target) == ContentSource.SRC_ATTR) {
        return;
      }

      String textContent = target.getTextContent();

      if (Strings.isNullOrEmpty(textContent)) {
        setSource(target, ContentSource.NONE);
        onUnset(target);

        String src = target.getAttribute(Attributes.SOURCE);
        if (!Strings.isNullOrEmpty(src)) {
          loadFromSrc(target, src);
        }
        return;
      }

      parseFromContent(target);
      setSource(target, ContentSource.TEXT_CONTENT);
    }
  }
}
