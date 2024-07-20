package net.arcadiusmc.dom;

import org.jetbrains.annotations.Nullable;

public interface TextNode extends Node {

  @Nullable
  String getTextContent();

  void setTextContent(@Nullable String textContent);
}
