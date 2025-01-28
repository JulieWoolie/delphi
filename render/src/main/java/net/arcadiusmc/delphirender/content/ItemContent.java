package net.arcadiusmc.delphirender.content;

import static net.arcadiusmc.delphidom.Consts.GLOBAL_SCALAR;
import static net.arcadiusmc.delphidom.Consts.ITEM_SPRITE_SIZE;

import net.arcadiusmc.delphirender.FullStyle;
import net.arcadiusmc.delphirender.Layer;
import net.arcadiusmc.delphirender.tree.RenderElement;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector2f;

public class ItemContent implements ElementContent {

  public static final float Z_SCALE = 0.150f * GLOBAL_SCALAR;
  public static final float Z_OFF = 0.033f * GLOBAL_SCALAR;
  public static final float Y_OFF_MODIFIER = 0.5f;
  public static final float ROTATION = (float) Math.toRadians(180);

  private final ItemStack item;

  public ItemContent(ItemStack item) {
    this.item = item;
  }

  public static boolean isEmpty(ItemStack item) {
    return item == null || item.getAmount() < 1 || item.getType().isAir();
  }

  @Override
  public Display createEntity(World world, Location location) {
    ItemDisplay display = world.spawn(location, ItemDisplay.class);
    display.setItemDisplayTransform(ItemDisplayTransform.GUI);
    return display;
  }

  @Override
  public void applyContentTo(Display entity, FullStyle set) {
    ItemDisplay display = (ItemDisplay) entity;
    display.setItemStack(item);
  }

  @Override
  public Class<? extends Display> getEntityClass() {
    return ItemDisplay.class;
  }

  @Override
  public void measureContent(Vector2f out, FullStyle set) {
    out.set(ITEM_SPRITE_SIZE);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public void configureInitial(Layer layer, RenderElement element) {
    layer.scale.z = Z_SCALE;

//    float transY = layer.size.y * Y_OFF_MODIFIER * element.getStyle().scale.y * GLOBAL_SCALAR;
//    layer.translate.y += transY;
    layer.translate.z += Z_OFF;

    //
    // FIXME: Items, un-rotated, are mirrored and need the 180deg rotation applied to
    //  them, but if they're rotated then any rotation of the screen's pitch causes
    //  the item to rotate in the opposite direction as the rest of the screen.
    //
    //layer.leftRotation.rotateY(ROTATION);
  }
}
