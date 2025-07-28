package com.juliewoolie.delphidom.system;

import static com.juliewoolie.dom.JavaObjectElement.INIT_METHOD_NAME;

import com.google.common.base.Strings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.juliewoolie.delphidom.DelphiJavaObjectElement;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.event.AttributeMutateEvent;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;
import org.slf4j.Logger;

public class JavaObjectSystem extends ElementTrackingSystem<DelphiJavaObjectElement> {

  private static final Logger LOGGER = Loggers.getDocumentLogger();

  AttrListener listener = new AttrListener();

  public JavaObjectSystem() {
    super(DelphiJavaObjectElement.class);
  }

  @Override
  protected boolean filterContainer(Element element) {
    return isInHeader(document, element);
  }

  @Override
  protected void onAppend(DelphiJavaObjectElement element) {
    element.addEventListener(EventTypes.MODIFY_ATTR, listener);

    if (element.entrypointCalled) {
      return;
    }

    String className = element.getClassName();
    if (Strings.isNullOrEmpty(className)) {
      return;
    }

    tryLinkClass(element, className);
  }

  @Override
  protected void onRemove(DelphiJavaObjectElement element) {
    element.removeEventListener(EventTypes.MODIFY_ATTR, listener);
  }

  void tryLinkClass(DelphiJavaObjectElement element, String cname) {
    Class<?> javaType;

    try {
      javaType = Class.forName(cname, true, getClass().getClassLoader());
    } catch (ClassNotFoundException e) {
      LOGGER.error("Failed to find class {}", cname, e);
      return;
    }

    element.linkedClass = javaType;

    Executable init;
    try {
      init = javaType.getMethod(INIT_METHOD_NAME, Document.class);
    } catch (NoSuchMethodException e) {
      try {
        init = javaType.getConstructor(Document.class);
      } catch (NoSuchMethodException ex) {
        LOGGER.error("Failed to find entry point to class {}", cname);
        return;
      }
    }

    try {
      if (init instanceof Constructor<?> ctor) {
        ctor.newInstance(document);
      } else {
        Method m = (Method) init;
        m.invoke(null, document);
      }

      element.entrypointCalled = true;
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
      LOGGER.error("Failed to call class entry point on {}", cname, e);
    }
  }

  class AttrListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      if (!event.getKey().equals(Attributes.CLASS_NAME)) {
        return;
      }

      DelphiJavaObjectElement element = (DelphiJavaObjectElement) event.getTarget();
      assert element != null;

      element.entrypointCalled = false;

      String nval = event.getNewValue();
      if (Strings.isNullOrEmpty(nval)) {
        element.linkedClass = null;
        return;
      }

      tryLinkClass(element, nval);
    }
  }
}
