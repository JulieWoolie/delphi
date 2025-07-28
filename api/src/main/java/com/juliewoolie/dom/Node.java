package com.juliewoolie.dom;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a node in the DOM structure.
 * <p>
 * A node can be an element, text, component or an item element. Unlike, regular DOM
 * implementations, attributes are <i>not</i> nodes.
 *
 * @see TextNode
 * @see Document
 * @see Element
 * @see HeadElement
 * @see OptionElement
 * @see StyleElement
 * @see JavaObjectElement
 * @see BodyElement
 * @see ItemElement
 * @see ButtonElement
 * @see ComponentElement
 * @see InputElement
 */
public interface Node {

  /**
   * Gets the document that owns this node.
   * @return Owning document.
   */
  @NotNull Document getOwningDocument();

  /**
   * Gets the parent element of this node.
   * @return Parent, or {@code null}
   */
  @Nullable Element getParent();

  /**
   * Gets the index of this element among its siblings.
   * @return Sibling index, or {@code -1}, if no parent is set
   */
  int getSiblingIndex();

  /**
   * Gets the node immediately after this one in the parent's child list.
   *
   * @return Next sibling node, or {@code null}, if this node has no parent or if there is no
   *         sibling after this node.
   */
  @Nullable Node nextSibling();

  /**
   * Gets the node immediately before this one in the parent's child list.
   *
   * @return Previous sibling node, or {@code null}, if this node has no parent or if there is no
   *         node before this one.
   */
  @Nullable Node previousSibling();

  /**
   * Gets the node's depth.
   * <p>
   * Depth is a measure of how far away from the root element a node is.
   *
   * @return Node depth
   */
  int getDepth();

  /**
   * Test if the node has the specified {@code flag} set.
   *
   * @param flag Flag to test
   * @return {@code true}, if the specified flag was set, {@code false} otherwise
   */
  @Contract("null -> false")
  boolean hasFlag(NodeFlag flag);

  /**
   * Get the rendering bounds for a node.
   * <p>
   * <b>Note</b>: If the node has not been appended to a document, or if the
   * {@link #getOwningDocument()}'s view has not been spawned, then this will
   * return {@code null}.
   * <p>
   * The returned reference is not updated after it is returned.
   *
   * @return Node rendering bounds, or {@code null}, if the document's view
   *         has not been spawned.
   */
  @Nullable RenderBounds getRenderingBounds();

  void enterVisitor(Visitor visitor);

  void exitVisitor(Visitor visitor);
}
