package net.arcadiusmc.delphiplugin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.arcadiusmc.delphi.Delphi;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.resource.DelphiResources;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphiplugin.command.PathParser;
import net.arcadiusmc.delphiplugin.math.RayScan;
import net.arcadiusmc.delphiplugin.math.Screen;
import net.arcadiusmc.delphiplugin.resource.Modules;
import net.arcadiusmc.delphiplugin.resource.PageResources;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PageManager implements Delphi {

  private final Modules modules;
  private final SessionManager sessions;

  public PageManager(Modules modules, SessionManager sessions) {
    this.modules = modules;
    this.sessions = sessions;
  }

  @Override
  public Result<ResourcePath, String> parsePath(String string) {
    PathParser<?> parser = new PathParser<>(modules, new StringReader(string));

    try {
      parser.parse();
    } catch (CommandSyntaxException exc) {
      return Result.err("Invalid path");
    }

    return Result.ok(parser.getPath());
  }

  @Override
  public DelphiResources getResources() {
    return modules;
  }

  @Override
  public Result<DocumentView, String> openDocument(
      @NotNull ResourcePath path,
      @NotNull Player player
  ) {
    Objects.requireNonNull(path, "Null path");
    Objects.requireNonNull(player, "Null player");

    String moduleName = path.getModuleName();

    Result<ResourceModule, String> moduleResult = modules.findModule(moduleName)
        .mapError(string -> "Failed to get module '" + moduleName + "': " + string);

    if (moduleResult.isError()) {
      return Result.err(moduleResult);
    }

    // Won't throw, we returned above if error
    ResourceModule module = moduleResult.getOrThrow();
    ResourcePath cwd;

    if (path.elementCount() > 0) {
      int lastIndex = path.elementCount() - 1;
      cwd = path.removeElement(lastIndex);
    } else {
      cwd = path;
      path = path.addElement("index.xml");
    }

    PageResources resources = new PageResources(modules, moduleName, module);
    resources.setCwd(cwd);

    PageView view = new PageView(player, path);
    view.setResources(resources);
    resources.setView(view);

    PlayerSession session = sessions.getOrCreateSession(player);
    session.addView(view);

    Result<DelphiDocument, String> res = resources.loadDocument(path, path.elements())
        .mapError(string -> "Failed to load document: " + string);

    if (res.isError()) {
      session.closeView(view);
      return Result.err(res);
    }

    DelphiDocument doc = res.getOrThrow();

    if (modules.getDefaultStyle() != null) {
      doc.addStylesheet(modules.getDefaultStyle());
    }

    view.initializeDocument(doc);

    Screen screen = view.getScreen();
    float width = screen.getWidth();
    float height = screen.getHeight();

    float centerY = (float) ((player.getY() + player.getEyeHeight()) - (height * 0.5));

    Vector3f center = new Vector3f();
    center.x = (float) player.getX();
    center.y = centerY;
    center.z = (float) player.getZ();

    screen.set(center, width, height);

    view.spawn();

    return Result.ok(view);
  }

  @Override
  public Result<DocumentView, String> openDocument(@NotNull String path, @NotNull Player player) {
    Objects.requireNonNull(path, "Null path");
    Objects.requireNonNull(player, "Null player");

    return parsePath(path).flatMap(p -> openDocument(p, player));
  }

  @Override
  public List<DocumentView> getOpenViews(@NotNull Player player) {
    Objects.requireNonNull(player, "Null player");

    return sessions.getSession(player.getUniqueId())
        .map(session -> new ArrayList<DocumentView>(session.getViews()))
        .orElseGet(ArrayList::new);
  }

  @Override
  public List<DocumentView> getAllViews() {
    List<DocumentView> views = new ArrayList<>();

    for (PlayerSession session : sessions.getSessions()) {
      views.addAll(session.getViews());
    }

    return views;
  }

  @Override
  public Optional<DocumentView> getSelectedView(@NotNull Player player) {
    Objects.requireNonNull(player, "Null player");
    return sessions.getSession(player.getUniqueId()).map(PlayerSession::getSelectedView);
  }

  @Override
  public Optional<DocumentView> getAnyTargetedView(@NotNull Player player) {
    Objects.requireNonNull(player, "Null player");

    List<ViewDist> views = new ArrayList<>();
    World world = player.getWorld();
    RayScan ray = RayScan.ofPlayer(player);

    Vector2f screenOut = new Vector2f();
    Vector3f hitOut = new Vector3f();

    for (PlayerSession session : sessions.getSessions()) {
      for (PageView view : session.getViews()) {
        if (!view.getWorld().equals(world)) {
          continue;
        }

        if (!view.getScreen().castRay(ray, hitOut, screenOut)) {
          continue;
        }

        double distSq = hitOut.distanceSquared(ray.getOrigin());
        if (distSq >= ray.getMaxLengthSq()) {
          continue;
        }

        views.add(new ViewDist(view, distSq));
      }
    }

    if (views.isEmpty()) {
      return Optional.empty();
    }
    if (views.size() == 1) {
      return Optional.of(views.getFirst().view());
    }

    views.sort(Comparator.naturalOrder());
    
    return Optional.of(views.getFirst().view());
  }

  record ViewDist(PageView view, double distSq) implements Comparable<ViewDist> {

    @Override
    public int compareTo(@NotNull PageManager.ViewDist o) {
      return Double.compare(distSq, o.distSq);
    }
  }
}
