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
  ;

  /**
   * The flag's bit mask
   */
  public final int mask;

  NodeFlag() {
    this.mask = 1 << ordinal();
  }
}
