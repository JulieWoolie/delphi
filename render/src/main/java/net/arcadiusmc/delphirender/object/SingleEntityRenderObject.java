package net.arcadiusmc.delphirender.object;

import net.arcadiusmc.delphirender.Consts;
import net.arcadiusmc.delphirender.RenderSystem;
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

    float zIndexDepth;
    if (parent != null) {
      zIndexDepth = parent.style.zindex * Consts.MACRO_LAYER_DEPTH;
    } else {
      zIndexDepth = 0.0f;
    }

    offset.x += size.x * 0.5f;
    offset.y -= size.y;
    offset.z = depth + zIndexDepth;

    configure(entity, trans);
    project(trans);

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
