package net.arcadiusmc.hephaestus.typemappers;

import java.util.Locale;
import java.util.UUID;
import java.util.jar.Attributes.Name;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.graalvm.polyglot.Value;

public class WorldTypeMapper implements TypeMapper<Value, World> {

  @Override
  public World apply(Value value) {
    if (value.isHostObject()) {
      Object host = value.asHostObject();
      if (host instanceof World world) {
        return world;
      }
      if (host instanceof Location location) {
        return location.getWorld();
      }
      if (host instanceof Entity entity) {
        return entity.getWorld();
      }
      if (host instanceof Block block) {
        return block.getWorld();
      }
      if (host instanceof BlockState state) {
        return state.getWorld();
      }
      return null;
    }

    String string = value.asString();
    World result = Bukkit.getWorld(string);

    if (result != null) {
      return result;
    }

    if (string.contains(":")) {
      NamespacedKey key = NamespacedKey.fromString(string);
      if (key != null) {
        result = Bukkit.getWorld(key);
        if (result != null) {
          return result;
        }
      }
    }

    try {
      UUID id = UUID.fromString(string);
      return Bukkit.getWorld(id);
    } catch (IllegalArgumentException exc) {
      return null;
    }
  }

  @Override
  public boolean test(Value value) {
    return true;
  }
}
