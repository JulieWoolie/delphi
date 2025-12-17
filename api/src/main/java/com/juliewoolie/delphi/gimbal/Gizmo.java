package com.juliewoolie.delphi.gimbal;

import java.util.Collection;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public interface Gizmo {

  /**
   * Get the position of the gimbal.
   * @return A copy of the gimbal's position
   */
  @NotNull Vector3d getPosition();

  /**
   * Move the gimbal to the specified X, Y and Z coordinates
   *
   * @param x X coordinate
   * @param y Y coordinate
   * @param z Z coordinate
   *
   * @throws IllegalArgumentException If any of the specified coordinates are {@code NaN}
   */
  void moveTo(double x, double y, double z) throws IllegalArgumentException;

  /**
   * Move the gimbal to the specified X, Y and Z coordinates
   *
   * @param pos Gimbal's new position
   *
   * @throws NullPointerException If the specified {@code pos} is {@code null}.
   * @throws IllegalArgumentException If any of the specified coordinates are {@code NaN}
   */
  void moveTo(@NotNull Vector3dc pos) throws NullPointerException, IllegalArgumentException;

  /**
   * Move the gimbal to the specified position and world, the location's yaw and pitch are ignored.
   *
   * @param location Gimbal's new location
   *
   * @throws NullPointerException If the specified {@code location} is {@code null}
   * @throws NullPointerException If the specified {@code location}'s world is {@code null}
   * @throws IllegalArgumentException If any coordinate in the specified {@code location} is {@code NaN}
   */
  void moveTo(@NotNull Location location) throws IllegalArgumentException, NullPointerException;

  /**
   * Get the world the gimbal is located in
   * @return Gimbal world
   */
  @NotNull World getWorld();

  /**
   * Get the base transformation applied to the gimbal
   * @return Base transformation applied to the gimbal
   */
  @NotNull Transformation getBaseTransform();

  /**
   * Get the player the gimbal is visible to
   * @return Player
   */
  @NotNull Player getPlayer();

  /**
   * Set the player the gimbal is visible to
   *
   * @param player New player
   *
   * @throws NullPointerException If the specified {@code player} is {@code null}
   */
  void setPlayer(@NotNull Player player) throws NullPointerException;

  /**
   * Kill the gimbal
   */
  void kill();

  /**
   * Spawn the gimbal
   */
  void spawn();

  /**
   * Check if the gimbal is active
   *
   * @return {@code true}, if the gimbal is visible to the player and can be used,
   *         {@code false} otherwise.
   */
  boolean isActive();

  /**
   * Set the base transformation
   * @param trans Base transformation, if {@code null}, an identity Transformation will be used
   */
  void setBaseTransform(@Nullable Transformation trans);

  /**
   * Get the applied transformation of the gimbal.
   * <p>
   * The returned value contains the movement, scale and rotation actions that have been set by a
   * player using the gimbal.
   *
   * @return Gimbal applied transform.
   */
  @NotNull Transformation getAppliedTransform();

  /**
   * Reset the {@link #getAppliedTransform()}.
   */
  void resetAppliedTransform();

  /**
   * Set the applied transformation
   * @param transform New transform, if {@code null}, resets the transform back to default.
   */
  void setAppliedTransform(@Nullable Transformation transform);

  /**
   * Get an immutable set of gimbal abilities.
   * <p>
   * The returned set defines what can be edited with this gimbal. It also determines which
   * components are visible to the player.
   * <p>
   * Unless changed with {@link #setAbilities(Collection)}, the returned set contains
   * every ability.
   *
   * @return Gimbal abilities
   */
  Set<GizmoAbility> getAbilities();

  /**
   * Set the gimbal abilities
   * <p>
   * The specified collection will define what can be edited with the gimbal. It also determines
   * which components are visible to the player.
   *
   * @param abilities Abilities collection
   *
   * @throws IllegalArgumentException If the specified {@code abilities} are empty
   * @throws NullPointerException If the specified {@code abilities} is {@code null}
   */
  void setAbilities(@NotNull Collection<GizmoAbility> abilities)
      throws IllegalArgumentException, NullPointerException;
}
