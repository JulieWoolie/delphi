package com.juliewoolie.dom;

import com.juliewoolie.dom.event.EventTypes;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An element that can take in a player's input
 */
public interface InputElement extends Element {

  /**
   * Default placeholder value returned by {@link #getPlaceholder()}
   */
  String DEFAULT_PLACEHOLDER = "...";

  /**
   * Input elements cannot have child nodes.
   * @return {@code false}
   */
  @Override @Contract("-> false")
  boolean canHaveChildren();

  /**
   * Test if the element is disabled.
   * <p>
   * This element can be disabled by setting the {@link Attributes#ENABLED}
   * attribute to {@code false}.
   *
   * @return {@code true}, if the element is disabled, {@code false} otherwise
   */
  boolean isDisabled();

  /**
   * Set the {@link Attributes#ENABLED} attribute
   * @param disabled {@code true} to disable the element,
   *                 {@code false} otherwise.
   */
  void setDisabled(boolean disabled);

  /**
   * Get the input type from the {@link Attributes#TYPE} attribute.
   * <p>
   * If the attribute isn't set or its value does not map to an enum value,
   * then {@link InputType#TEXT} is returned.
   *
   * @return Input type
   */
  @NotNull InputType getType();

  /**
   * Set the input type
   * @param type Input type, or {@code null}.
   */
  void setType(@Nullable InputType type);

  /**
   * Get the inputted value
   * @return Inputted value, may be an empty string or {@code null}
   */
  @Nullable String getValue();

  /**
   * Set the inputted value.
   * <p>
   * Triggers a {@link EventTypes#INPUT} event.
   *
   * @param value New element value
   */
  void setValue(@Nullable String value);

  /**
   * Set the inputted value.
   * <p>
   * Triggers a {@link EventTypes#INPUT} event.
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

  /**
   * Input element type
   */
  enum InputType {
    /**
     * Regular text input
     */
    TEXT ("text"),

    /**
     * Password text, when rendered, all characters will be replaced with
     * the {@code *} character.
     */
    PASSWORD ("password"),

    /**
     * Number input, validated beforehand when entered by a player, however calling the
     * {@link #setValue(String)} method can skip validation.
     */
    NUMBER ("number"),
    ;

    final String keyword;

    InputType(String keyword) {
      this.keyword = keyword;
    }

    /**
     * Get the keyword used to represent the input type
     * @return Input type keyword
     */
    public String getKeyword() {
      return keyword;
    }
  }
}
