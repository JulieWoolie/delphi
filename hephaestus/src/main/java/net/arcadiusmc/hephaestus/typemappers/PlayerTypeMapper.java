package net.arcadiusmc.hephaestus.typemappers;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.graalvm.polyglot.Value;

public class PlayerTypeMapper implements TypeMapper<Value, Player> {

  @Override
  public Player apply(Value value) {
    if (value.isHostObject()) {
      if (value.asHostObject() instanceof Player player) {
        return player;
      }
      if (value.asHostObject() instanceof UUID id) {
        return Bukkit.getPlayer(id);
      }

      return null;
    }

    if (!value.isString()) {
      return null;
    }

    String playerName = value.asString();
    Player player = Bukkit.getPlayerExact(playerName);

    if (player != null) {
      return player;
    }

    try {
      UUID id = UUID.fromString(playerName);
      return Bukkit.getPlayer(id);
    } catch (IllegalArgumentException exc) {
      return null;
    }
  }

  @Override
  public boolean test(Value value) {
    return true;
  }
}
