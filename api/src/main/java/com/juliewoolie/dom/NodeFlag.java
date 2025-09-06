package com.juliewoolie.dom;

/**
 * Flags each {@link Node} can have
 */
public enum NodeFlag {
  /**
   * The element is currently 'hovered'
   * <p>
   * Note that this does not mean {@link Document#getHoveredElement()} will equal the node this
   * was set on. This flag is propagated through the DOM tree when the player hovers over an
   * element, so all the parent elements of a hovered element will also have this flag.
   */
  HOVERED,

  /**
   * The element is currently 'clicked'
   */
  CLICKED,

  /**
   * The element has been added to the DOM.
   * <p>
   * If this flag is <i>not</i> set, it means the node, or it's ancestors, have not been
   * appended to a {@link Document}.
   */
  ADDED,

  /**
   * The element is the root element of its {@link Document}
   */
  ROOT,

  /**
   * This node is part of a tooltip tree (A tree of nodes that acts as a
   * tooltip for another element)
   * <p>
   * Note that if this flag is set, it does not mean the element itself is the tooltip of it's
   * parent, just that it's part of the elements that are an element's tooltip.
   *
   * @see Element#setTitleNode(Element)
   * @see Element#getTitleNode()
   */
  TOOLTIP,
  ;

  /**
   * The flag's bit mask
   */
  public final int mask;

  NodeFlag() {
    this.mask = 1 << ordinal();
  }
}
