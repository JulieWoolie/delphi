package net.arcadiusmc.dom;

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
}
