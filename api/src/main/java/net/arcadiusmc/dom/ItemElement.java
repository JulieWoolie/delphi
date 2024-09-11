package net.arcadiusmc.dom;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an ItemStack element
 * <pre><code>
 * <item src="./item.json" />
 * </code></pre>
 */
public interface ItemElement extends Element {

  /**
   * Item nodes cannot have children, this will always return {@code false}
   * @return {@code false}
   */
  @Override
  boolean canHaveChildren();

  /**
   * Get whether the element shows the automatically generated tooltip when hovered over or not.
   * <p>
   * If {@link #getTooltip()} does NOT return the automatically generated item tooltip, then the
   * result of this method is ignored.
   * <p>
   * Shorthand for parsing the value of the {@link Attributes#ITEM_TOOLTIP_HIDE} attribute. If the
   * attribute is not set, {@code false} is returned. If the attribute's value cannot be parsed
   * into a boolean, {@code false} is returned. Otherwise the parsed value of the attribute is
   * returned.
   *
   * @return {@code true}, if the item tooltip is hidden, {@code false} otherwise.
   *
   * @see Attributes#ITEM_TOOLTIP_HIDE
   */
  boolean getTooltipHidden();

  /**
   * Set whether the element shows the automatically generated tooltip when hovered over.
   * <p>
   * Shorthand for setting the {@link Attributes#ITEM_TOOLTIP_HIDE} attribute.
   *
   * @param tooltipShown {@code true}, if the item tooltip is hidden, {@code false} otherwise.
   *
   * @see Attributes#ITEM_TOOLTIP_HIDE
   */
  void setTooltipHidden(boolean tooltipShown);

  /**
   * Get whether the shown item's tooltip shows the advanced {@code F3+H} information.
   * <p>
   * Shorthand for parsing the value of the {@link Attributes#ADVANCED_ITEM_TOOLTIPS}
   * attribute. If the attribute is not set, then the value of
   * {@link Options#ADVANCED_ITEM_TOOLTIPS} is used. If the option has no set value,
   * then {@code false} is returned. If the attribute/option's value cannot be parsed
   * into a boolean, {@code false} is returned, otherwise the parsed value is returned.
   *
   * @return {@code true}, if advanced tooltips are shown, {@code false} otherwise.
   *
   * @see Attributes#ADVANCED_ITEM_TOOLTIPS
   * @see Options#ADVANCED_ITEM_TOOLTIPS
   */
  boolean getAdvancedTooltip();

  /**
   * Set whether the shown item's tooltip shows the advanced {@code F3+H} information.
   * <p>
   * Shorthand for setting the {@link Attributes#ADVANCED_ITEM_TOOLTIPS} attribute.
   *
   * @param advancedTooltip {@code true}, to show advanced tooltips, {@code false} otherwise.
   *
   * @see Attributes#ADVANCED_ITEM_TOOLTIPS
   * @see Options#ADVANCED_ITEM_TOOLTIPS
   */
  void setAdvancedTooltip(boolean advancedTooltip);

  /**
   * Gets the element's tooltip.
   * <p>
   * May return the item stack's tooltip, unless the element's tooltip has been overridden with
   * {@link #setTitleNode(Node)} or if the {@link Attributes#ITEM_TOOLTIP_HIDE} has been set to
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
