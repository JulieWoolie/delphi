package net.arcadiusmc.delphiplugin;

import com.destroystokyo.paper.ParticleBuilder;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import net.arcadiusmc.delphiplugin.math.Screen;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.joml.Vector2f;
import org.joml.Vector3f;

public final class Debug {
  private Debug() {}

  static final float POINT_DIST = 0.12f;

  public static void drawSelectionOutline(Rectangle rectangle, PageView view) {
    Vector2f min = rectangle.getPosition();
    Vector2f max = new Vector2f();
    rectangle.getMax(max);

    Vector3f loLeft = new Vector3f();
    Vector3f hiLeft = new Vector3f();
    Vector3f loRight = new Vector3f();
    Vector3f hiRight = new Vector3f();

    Screen screen = view.getScreen();
    screen.screenToWorld(min, loLeft);
    screen.screenToWorld(max, hiRight);

    loRight.set(hiRight);
    loRight.y = loLeft.y;

    hiLeft.set(loLeft);
    hiLeft.y = hiRight.y;

    ParticleBuilder builder = Particle.DUST.builder()
        .color(Color.RED, 0.5f)
        .receivers(view.getPlayer());

    World w = view.getWorld();

    line(loLeft, hiLeft, builder, w);
    line(loLeft, loRight, builder, w);
    line(hiLeft, hiRight, builder, w);
    line(loRight, hiRight, builder, w);
  }

  private static void line(Vector3f origin, Vector3f target, ParticleBuilder builder, World world) {
    Vector3f dir = new Vector3f(target).sub(origin);

    float len = dir.length();
    dir.normalize();

    Vector3f point = new Vector3f();

    for (float c = 0; c <= len; c += POINT_DIST) {
      dir.mul(c, point);
      point.add(origin);

      builder.location(world, point.x, point.y, point.z)
          .spawn();
    }
  }
}
