package net.arcadiusmc.delphiplugin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.arcadiusmc.chimera.ChimeraSheetBuilder;
import net.arcadiusmc.delphi.Delphi;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.event.DocumentOpenEvent;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.DelphiResources;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.event.EventImpl;
import net.arcadiusmc.delphiplugin.command.PathParser;
import net.arcadiusmc.delphiplugin.math.RayScan;
import net.arcadiusmc.delphiplugin.resource.Modules;
import net.arcadiusmc.delphiplugin.resource.PageResources;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.style.StylesheetBuilder;
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
  public Result<ResourcePath, DelphiException> parsePath(String string) {
    PathParser<?> parser = new PathParser<>(modules, new StringReader(string));

    try {
      parser.parse();
    } catch (CommandSyntaxException exc) {
      return Result.err(new DelphiException(DelphiException.ERR_INVALID_PATH, exc.getMessage()));
    }

    return Result.ok(parser.getPath());
  }

  @Override
  public DelphiResources getResources() {
    return modules;
  }

  @Override
  public Result<DocumentView, DelphiException> openDocument(
      @NotNull ResourcePath path,
      @NotNull Player player
  ) {
    Objects.requireNonNull(path, "Null path");
    Objects.requireNonNull(player, "Null player");

    String moduleName = path.getModuleName();

    Result<ResourceModule, DelphiException> moduleResult = modules.findModule(moduleName);

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

    Result<DelphiDocument, DelphiException> res = resources.loadDocument(path, path.elements());

    if (res.isError()) {
      session.closeView(view);
      return Result.err(res);
    }

    DelphiDocument doc = res.getOrThrow();

    if (modules.getDefaultStyle() != null) {
      doc.addStylesheet(modules.getDefaultStyle());
    }

    view.initializeDocument(doc);

    EventImpl loaded = new EventImpl(EventTypes.DOM_LOADED, doc);
    loaded.initEvent(null, false, false);
    doc.dispatchEvent(loaded);

    DocumentOpenEvent bukkitEvent = new DocumentOpenEvent(player, view);
    bukkitEvent.callEvent();

    view.spawn();

    EventImpl event = new EventImpl(EventTypes.DOM_SPAWNED, doc);
    event.initEvent(null, false, false);
    doc.dispatchEvent(event);

    return Result.ok(view);
  }

  @Override
  public Result<DocumentView, DelphiException> openDocument(@NotNull String path, @NotNull Player player) {
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

    World world = player.getWorld();
    RayScan ray = RayScan.ofPlayer(player);

    Vector2f screenOut = new Vector2f();
    Vector3f hitOut = new Vector3f();

    float closestDistSq = Float.MAX_VALUE;
    PageView closest = null;

    for (PlayerSession session : sessions.getSessions()) {
      for (PageView view : session.getViews()) {
        if (!view.getWorld().equals(world)) {
          continue;
        }

        if (!view.getScreen().castRay(ray, hitOut, screenOut)) {
          continue;
        }

        float distSq = hitOut.distanceSquared(ray.getOrigin());
        if (distSq >= ray.getMaxLengthSq()) {
          continue;
        }

        if (distSq >= closestDistSq) {
          continue;
        }

        closestDistSq = distSq;
        closest = view;
      }
    }

    return Optional.ofNullable(closest);
  }

  @Override
  public @NotNull StylesheetBuilder newStylesheetBuilder() {
    return new ChimeraSheetBuilder();
  }
}
