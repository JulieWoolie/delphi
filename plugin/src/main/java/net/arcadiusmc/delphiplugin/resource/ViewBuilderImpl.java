package net.arcadiusmc.delphiplugin.resource;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.DocumentViewBuilder;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphiplugin.PageManager;
import net.arcadiusmc.delphiplugin.command.PathParser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Getter
public class ViewBuilderImpl implements DocumentViewBuilder {

  private static final Logger LOGGER = Loggers.getLogger("DocumentRequest");

  private ResourcePath path;
  private Player player;
  private Location spawnLocation;

  private final PageManager manager;

  public ViewBuilderImpl(PageManager manager) {
    this.manager = manager;
  }

  @Override
  public DocumentViewBuilder setPath(@NotNull ResourcePath path) {
    Objects.requireNonNull(path, "Null path");
    this.path = path;
    return this;
  }

  @Override
  public DocumentViewBuilder setPath(@NotNull String path) throws DelphiException {
    Objects.requireNonNull(path, "Null path");

    try {
      PathParser<?> parser = new PathParser<>(null, new StringReader(path));
      parser.parsePath();
      this.path = parser.getPath();
    } catch (CommandSyntaxException exc) {
      throw new DelphiException(DelphiException.ERR_SYNTAX, exc.getMessage());
    }

    return this;
  }

  @Override
  public DocumentViewBuilder setPlayer(@NotNull Player player) {
    Objects.requireNonNull(player, "Null player");
    this.player = player;
    return this;
  }

  @Override
  public DocumentViewBuilder setSpawnLocation(@Nullable Location location) {
    this.spawnLocation = location;
    return this;
  }

  @Override
  public Result<DocumentView, DelphiException> open() throws IllegalStateException {
    return manager.openDocument(this);
  }

  @Override
  public DocumentView openOrThrow() throws DelphiException, IllegalStateException {
    return open().getOrThrow(e -> e);
  }

  @Override
  public Optional<DocumentView> openOrLog() throws IllegalStateException {
    return open()
        .ifError(e -> {
          LOGGER.error("Failed to open document at {}", path, e);
        })
        .value();
  }
}
