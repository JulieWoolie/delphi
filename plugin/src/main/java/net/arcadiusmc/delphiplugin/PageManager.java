package net.arcadiusmc.delphiplugin;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import net.arcadiusmc.delphi.Delphi;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphi.resource.DelphiResources;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphiplugin.command.PathParser;
import net.arcadiusmc.delphiplugin.resource.Modules;
import net.arcadiusmc.delphiplugin.resource.PageResources;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PageManager implements Delphi {

  private static final Logger LOGGER = Loggers.getLogger("Delphi");

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

    // Won't throw, we return above
    ResourceModule module = moduleResult.getOrThrow();
    ResourcePath cwd = path;

    if (cwd.elementCount() > 0) {
      int lastIndex = cwd.elementCount() - 1;
      cwd = cwd.removeElement(lastIndex);
    }

    PageResources resources = new PageResources(modules, moduleName, module);
    resources.setCwd(cwd);

    PageView view = new PageView(player, path);
    view.setResources(resources);

    PlayerSession session = sessions.getOrCreateSession(player);
    session.addView(view);

    Result<DelphiDocument, String> res = resources.loadDocument(path, path.path() + path.query())
        .mapError(string -> "Failed to load document: " + string);

    if (res.isError()) {
      session.removeView(view);
      return Result.err(res);
    }

    view.initializeDocument(res.getOrThrow());

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
    return List.of();
  }

  @Override
  public List<DocumentView> getAllViews() {
    return List.of();
  }
}
