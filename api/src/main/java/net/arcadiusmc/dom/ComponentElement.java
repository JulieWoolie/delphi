package net.arcadiusmc.dom;

import net.kyori.adventure.text.Component;

/**
 * A node which uses a {@link Component} as content
 */
public interface ComponentElement extends Element {

  /**
   * Component elements cannot have child elements, this method will always
   * return {@code false}.
   *
   * @return {@code false}
   */
  @Override
  boolean canHaveChildren();

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
