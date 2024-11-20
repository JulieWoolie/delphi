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
import net.arcadiusmc.delphi.DocumentViewBuilder;
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
import net.arcadiusmc.delphiplugin.resource.PageResources;
import net.arcadiusmc.delphiplugin.resource.PluginResources;
import net.arcadiusmc.delphiplugin.resource.ViewBuilderImpl;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.style.StylesheetBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PageManager implements Delphi {

  private final DelphiPlugin plugin;
  private final PluginResources pluginResources;
  private final SessionManager sessions;

  public PageManager(DelphiPlugin plugin, PluginResources pluginResources, SessionManager sessions) {
    this.plugin = plugin;
    this.pluginResources = pluginResources;
    this.sessions = sessions;
  }

  @Override
  public Result<ResourcePath, DelphiException> parsePath(String string) {
    PathParser<?> parser = new PathParser<>(pluginResources, new StringReader(string));

    try {
      parser.parse();
    } catch (CommandSyntaxException exc) {
      return Result.err(new DelphiException(DelphiException.ERR_INVALID_PATH, exc.getMessage()));
    }

    return Result.ok(parser.getPath());
  }

  @Override
  public DelphiResources getResources() {
    return pluginResources;
  }

  @Override
  public DocumentViewBuilder newViewBuilder() {
    return new ViewBuilderImpl(this);
  }

  @Override
  public Result<DocumentView, DelphiException> openDocument(
      @NotNull ResourcePath path,
      @NotNull Player player
  ) {
    ViewBuilderImpl req = new ViewBuilderImpl(this);
    req.setPath(path);
    req.setPlayer(player);
    return openDocument(req);
  }

  @Override
  public Result<DocumentView, DelphiException> openDocument(
      @NotNull String path,
      @NotNull Player player
  ) {
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

  public Result<DocumentView, DelphiException> openDocument(ViewBuilderImpl request) {
    ResourcePath path = request.getPath();
    Player player = request.getPlayer();

    Objects.requireNonNull(path, "Path not set");
    Objects.requireNonNull(player, "Player not set");

    String moduleName = path.getModuleName();

    Result<ResourceModule, DelphiException> moduleResult = pluginResources.findModule(moduleName);

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

    PageResources resources = new PageResources(pluginResources, moduleName, module);
    resources.setCwd(cwd);

    PageView view = new PageView(plugin, player, path);
    view.setResources(resources);
    view.setFontMetrics(plugin.getMetrics());
    resources.setView(view);

    PlayerSession session = sessions.getOrCreateSession(player);
    session.addView(view);

    Result<DelphiDocument, DelphiException> res = resources.loadDocument(path, path.elements());

    if (res.isError()) {
      session.closeView(view);
      return Result.err(res);
    }

    DelphiDocument doc = res.getOrThrow();

    if (pluginResources.getDefaultStyle() != null) {
      doc.addStylesheet(pluginResources.getDefaultStyle());
    }

    view.initializeDocument(doc);

    if (request.getSpawnLocation() != null) {
      Location loc = request.getSpawnLocation();
      view.moveTo(loc);
    } else {
      view.configureScreen();
    }

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
}
