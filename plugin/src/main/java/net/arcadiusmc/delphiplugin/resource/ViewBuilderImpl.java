package net.arcadiusmc.delphiplugin.resource;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.DocumentViewBuilder;
import net.arcadiusmc.delphi.PlayerSet;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphiplugin.AllPlayersSet;
import net.arcadiusmc.delphiplugin.DelphiImpl;
import net.arcadiusmc.delphiplugin.PlayerSetImpl;
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
  private PlayerSet players = new AllPlayersSet();
  private Location spawnLocation;
  private String instanceName;

  private final DelphiImpl manager;

  public ViewBuilderImpl(DelphiImpl manager) {
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
  public DocumentViewBuilder setInstanceName(@Nullable String instanceName) {
    this.instanceName = instanceName;
    return this;
  }

  @Override
  public DocumentViewBuilder setPlayer(@NotNull Player player) {
    Objects.requireNonNull(player, "Null player");

    this.players = new PlayerSetImpl();
    this.players.add(player);

    return this;
  }

  @Override
  public DocumentViewBuilder setPlayers(@NotNull Collection<Player> players) {
    Objects.requireNonNull(players, "Null players");

    PlayerSetImpl set = new PlayerSetImpl();
    set.addAll(players);

    this.players = set;

    return this;
  }

  @Override
  public DocumentViewBuilder addPlayers(@NotNull Collection<Player> players) {
    Objects.requireNonNull(players, "Null players");

    if (this.players instanceof AllPlayersSet) {
      return setPlayers(players);
    }

    this.players.addAll(players);
    return this;
  }

  @Override
  public DocumentViewBuilder addPlayer(@NotNull Player player) {
    Objects.requireNonNull(player, "Null player");

    if (players instanceof AllPlayersSet) {
      return setPlayer(player);
    }

    this.players.add(player);
    return this;
  }

  @Override
  public DocumentViewBuilder allPlayers() {
    this.players = new AllPlayersSet();
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

  public void validate() {
    Objects.requireNonNull(path, "Null page path");

    if (players.size() == 1) {
      return;
    }
    if (spawnLocation != null) {
      return;
    }

    throw new IllegalStateException(
        "Cannot automatically determine page spawn, please call setSpawnLocation"
    );
  }
}
