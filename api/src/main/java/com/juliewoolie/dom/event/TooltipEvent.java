package com.juliewoolie.dom.event;

import com.juliewoolie.dom.Element;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event fired when an element's tooltip, aka title, element is changed.
 */
public interface TooltipEvent {

  /**
   * Get the old tooltip element being removed.
   * @return Old tooltip element, or {@code null}, if there was no previous tooltip element
   */
  @Nullable
  Element getOldTooltip();

  /**
   * Get the new tooltip element being set.
   * @return New tooltip element, or {@code null}, if a tooltip is being removed.
   */
  @Nullable
  Element getNewTooltip();
}
