package net.arcadiusmc.dom;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemElement extends Element {

  /**
   * Gets the element's tooltip.
   * <p>
   * May return the item stack's tooltip, unless the element's tooltip has been overridden with
   * {@link #setTitleNode(Node)} or if the {@link Attr#ITEM_TOOLTIP_HIDE} has been set to
   * {@code true}
   *
   * @return Element tooltip
   */
  @Override
  Node getTooltip();

  /**
   * Gets the element's item stack
   * @return Item stack, or {@code null}, if not set or if it failed to load.
   */
  @Nullable
  ItemStack getItemStack();

  /**
   * Sets the element's item stack.
   * @param stack Item tack.
   */
  void setItemStack(@Nullable ItemStack stack);
}
