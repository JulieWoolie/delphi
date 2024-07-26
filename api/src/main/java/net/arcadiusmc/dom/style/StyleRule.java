package net.arcadiusmc.dom.style;

public interface StyleRule {

  String getSelector();

  StylePropertiesReadonly getProperties();
}
