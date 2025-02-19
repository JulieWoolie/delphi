package net.arcadiusmc.dom;

import org.jetbrains.annotations.Nullable;

/**
 * The option element which specifies an option inside a document's header element.
 */
public interface OptionElement extends Element {

  /**
   * Option elements cannot have children
   * @return {@code false}
   */
  @Override
  boolean canHaveChildren();

  /**
   * Get the option name.
   * <p>
   * Shortcut for accessing the {@link Attributes#NAME} attribute.
   *
   * @return Option name
   */
  String getName();

  /**
   * Set the option name.
   * <p>
   * Shortcut for setting the {@link Attributes#NAME} attribute.
   *
   * @param key New option name
   */
  void setName(@Nullable String key);

  /**
   * Get the value of the option.
   * <p>
   * Shortcut for accessing the {@link Attributes#VALUE} attribute.
   *
   * @return Option value
   */
  String getValue();

  /**
   * Set the value of the option.
   * <p>
   * Shortcut for setting the {@link Attributes#VALUE} attribute.
   *
   * @param value New option value
   */
  void setValue(String value);
}
