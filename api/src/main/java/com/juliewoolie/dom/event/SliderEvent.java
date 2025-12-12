package com.juliewoolie.dom.event;

import com.juliewoolie.dom.SliderElement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event triggered by a slider's value being changed.
 */
public interface SliderEvent extends Event {

  /**
   * Get the slider element the event was triggered on
   * @return Slider element target
   */
  @Override
  SliderElement getTarget();

  /**
   * Get the new slider value
   * @return New slider value
   */
  @Nullable Double getNewValue();

  /**
   * Get the previous slider value
   * @return Previous slider value
   */
   @Nullable Double getPreviousValue();

  /**
   * Get the source of the change
   * @return Player that caused the slider to change, or {@code null}, if the change didn't
   *         come from a player.
   */
  @Nullable
  Player getPlayer();
}
