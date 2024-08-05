package net.arcadiusmc.delphi;

import java.util.List;
import java.util.Optional;
import net.arcadiusmc.delphi.resource.DelphiResources;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphi.util.Result;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Delphi {

  /**
   * Gets the Delphi plugin resources
   * @return Delphi plugin resources
   */
  DelphiResources getResources();

  /**
   * Attempts to parse a path
   * @param string Parse string
   * @return Parse result, either successful, or an error with the message "Invalid path"
   * @see ResourcePath
   */
  Result<ResourcePath, String> parsePath(String string);

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
   * <br>
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Error format</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code "Failed to get module %module-name%: %reason%"}</td>
   *     <td>Means {@link DelphiResources#findModule(String)} failed, {@code %reason%} contains the error output of the module finding result</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Failed to load document: %reason%"}</td>
   *     <td>Means {@link ViewResources#loadDocument(String)} failed, {@code %reason%} contains error output of the load result</td>
   *   </tr>
   * </table>
   *
   * @param path Document path
   * @param player Player to show the document to
   *
   * @return The opened view, or an error result explaining why the document couldn't be opened.
   *
   * @throws NullPointerException If {@code path} or {@code player} are {@code null}
   */
  Result<DocumentView, String> openDocument(@NotNull ResourcePath path, @NotNull Player player);

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
   * <br>
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Error format</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code "Invalid path"}</td>
   *     <td>If the specified {@code path} couldn't be parsed into a {@link ResourcePath}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Failed to get module %module-name%: %reason%"}</td>
   *     <td>Means {@link DelphiResources#findModule(String)} failed, {@code %reason%} contains the error output of the module finding result</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Failed to load document: %reason%"}</td>
   *     <td>Means {@link ViewResources#loadDocument(String)} failed, {@code %reason%} contains error output of the load result</td>
   *   </tr>
   * </table>
   *
   * @param path Document path
   * @param player Player to show the document to
   *
   * @return The opened view, or an error result explaining why the document couldn't be opened.
   *
   * @throws NullPointerException If {@code path} or {@code player} are {@code null}
   */
  Result<DocumentView, String> openDocument(@NotNull String path, @NotNull Player player);

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
   *
   * @param player Player
   *
   * @return An optional containing the view the player is currently looking at, or an empty
   *         optional, if the {@code player} is not looking at any document views.
   *
   * @throws NullPointerException If {@code player} is {@code null}
   */
  Optional<DocumentView> getSelectedView(@NotNull Player player);
}
