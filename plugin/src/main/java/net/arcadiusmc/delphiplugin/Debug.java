package net.arcadiusmc.delphiplugin;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphiplugin.command.Permissions;
import net.arcadiusmc.delphiplugin.math.Rectangle;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.delphiplugin.render.RenderTreePrint;
import net.arcadiusmc.dom.Visitor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;

public final class Debug {
  private Debug() {}

  private static final Logger LOGGER = Loggers.getLogger();
  public static boolean debugOutlines = LOGGER.isDebugEnabled();
  static final float POINT_DIST = 0.12f;

  public static Path dumpDebugTree(String fileName, PageView view) {
    Path dir;
    ClassLoader loader = Debug.class.getClassLoader();

    if (loader instanceof ConfiguredPluginClassLoader l && l.getPlugin() != null) {
      dir = l.getPlugin().getDataPath().resolve("debug");
    } else {
      dir = Path.of("debug");
    }

    if (!Files.isDirectory(dir)) {
      try {
        Files.createDirectories(dir);
      } catch (IOException exc) {
        LOGGER.error("Failed to create debug dump directory at {}", dir, exc);
        return null;
      }
    }

    Path dumpFile = dir.resolve(fileName + ".xml");

    RenderTreePrint print = new RenderTreePrint(view);

    print.nlIndent().append("<page>");
    print.indent++;

    print.appendHeader();
    Visitor.visit(view.getDocument().getBody(), print);

    print.indent--;
    print.nlIndent().append("</page>");

    String string = print.toString();

    try {
      Files.writeString(dumpFile, string, StandardCharsets.UTF_8);
    } catch (IOException exc) {
      Loggers.getDocumentLogger().error("Failed to dump XML info", exc);
      return null;
    }

    return dumpFile;
  }

  public static void drawSelectionOutline(Rectangle rectangle, PageView view) {
    drawOutline(rectangle, view, Color.RED);
  }

  public static void drawScreen(Screen screen, World world) {
    Vector3f loRight = screen.getLowerRight();
    Vector3f hiRight = screen.getUpperRight();
    Vector3f loLeft = screen.getLowerLeft();
    Vector3f hiLeft = screen.getUpperLeft();

    Vector3f center = screen.center();
    Vector3f normal = screen.normal();

    normal.add(center);

    ParticleBuilder builder = particleBuilder(Color.BLUE);

    line(center, normal, builder, world);

    line(loRight, loLeft, builder, world);
    line(loRight, hiRight, builder, world);
    line(hiLeft, hiRight, builder, world);
    line(loLeft, hiLeft, builder, world);
  }

  public static void drawOutline(Rectangle rectangle, PageView view, Color color) {
    Vector2f screenLoLeft = rectangle.getPosition();
    Vector2f screenHiRight = new Vector2f();
    Vector2f screenLoRight = new Vector2f();
    Vector2f screenHiLeft = new Vector2f();

    rectangle.getMax(screenHiRight);

    screenHiLeft.set(screenLoLeft);
    screenHiLeft.y = screenHiRight.y;

    screenLoRight.set(screenHiRight);
    screenLoRight.y = screenLoLeft.y;

    Vector3f loLeft = new Vector3f();
    Vector3f hiLeft = new Vector3f();
    Vector3f loRight = new Vector3f();
    Vector3f hiRight = new Vector3f();

    Screen screen = view.getScreen();
    screen.screenToWorld(screenLoLeft, loLeft);
    screen.screenToWorld(screenHiLeft, hiLeft);
    screen.screenToWorld(screenHiRight, hiRight);
    screen.screenToWorld(screenLoRight, loRight);

    ParticleBuilder builder = particleBuilder(color);
    World w = view.getWorld();

    line(loLeft, hiLeft, builder, w);
    line(loLeft, loRight, builder, w);
    line(hiLeft, hiRight, builder, w);
    line(loRight, hiRight, builder, w);
  }

  private static ParticleBuilder particleBuilder(Color color) {
    ParticleBuilder builder = Particle.DUST.builder()
        .color(color, 0.33f);

    builder.receivers(
        Bukkit.getOnlinePlayers().stream()
            .filter(player -> player.hasPermission(Permissions.DEBUG))
            .collect(Collectors.toSet())
    );

    return builder;
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
