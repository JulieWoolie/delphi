package net.arcadiusmc.dom;

public interface TagNames {

  /**
   * Body tag
   * @see BodyElement
   */
  String BODY = "body";

  /**
   * Button tag
   * @see ButtonElement
   */
  String BUTTON = "button";

  /**
   * Item tag
   * @see ItemElement
   */
  String ITEM = "item";

  /**
   * Item tooltip container tag.
   * <p>
   * Tag is created when a {@link ItemElement} generates a tooltip.
   */
  String ITEM_TOOLTIP = "item-tooltip";

  /**
   * Item tooltip name.
   * <p>
   * Used by {@link ItemElement} to display an item's name when creating
   * an item tooltip.
   */
  String ITEM_TOOLTIP_NAME = ITEM_TOOLTIP + "-name";

  /**
   * Item tooltip line
   * <p>
   * Used by {@link ItemElement} to display each individual line of an item's
   * tooltip.
   */
  String ITEM_TOOLTIP_LINE = ITEM_TOOLTIP + "-line";

}
