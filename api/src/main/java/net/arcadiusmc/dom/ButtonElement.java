package net.arcadiusmc.dom;

/**
 * Button elements play a sound when clicked
 */
public interface ButtonElement extends Element {

  /**
   * Test if the button is enabled
   * <p>
   * Shortcut for accessing the {@link Attributes#ENABLED} attribute.
   *
   * @return {@code true}, if the button is enabled, {@code false} otherwise.
   */
  boolean isEnabled();

  /**
   * Set if the button is enabled
   * <p>
   * Shortcut for setting the {@link Attributes#ENABLED} attribute.
   *
   * @param enabled {@code true}, if the button is enabled, {@code false} otherwise.
   */
  void setEnabled(boolean enabled);
}
