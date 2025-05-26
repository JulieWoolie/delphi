package net.arcadiusmc.hephaestus.typemappers;

import net.arcadiusmc.hephaestus.Scripting;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.graalvm.polyglot.Value;

public class LocationTypeMapper implements TypeMapper<Value, Location> {

  @Override
  public Location apply(Value value) {
    if (value.isHostObject()) {
      Object host = value.asHostObject();
      if (host instanceof Location loc) {
        return loc;
      }
      if (host instanceof Entity entity) {
        return entity.getLocation();
      }
      return null;
    }

    if (!value.hasMembers()) {
      return null;
    }

    World world = value.getMember("world").as(World.class);

    double x = Scripting.toDouble(value.getMember("x"), 0.0);
    double y = Scripting.toDouble(value.getMember("y"), 0.0);
    double z = Scripting.toDouble(value.getMember("z"), 0.0);

    float yaw = Scripting.toFloat(value.getMember("yaw"), 0.0f);
    float pitch = Scripting.toFloat(value.getMember("pitch"), 0.0f);

    return new Location(world, x, y, z, yaw, pitch);
  }

  @Override
  public boolean test(Value value) {
    return true;
  }
}
