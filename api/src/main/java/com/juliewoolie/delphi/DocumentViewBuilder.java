package com.juliewoolie.delphi;

import java.util.Collection;
import java.util.Optional;
import com.juliewoolie.delphi.resource.DelphiException;
import com.juliewoolie.delphi.resource.DelphiResources;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphi.resource.ViewResources;
import com.juliewoolie.delphi.util.Result;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for building a document view.
 *
 * <p>
 * When creating a document view, if more than 1 player is specified, then it's required that
 * {@link #setSpawnLocation(Location)} also be set explicitly.
 * <br>
 * This is because the system for automatically determining for where to spawn a page only works
 * if there's exactly 1 player in the {@link #getPlayers()} player set.
 * <br>
 * If it's empty, then it doesn't know where to spawn, if there's more than 1, then it's not
 * possible to know which player it should be spawned in front of, as the players may be in
 * different locations and different worlds on the server.
 */
public interface DocumentViewBuilder {

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
  DocumentViewBuilder setPath(@NotNull ResourcePath path);

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
   * @throws DelphiException (With Error code {@link DelphiException#ERR_INVALID_PATH})
   *                         If the {@code path} cannot be parsed.
   */
  DocumentViewBuilder setPath(@NotNull String path) throws DelphiException;

  /**
   * Get the currently set instance name.
   * <p>
   * Instance names are unique names given to each page used to differentiate
   * them from other open pages of the same module or same resource path.
   *
   * @return Instance name
   */
  @Nullable String getInstanceName();

  /**
   * Set the instance name of the view.
   * <p>
   * Instance names are unique names given to each page used to differentiate
   * them from other open pages of the same module or same resource path.
   *
   * @param instanceName New instance name
   * @return {@code this}
   */
  DocumentViewBuilder setInstanceName(@Nullable String instanceName);

  /**
   * Get the player the opened document will be linked to.
   * <p>
   * Unless any of the player set methods have been called, this will return a set of all
   * online players.
   *
   * @return Request player.
   */
  PlayerSet getPlayers();

  /**
   * Set the player the opened document will be linked to.
   * <p>
   * If there are any other players in the {@link #getPlayers()} set, then they will be removed and
   * only the specified player will be in that set.
   *
   * @param player player
   * @return {@code this}
   * @throws NullPointerException If {@code player} is {@code null}
   */
  DocumentViewBuilder setPlayer(@NotNull Player player);

  /**
   * Set the players that resulting view will be visible and interactable to.
   *
   * @param players Player collection
   * @return {@code this}
   * @throws NullPointerException If {@code players}, or any element in {@code players}
   *                              is {@code null}
   */
  DocumentViewBuilder setPlayers(@NotNull Collection<Player> players);

  /**
   * Add players that resulting view will be visible and interactable to.
   * <p>
   * If {@link #allPlayers()} has been called before this, then this acts identically to
   * {@link #setPlayers(Collection)}.
   *
   * @param players Player collection
   * @return {@code this}
   * @throws NullPointerException If {@code players}, or any element in {@code players}
   *                              is {@code null}
   */
  DocumentViewBuilder addPlayers(@NotNull Collection<Player> players);

  /**
   * Add a player to the resulting view.
   * <p>
   * Adding a player to the view means the player can see and use the document page.
   * <p>
   * If {@link #allPlayers()} has been called, this method acts identically to
   * {@link #setPlayer(Player)}.
   *
   * @param player Player
   * @return {@code this}
   * @throws NullPointerException If {@code player} is {@code null}
   * @see #setPlayer(Player)
   */
  DocumentViewBuilder addPlayer(@NotNull Player player);

  /**
   * Make the resulting view visible to all online players.
   * <p>
   * This means the resulting view will be shown to all players and can be used by all players.
   *
   * @return {@code this}
   */
  DocumentViewBuilder allPlayers();

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
  DocumentViewBuilder setSpawnLocation(@Nullable Location location);

  /**
   * Attempts to open the document specified by {@link #getPath()} and show it to the
   * {@link #getPlayers()}.
   * <p>
   * Uses {@link DelphiResources#findModule(String)} to find the module of the {@link #getPath()},
   * then creates a {@link ViewResources} object with the module and attempts to call
   * {@link ViewResources#loadDocument(String)} with the path given to this method.
   * <p>
   * If the document is successfully loaded and initialized, it is spawned and then shown to
   * the players, and the created document view is returned.
   * <p>
   * Any erroneous result returned by this method will have an error code dictated by either
   * the {@link DelphiResources#findModule(String)} or {@link ViewResources#loadDocument(String)}
   * methods.
   * <table>
   *   <caption>Special error codes</caption>
   *   <tr>
   *     <th>Error code</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_INSTANCE_NAME_USED}</td>
   *     <td>The specified {@link #getInstanceName()} is already in use</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_ILLEGAL_INSTANCE_NAME}</td>
   *     <td>
   *       The specified {@link #getInstanceName()} is not allowed
   *       <br>
   *       Most likely because its usage would interfere with a command.
   *     </td>
   *   </tr>
   * </table>
   *
   * @return The opened view, or an error result explaining why the document couldn't be opened.
   *
   * @throws NullPointerException  If {@link #getPath()} is unset
   * @throws IllegalStateException If {@link #getPlayers()} does not contain exactly 1 player, and
   *                               no {@link #getSpawnLocation()} has been set.
   *                               <br>
   *                               This means if the player set is empty, or contains more than 1
   *                               player, the system cannot figure out where to spawn the page by
   *                               itself, and requires a spawn location to be set.
   */
  Result<DocumentView, DelphiException> open() throws NullPointerException, IllegalStateException;

  /**
   * Invokes {@link #open()} and if an erroneous result is returned, the exception is thrown.
   *
   * @return Opened document view.
   *
   * @throws DelphiException       If the document failed to be opened for any reason
   * @throws NullPointerException  If {@link #getPath()} is unset
   * @throws IllegalStateException If {@link #getPlayers()} does not contain exactly 1 player, and
   *                               no {@link #getSpawnLocation()} has been set.
   *                               <br>
   *                               This means if the player set is empty, or contains more than 1
   *                               player, the system cannot figure out where to spawn the page by
   *                               itself, and requires a spawn location to be set.
   */
  DocumentView openOrThrow() throws NullPointerException, IllegalStateException;

  /**
   * Invokes {@link #open()} and if an erroneous result is returned, it is logged and an empty
   * optional returned. If the {@link #open()} invocation succeeds, an optional containing the
   * opened view is returned.
   *
   * @return View optional
   *
   * @throws NullPointerException  If {@link #getPath()} is unset
   * @throws IllegalStateException If {@link #getPlayers()} does not contain exactly 1 player, and
   *                               no {@link #getSpawnLocation()} has been set.
   *                               <br>
   *                               This means if the player set is empty, or contains more than 1
   *                               player, the system cannot figure out where to spawn the page by
   *                               itself, and requires a spawn location to be set.
   */
  Optional<DocumentView> openOrLog() throws NullPointerException, IllegalStateException;
}
