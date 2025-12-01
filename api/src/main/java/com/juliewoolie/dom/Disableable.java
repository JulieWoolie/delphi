package com.juliewoolie.dom;

public interface Disableable {

  /**
   * Test if the element is enabled.
   * <p>
   * If the {@link Attributes#DISABLED} void attribute is set, then this method will
   * return {@code false}.
   * <p>
   * If the {@link Attributes#ENABLED} attribute is set with a boolean value, that boolean value
   * will be returned.
   *
   * @return {@code true}, if the element is enabled, {@code false} otherwise
   *
   * @see #setEnabled(boolean)
   */
  boolean isEnabled();

  /**
   * Shorthand for calling {@link #isEnabled()} with an inverted return value
   * @return Inverse of {@link #isEnabled()}
   * @see #isEnabled()
   */
  boolean isDisabled();

  /**
   * Set the enabled state of the element.
   * <p>
   * If the {@link Attributes#ENABLED} attribute has been set, it is removed and replaced with the
   * {@link Attributes#DISABLED} attribute which will either be set, or removed, depending on the
   * argument specified.
   *
   * @param enabled {@code true} to enable the element, {@code false} to disable it.
   */
  void setEnabled(boolean enabled);

  /**
   * Inverted shorthand for calling {@link #setEnabled(boolean)}
   * @param disabled {@code true} to disable the element, {@code false} to enable it.
   */
  void setDisabled(boolean disabled);
}
