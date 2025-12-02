package com.juliewoolie.dom.event;

import com.juliewoolie.delphi.DocumentView;
import org.bukkit.entity.Player;

/**
 * Provides contextual information about changes to a {@link DocumentView#getPlayers()} set.
 */
public interface PlayerSetEvent extends Event {

  /**
   * Get the player that was added/removed from the player set.
   * @return Affected player
   */
  Player getPlayer();
}
