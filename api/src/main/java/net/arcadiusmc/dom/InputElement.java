package net.arcadiusmc.dom;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An element that can take in a player's input
 */
public interface InputElement extends Element {

  /**
   * Default placeholder value returned by {@link #getPlaceholder()}
   */
  String DEFAULT_PLACEHOLDER = "Enter input...";

  /**
   * Input elements cannot have child nodes.
   * @return {@code false}
   */
  @Override
  boolean canHaveChildren();

  /**
   * Get the inputted value
   * @return Inputted value, may be an empty string or {@code null}
   */
  @Nullable String getValue();

  /**
   * Set the inputted value.
   * <p>
   * Triggers a {@link net.arcadiusmc.dom.event.EventTypes#INPUT} event.
   *
   * @param value New element value
   */
  void setValue(@Nullable String value);

  /**
   * Set the inputted value.
   * <p>
   * Triggers a {@link net.arcadiusmc.dom.event.EventTypes#INPUT} event.
   *
   * @param value New element value
   * @param player Player that changed the element
   */
  void setValue(@Nullable String value, @NotNull Player player);

  /**
   * Get the element's placeholder, or a fallback value, if not set.
   * <p>
   * Placeholders are set with the {@link Attributes#PLACEHOLDER} attribute.
   * If the attribute is not explicitly set, then this method returns
   * {@link #DEFAULT_PLACEHOLDER}.
   *
   * @return Placeholder
   */
  @NotNull String getPlaceholder();

  /**
   * Set the value of the {@link Attributes#PLACEHOLDER} attribute.
   * @param placeholder New placeholder
   */
  void setPlaceholder(@Nullable String placeholder);
}
