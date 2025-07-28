package com.juliewoolie.delphirender.object;

import com.juliewoolie.delphirender.RenderSystem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

public abstract class SingleEntityRenderObject<T extends Display> extends RenderObject {

  public T entity;

  public SingleEntityRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  public void moveTo(float x, float y) {
    super.moveTo(x, y);

    if (!isSpawned()) {
      return;
    }

    Location loc = getLocation();
    entity.teleport(loc);
  }

  boolean isSpawned() {
    return entity != null && !entity.isDead();
  }

  protected abstract T spawnEntity(World w, Location l);

  protected void configure(T entity, Transformation trans) {

  }

  @Override
  public void spawn() {
    Location location = getLocation();

    if (!isSpawned()) {
      entity = spawnEntity(location.getWorld(), location);
      system.addEntity(entity);
      configureEntity(entity);
    } else {
      entity.teleport(location);
    }

    Transformation trans = newTransform();
    Vector3f offset = trans.getTranslation();

    //
    // === The offset stuff, explanation ===
    //
    // Let 'O' be the origin point of the entity
    //
    // Starting out, all spawned text displays start like this:
    // +---+
    // |   |
    // +-O-+
    //   ^ Origin at the bottom middle
    //
    // Item displays start a bit differently:
    // +---+
    // | O | <- Origin in the middle
    // +---+
    // But we don't GAF about that rn.
    //
    // We need the origin to be at the top left, as that's how elements are
    // calculated.
    //
    // So we offset the entity by half it's width and by its height to
    // achieve this:
    // O---+
    // |   |
    // +---+
    // Origin point at the top left
    //

    offset.x += size.x * 0.5f;
    offset.y -= size.y;
    offset.z = depth + getZIndexDepth();

    configure(entity, trans);
    screen.project(trans);

    entity.setTransformation(trans);
  }

  @Override
  public void kill() {
    if (!isSpawned()) {
      return;
    }

    system.removeEntity(entity);
    entity.remove();

    entity = null;
  }
}
