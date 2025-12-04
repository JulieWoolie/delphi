package com.juliewoolie.dom;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a basic text node in the XML tree
 */
public interface TextNode extends Node {

  /**
   * Get the text content of the node
   * @return String text content
   */
  @Nullable
  String getTextContent();

  /**
   * Set the text content of the node
   * @param textContent Text content
   */
  void setTextContent(@Nullable String textContent);
}
