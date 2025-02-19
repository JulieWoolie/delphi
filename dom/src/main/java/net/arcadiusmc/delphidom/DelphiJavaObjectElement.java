package net.arcadiusmc.delphidom;

import com.google.common.base.Strings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.JavaObjectElement;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;
import org.slf4j.Logger;

public class DelphiJavaObjectElement extends DelphiElement implements JavaObjectElement {

  private static final Logger LOGGER = Loggers.getDocumentLogger();

  Class<?> linkedClass;
  boolean entrypointCalled = false;

  public DelphiJavaObjectElement(DelphiDocument document) {
    super(document, TagNames.JAVA_OBJECT);
    addEventListener(EventTypes.MODIFY_ATTR, new ClassNameListener());
  }

  @Override
  public String getClassName() {
    return getAttribute(Attributes.CLASS_NAME);
  }

  @Override
  public Class<?> getJavaClass() {
    return linkedClass;
  }

  @Override
  public boolean wasEntrypointCalled() {
    return entrypointCalled;
  }

  @Override
  public void setClassName(String className) {
    setAttribute(Attributes.CLASS_NAME, className);
  }

  void tryLinkClass(String cname) {
    Class<?> javaType;

    try {
      javaType = Class.forName(cname, true, getClass().getClassLoader());
    } catch (ClassNotFoundException e) {
      LOGGER.error("Failed to find class {}", cname, e);
      return;
    }

    linkedClass = javaType;

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
        ctor.newInstance(getOwningDocument());
      } else {
        Method m = (Method) init;
        m.invoke(null, getOwningDocument());
      }

      entrypointCalled = true;
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
      LOGGER.error("Failed to call class entry point on {}", cname, e);
    }
  }

  class ClassNameListener implements EventListener.Typed<AttributeMutateEvent> {

    @Override
    public void handleEvent(AttributeMutateEvent event) {
      if (!event.getKey().equals(Attributes.CLASS_NAME)) {
        return;
      }

      entrypointCalled = false;

      String nval = event.getNewValue();
      if (Strings.isNullOrEmpty(nval)) {
        linkedClass = null;
        return;
      }

      tryLinkClass(nval);
    }
  }
}
