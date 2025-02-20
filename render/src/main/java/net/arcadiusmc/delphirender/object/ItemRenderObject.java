package net.arcadiusmc.delphirender.object;

import static net.arcadiusmc.delphidom.Consts.ITEM_SPRITE_SIZE;

import net.arcadiusmc.delphirender.RenderSystem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ItemRenderObject extends SingleEntityRenderObject<ItemDisplay> {

  public static final float Z_SCALE = 0.150f * .5f;
  public static final float Z_OFF = 0.033f * .5f;
  public static final float Y_OFF_MODIFIER = 0.5f;
  public static final float ROTATION = (float) Math.toRadians(180);

  public ItemStack item;

  public ItemRenderObject(RenderSystem system) {
    super(system);
  }

  @Override
  protected void configure(ItemDisplay entity, Transformation trans) {
    Vector3f scale = trans.getScale();
    Vector3f offset = trans.getTranslation();

    // FIXME: If the screen is rotate along any axis other than Y, the item
    //  entity will be rotated weirdly because of this, but if this is not
    //  done, the item will be mirrored.
    Quaternionf lrot = trans.getLeftRotation();
    lrot.rotateY(ROTATION);

    Vector2f size;
    if (parent != null) {
      size = new Vector2f(0);
      parent.getContentSize(size);
    } else {
      size = this.size;
    }

    scale.x = (size.x / ITEM_SPRITE_SIZE);
    scale.y = (size.y / ITEM_SPRITE_SIZE);
    scale.z = Z_SCALE;

    // This has to be done here again because the parent class use
    // this object's size value, but item instances use the parent size.
    offset.x = size.x * 0.5f;
    offset.y = 0.0f;
    offset.y -= size.y * Y_OFF_MODIFIER;
//    offset.z += Z_OFF;

    entity.setItemStack(item);
  }

  @Override
  protected ItemDisplay spawnEntity(World w, Location l) {
    return w.spawn(l, ItemDisplay.class, id -> {
      id.setItemDisplayTransform(ItemDisplayTransform.GUI);
    });
  }
}

