package net.arcadiusmc.delphi;

import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.dom.Document;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

public interface DocumentView {

  /**
   * Gets the document that belongs to this view
   * @return View document
   */
  Document getDocument();

  /**
   * Gets the screen that this view is rendered with
   * @return View Screen
   */
  Screen getScreen();

  /**
   * Gets the position of the player's cursor in screen coordinates.
   * @return Cursor screen position, or {@code null}, if the player is not looking at the screen
   */
  Vector2f getCursorScreenPosition();

  /**
   * Gets the position of the player's cursor along the screen in world coordinates.
   * @return Cursor world position, or {@code null}, if the player is not looking at the screen
   */
  Vector3f getCursorWorldPosition();

  /**
   * Gets the path of the opened document
   * @return Document path
   */
  ResourcePath getPath();

  /**
   * Gets the resources of this view
   * @return View resources
   */
  ViewResources getResources();

  /**
   * Gets the player this view belongs to
   * @return View player
   */
  Player getPlayer();

  /**
   * Gets the world the view is spawned in.
   * @return View world.
   */
  World getWorld();

  /**
   * Closes this view
   */
  void close();

  /**
   * Tests if this view is currently being looked at by the player.
   *
   * @return {@code true}, if {@link #getPlayer()} is currently looking at the view,
   *         {@code false} otherwise.
   */
  boolean isSelected();
}
