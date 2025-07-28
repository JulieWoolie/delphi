package com.juliewoolie.delphi;

import java.util.Set;
import org.bukkit.entity.Player;

/**
 * Set of players
 * <p>
 * Instances of this interface are mutable, unless {@link #isServerPlayerSet()} returns {@code true}, players
 * cannot be removed from a set containing all the server's players.
 */
public interface PlayerSet extends Set<Player> {

  /**
   * Tests if this set is simply a wrapper around the server's whole player list
   * @return {@code true}, if this set contains all online player, {@code false} otherwise
   */
  boolean isServerPlayerSet();
}
