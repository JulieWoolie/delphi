package net.arcadiusmc.delphi;

import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.dom.Document;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * An active view of a document that's been spawned in the world.
 */
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
   * Test if the view is closed
   * @return {@code true}, if the view has been closed, {@code false} if it's open.
   */
  boolean isClosed();

  /**
   * Tests if this view is currently being looked at by the player.
   *
   * @return {@code true}, if {@link #getPlayer()} is currently looking at the view,
   *         {@code false} otherwise.
   */
  boolean isSelected();

  /**
   * Apply a transformation to the view's screen.
   *
   * @param transformation Transformation to apply to the screen
   *
   * @throws NullPointerException If {@code transformation} is {@code null}
   */
  void transform(@NotNull Transformation transformation);

  /**
   * Moves the page to the specified {@code location}.
   * <p>
   * The view will be moved so the bottom middle is at the {@code location}.
   * The specified {@code location}'s yaw and pitch become the screen's rotation.
   *
   * @param location New location
   * @throws NullPointerException If {@code location} is {@code null}
   */
  void moveTo(@NotNull Location location);

  /**
   * Moves the page to the specified {@code location}.
   * <p>
   * The view ill be moved so the bottom middle is at the specified {@code location}.
   * <p>
   * If {@code #changeRotation} is set to {@code true}, the specified {@code location}'s
   * yaw and pitch will become the screen's rotation.
   *
   * @param location New location
   * @param changeRotation {@code true}, to change the screen's rotation, {@code false} otherwise
   *
   * @throws NullPointerException If {@code location} is {@code null}
   */
  void moveTo(@NotNull Location location, boolean changeRotation);

  /**
   * Moves the page to the specified {@code position} in the specified {@code world}.
   * <p>
   * The view will be moved so the bottom middle is at the specified {@code position}
   *
   * @param world New world
   * @param position New position
   *
   * @throws NullPointerException If {@code world} or {@code position} is {@code null}
   */
  void moveTo(@NotNull World world, @NotNull Vector3f position);

  /**
   * Moves the page to the specified {@code position}.
   * <p>
   * The view will be moved so the bottom middle is at the specified {@code position}
   *
   * @param position New position
   *
   * @throws NullPointerException If {@code position} is {@code null}
   */
  void moveTo(@NotNull Vector3f position);
}
