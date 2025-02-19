package net.arcadiusmc.dom;

import net.kyori.adventure.text.Component;

/**
 * A node which uses a {@link Component} as content
 */
public interface ComponentElement extends Element {

  /**
   * Get the chat component content
   * @return Content component
   */
  Component getContent();

  /**
   * Set the chat component content
   * @param content Content component
   */
  void setContent(Component content);
}
