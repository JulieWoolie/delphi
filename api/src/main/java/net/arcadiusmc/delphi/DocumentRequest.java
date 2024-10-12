package net.arcadiusmc.delphi;

import java.util.Optional;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface representing a request for a document
 */
public interface DocumentRequest {

  /**
   * Get the path the request will be sent to
   * @return Request path
   */
  @Nullable ResourcePath getPath();

  /**
   * Set the resource path the document will be fetched from.
   *
   * @param path Document resource path
   * @return {@code this}
   *
   * @throws NullPointerException If {@code path} is {@code null}
   */
  DocumentRequest setPath(@NotNull ResourcePath path);

  /**
   * Set the resource path the document will be fetched from.
   * <p>
   * Shorthand for calling {@link #setPath(ResourcePath)} and {@link Delphi#parsePath(String)}
   *
   * @param path Document resource path
   *
   * @return {@code this}
   *
   * @throws NullPointerException If {@code path} is {@code null}.
   * @throws DelphiException If the {@code path} cannot be parsed.
   */
  DocumentRequest setPath(@NotNull String path) throws DelphiException;

  /**
   * Get the player the opened document will be linked to.
   * @return Request player, or {@code null}, if not yet set.
   */
  @Nullable Player getPlayer();

  /**
   * Set the player the opened document will be linked to.
   * @param player Request player
   * @return {@code this}
   * @throws NullPointerException If {@code player} is {@code null}
   */
  DocumentRequest setPlayer(@NotNull Player player);

  /**
   * Get the set spawn location for the opened document.
   * <p>
   * If this is not set, the document will be spawned in front of the
   * player, facing the player.
   *
   * @return Document spawn location
   */
  @Nullable Location getSpawnLocation();

  /**
   * Set the spawn location of the opened document.
   * <p>
   * If set to null, then the document will be spawned in front of the player,
   * facing the player.
   *
   * @param location Spawn location
   * @return {@code this}
   */
  DocumentRequest setSpawnLocation(@Nullable Location location);

  /**
   * Send the page open request. Functions identically to
   * {@link Delphi#openDocument(ResourcePath, Player)}.
   *
   * @return Opening result.
   * @throws NullPointerException If either {@link #getPlayer()} or {@link #getPath()} is unset
   */
  Result<DocumentView, DelphiException> open() throws NullPointerException;

  /**
   * Invokes {@link #open()} and if an erroneous result is returned, the exception is thrown.
   *
   * @return Opened document view.
   * @throws DelphiException If the document failed to be opened for any reason
   * @throws NullPointerException If either {@link #getPlayer()} or {@link #getPath()} is unset
   */
  DocumentView openOrThrow() throws DelphiException, NullPointerException;

  /**
   * Invokes {@link #open()} and if an erroneous result is returned, it is logged and an empty
   * optional returned. If the {@link #open()} invocation succeeds, an optional containing the
   * opened view is returned.
   *
   * @return View optional
   * @throws NullPointerException If either {@link #getPlayer()} or {@link #getPath()} is unset
   */
  Optional<DocumentView> openOrLog() throws NullPointerException;
}
