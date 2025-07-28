package com.juliewoolie.dom.event;

import com.juliewoolie.dom.InputElement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when an {@link InputElement}'s value is changed either by a player or
 * by invoking the {@link InputElement#setValue(String)} method.
 */
public interface InputEvent extends Event {

  /**
   * Gets the node this event was called on.
   * @return Event target
   */
  @Override
  @NotNull InputElement getTarget();

  /**
   * Get the new value of the input element
   * @return New value
   */
  @Nullable String getNewValue();

  /**
   * Get the previous value of the input element
   * @return Previous value
   */
  @Nullable String getPreviousValue();

  /**
   * Get the player that typed in the new input, will be {@code null}, if
   * this event was triggered by invoking the {@link InputElement#setValue(String)}
   * method.
   *
   * @return Player, or {@code null}
   */
  @Nullable Player getPlayer();
}
