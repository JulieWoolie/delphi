package net.arcadiusmc.delphi;

import java.util.List;
import java.util.Optional;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.DelphiResources;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.dom.style.StylesheetBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Delphi plugin interface.
 */
public interface Delphi {

  /**
   * Gets the Delphi plugin resources
   * @return Delphi plugin resources
   */
  DelphiResources getResources();

  /**
   * Create a new document request.
   * @return Created document request
   */
  DocumentRequest newRequest();

  /**
   * Attempts to parse a path
   * @param string Parse string
   * @return Parse result, either successful, or an error with the error code
   *         {@code ERR_INVALID_PATH}
   * @see ResourcePath
   */
  Result<ResourcePath, DelphiException> parsePath(String string);

  /**
   * Attempts to open the document specified by the {@code path} and show it to player.
   * <p>
   * Uses {@link DelphiResources#findModule(String)} to find the module of the {@code path}, then
   * creates a {@link ViewResources} object with the module and attempts to call
   * {@link ViewResources#loadDocument(String)} with the path given to this method.
   *
   * <p>
   * If the document is successfully loaded and initialized, it is spawned and then shown to
   * the player, and the created document view is returned.
   * <p>
   * Any erroneous result returned by this method will have an error code dictated by either
   * the {@link DelphiResources#findModule(String)} or {@link ViewResources#loadDocument(String)}
   * methods.
   *
   * @param path Document path
   * @param player Player to show the document to
   *
   * @return The opened view, or an error result explaining why the document couldn't be opened.
   *
   * @see DelphiResources#findModule(String)
   * @see ViewResources#loadDocument(String)
   *
   * @throws NullPointerException If {@code path} or {@code player} are {@code null}
   */
  Result<DocumentView, DelphiException> openDocument(@NotNull ResourcePath path, @NotNull Player player);

  /**
   * Attempts to open the document specified by the {@code path} and show it to player.
   * <p>
   * Uses {@link DelphiResources#findModule(String)} to find the module of the {@code path}, then
   * creates a {@link ViewResources} object with the module and attempts to call
   * {@link ViewResources#loadDocument(String)} with the path given to this method.
   *
   * <p>
   * If the document is successfully loaded and initialized, it is spawned and then shown to
   * the player, and the created document view is returned.
   * <p>
   * Any erroneous result returned by this method will have an error code dictated by either
   * the {@link DelphiResources#findModule(String)}, {@link #parsePath(String)} or
   * {@link ViewResources#loadDocument(String)} methods.
   *
   * @param path Document path
   * @param player Player to show the document to
   *
   * @return The opened view, or an error result explaining why the document couldn't be opened.
   *
   * @see DelphiResources#findModule(String)
   * @see ViewResources#loadDocument(String)
   * @see #parsePath(String)
   *
   * @throws NullPointerException If {@code path} or {@code player} are {@code null}
   */
  Result<DocumentView, DelphiException> openDocument(@NotNull String path, @NotNull Player player);

  /**
   * Gets all the document views a player has open
   *
   * @param player Player
   * @return Unmodifiable open view list, may be empty.
   *
   * @throws NullPointerException if {@code player} is {@code null}
   */
  List<DocumentView> getOpenViews(@NotNull Player player);

  /**
   * Gets all document views currently open
   * @return Unmodifiable open view list, may be empty.
   */
  List<DocumentView> getAllViews();

  /**
   * Get the view currently being looked at by the specified {@code player}.
   * @param player Player
   *
   * @return An optional containing the view the player is currently looking at, or an empty
   *         optional, if the {@code player} is not looking at any document views.
   *
   * @throws NullPointerException If {@code player} is {@code null}
   *
   * @apiNote This method will only return a selected view that belongs to the {@code player}
   *          ({@link DocumentView#getPlayer()} is equal to the specified {@code player}).
   *
   * @see #getAnyTargetedView(Player) Accessing any view a player is looking at
   */
  Optional<DocumentView> getSelectedView(@NotNull Player player);

  /**
   * Get the view currently being looked at by the specified {@code player}, regardless of who that
   * view belongs to.
   * <p>
   * Unlike {@link #getSelectedView(Player)} this method will ignore who a view belongs to, and will
   * get the closest view the player is looking at.
   *
   * @apiNote Iterates through all alive views and tests if their world matches the {@code player}'s
   *          world, and then performs a ray scan on the view to test if it's being looked at.
   *
   * @param player Player
   *
   * @return An optional containing the closest the document view the player is looking at, or an
   *         empty optional, if the player is not looking at any views.
   *
   * @throws NullPointerException If {@code player} is {@code null}
   */
  Optional<DocumentView> getAnyTargetedView(@NotNull Player player);

  /**
   * Create a new stylesheet builder.
   * @return New stylesheet builder
   */
  @NotNull StylesheetBuilder newStylesheetBuilder();
}
