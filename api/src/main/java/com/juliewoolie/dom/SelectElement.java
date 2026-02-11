package com.juliewoolie.dom;

import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SelectElement extends Element, Disableable {

  /**
   * Get a list of choices that belong to the select element
   * @return List of choices
   */
  @NotNull
  List<ChoiceElement> getChoices();

  /**
   * Get the first choice element marked as selected
   * @return First selected choice
   */
  @Nullable ChoiceElement getSelectedChoice();

  /**
   * Set the choice with the specified {@code value} as the selected choice.
   * <p>
   * If the specified {@code value} doesn't match any choice's value then the selected
   * choice is cleared.
   * <p>
   * If no changes were made as a result of the method call, nothing happens. Otherwise,
   * this method will trigger a {@link com.juliewoolie.dom.event.EventTypes#SELECT} event.
   *
   * @param value New value, or {@code null}, to unselect
   * @see ChoiceElement#getValue()
   */
  void select(@Nullable String value);

  /**
   * Set the choice with the specified {@code value} as the selected choice.
   * <p>
   * If the specified {@code value} doesn't match any choice's value then the selected
   * choice is cleared.
   * <p>
   * If no changes were made as a result of the method call, nothing happens. Otherwise,
   * this method will trigger a {@link com.juliewoolie.dom.event.EventTypes#SELECT} event.
   *
   * @param player Player that changed the value
   * @param value New value, or {@code null}, to unselect
   *
   * @see ChoiceElement#getValue()
   */
  void select(@Nullable Player player, @Nullable String value);
}
