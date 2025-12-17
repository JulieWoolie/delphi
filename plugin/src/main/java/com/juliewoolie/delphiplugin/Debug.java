package com.juliewoolie.delphiplugin;

import com.destroystokyo.paper.ParticleBuilder;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.delphiplugin.command.Permissions;
import com.juliewoolie.delphiplugin.math.Screen;
import com.juliewoolie.delphirender.RenderTreePrint;
import com.juliewoolie.delphirender.math.Rectangle;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.Visitor;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.slf4j.Logger;

public final class Debug {
  private Debug() {}

  private static final Logger LOGGER = Loggers.getLogger();
  public static boolean debugOutlines = false;
  static final float POINT_DIST = 0.12f;

  public static Path dumpDebugTree(String fileName, PageView view, @Nullable Element target) {
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

    RenderTreePrint print = new RenderTreePrint(view, view.renderer);

    if (target == null) {
      print.appendDocumentInfo();
      Visitor.visit(view.getDocument().getDocumentElement(), print);
    } else {
      Visitor.visit(target, print);
    }

    String string = print.toString();

    try {
      Files.writeString(dumpFile, string, StandardCharsets.UTF_8);
    } catch (IOException exc) {
      Loggers.getDocumentLogger().error("Failed to dump XML info", exc);
      return null;
    }

    return dumpFile;
  }

  public static void drawScreen(Screen screen, World world) {
    Vector3d loRight = screen.getLowerRight();
    Vector3d hiRight = screen.getUpperRight();
    Vector3d loLeft = screen.getLowerLeft();
    Vector3d hiLeft = screen.getUpperLeft();

    Vector3d center = screen.center();
    Vector3d normal = screen.normal();

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

    Vector3d loLeft = new Vector3d();
    Vector3d hiLeft = new Vector3d();
    Vector3d loRight = new Vector3d();
    Vector3d hiRight = new Vector3d();

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

  public static ParticleBuilder particleBuilder(Color color) {
    ParticleBuilder builder = Particle.DUST.builder()
        .color(color, 0.33f);

    builder.receivers(
        Bukkit.getOnlinePlayers().stream()
            .filter(player -> player.hasPermission(Permissions.DEBUG))
            .collect(Collectors.toSet())
    );

    return builder;
  }

  public static void line(Vector3d origin, Vector3d end, ParticleBuilder builder, World world) {
    Vector3d dir = new Vector3d(end).sub(origin);

    double len = dir.length();
    dir.normalize();

    Vector3d point = new Vector3d();

    for (float c = 0; c <= len; c += POINT_DIST) {
      dir.mul(c, point);
      point.add(origin);

      builder.location(world, point.x, point.y, point.z)
          .spawn();
    }
  }
}
