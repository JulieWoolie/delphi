package com.juliewoolie.delphi.event;

import com.juliewoolie.delphi.DocumentView;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class DocumentViewMoveEvent extends Event {

  private static final HandlerList handlerList = new HandlerList();

  private final DocumentView view;
  private final World world;
  private final Vector3d position;

  public DocumentViewMoveEvent(DocumentView view, World world, Vector3d position) {
    Objects.requireNonNull(view, "Null view");
    Objects.requireNonNull(world, "Null world");
    Objects.requireNonNull(position, "Null position");

    this.view = view;
    this.world = world;
    this.position = position;
  }

  /**
   * Get the view being moved
   * @return Moving view
   */
  public @NotNull DocumentView getView() {
    return view;
  }

  /**
   * Get the location the view is being moved to
   * @return New view location
   */
  public @NotNull Location getNewLocation() {
    return new Location(world, position.x, position.y, position.z);
  }

  /**
   * Get the world the view is being moved to
   * @return New view world
   */
  public World getWorld() {
    return world;
  }

  /**
   * Get the position the view is being moved to
   * @return New view position
   */
  public Vector3d getPosition() {
    return new Vector3d(position);
  }

  public static HandlerList getHandlerList() {
    return handlerList;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlerList;
  }
}
