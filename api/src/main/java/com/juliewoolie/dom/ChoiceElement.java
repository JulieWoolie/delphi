package com.juliewoolie.dom;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a choice inside a {@link SelectElement}.
 */
public interface ChoiceElement extends Element, Disableable {

  /**
   * Get the select element the choice belongs to.
   * @return Selection element
   */
  @Nullable SelectElement getSelectElement();

  /**
   * Get the value of the element
   * <p>
   * Shorthand for accessing the {@link Attributes#VALUE} attribute value
   *
   * @return Value
   */
  @Nullable String getValue();

  /**
   * Set the value of the element
   * <p>
   * Shorthand for setting the {@link Attributes#VALUE} attribute value
   *
   * @param value Value
   */
  void setValue(@Nullable String value);

  /**
   * Test if the element is marked as selected.
   * <p>
   * Test if the {@link Attributes#SELECTED} void attribute is set
   *
   * @return {@code true} if an element is marked as selected, {@code false} otherwise.
   */
  boolean isSelected();

  /**
   * Set if an element is marked as selected.
   * @param selected {@code true} to mark as selected, {@code false} otherwise.
   */
  void setSelected(boolean selected);
}
