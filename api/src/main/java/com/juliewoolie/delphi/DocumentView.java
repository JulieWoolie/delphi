package com.juliewoolie.delphi;

import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphi.resource.ViewResources;
import com.juliewoolie.dom.Document;
import org.bukkit.Location;
import org.bukkit.World;
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
   * Get the instance name of this view.
   * <p>
   * Instance names are unique names given to each page used to differentiate
   * them from other open pages of the same module or same resource path.
   *
   * @return Instance name
   */
  String getInstanceName();

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
   * Gets the players this view belongs to
   * @return View players
   */
  PlayerSet getPlayers();

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
   * @return {@code true}, if any player in {@link #getPlayers()} is currently looking at the view,
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
   * Set the current screen transformation.
   * <p>
   * The translation component of the specified transformation becomes the position of the center
   * of the screen
   *
   * @param transformation New screen Transformation
   * @throws NullPointerException If {@code transformation} is {@code null}
   */
  void setScreenTransform(@NotNull Transformation transformation);

  /**
   * Get the current screen transformation.
   * <p>
   * The returned transformation's translation component will be the screen center's position
   *
   * @return View screen's transformation
   */
  @NotNull
  Transformation getScreenTransform();

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

  /**
   * Execute a task after a tick delay.
   * <p>
   * Tasks scheduled with DocumentView's schedulers are only active while the view itself is active.
   * Once closed, all tasks are cancelled.
   *
   * @param tickDelay Tick Delay
   * @param task Task
   *
   * @return Task ID
   *
   * @throws IllegalArgumentException If {@code tickDelay} is less than {@code 1}
   * @throws NullPointerException If {@code task} is {@code null}
   *
   * @see #cancelTask(int)
   * @see #runRepeating(long, long, Runnable)
   */
  int runLater(long tickDelay, @NotNull Runnable task)
      throws NullPointerException, IllegalStateException;

  /**
   * Execute a task repeatedly
   * <p>
   * Tasks scheduled with DocumentView's schedulers are only active while the view itself is active.
   * Once closed, all tasks are cancelled.
   *
   * @param tickDelay Initial Tick Delay
   * @param tickInterval Delay between task repetitions
   * @param task Task
   *
   * @return Task ID
   *
   * @throws NullPointerException If {@code task} is {@code null}
   * @throws IllegalArgumentException If {@code tickInterval} is less than 1
   *
   * @see #cancelTask(int)
   * @see #runLater(long, Runnable)
   */
  int runRepeating(long tickDelay, long tickInterval, @NotNull Runnable task)
      throws NullPointerException, IllegalStateException;

  /**
   * Cancel a scheduled task.
   *
   * @param taskId Task ID
   *
   * @return {@code true}, if the task was cancelled, {@code false}, if it was already cancelled or
   *         had finished execution.
   *
   * @see #runRepeating(long, long, Runnable)
   * @see #runLater(long, Runnable)
   */
  boolean cancelTask(int taskId);
}
