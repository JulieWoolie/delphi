package net.arcadiusmc.delphidom;

import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.JavaObjectElement;
import net.arcadiusmc.dom.TagNames;

public class DelphiJavaObjectElement extends DelphiElement implements JavaObjectElement {

  public Class<?> linkedClass;
  public boolean entrypointCalled = false;

  public DelphiJavaObjectElement(DelphiDocument document) {
    super(document, TagNames.JAVA_OBJECT);
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
}
