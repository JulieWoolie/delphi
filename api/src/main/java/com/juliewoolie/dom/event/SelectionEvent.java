package com.juliewoolie.dom.event;

import com.juliewoolie.dom.ChoiceElement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the data for an event fired when a {@link com.juliewoolie.dom.SelectElement}'s value
 * is changed.
 */
public interface SelectionEvent extends Event {

  /**
   * Get the element that was selected
   * @return Selected element, or {@code null}, if the selection was cleared
   */
  @Nullable ChoiceElement getChoice();

  /**
   * Get the player that changed the selection.
   *
   * @return Player that changed the selection, or
   *         {@code null}, if change was not made by a player.
   */
  @Nullable Player getPlayer();
}
