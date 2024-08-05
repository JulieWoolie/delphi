package net.arcadiusmc.dom;

import net.kyori.adventure.text.Component;

public interface ComponentNode extends Node {

  Component getContent();

  void setContent(Component content);
}
