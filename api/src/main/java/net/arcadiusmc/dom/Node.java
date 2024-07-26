package net.arcadiusmc.dom;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  void enterVisitor(Visitor visitor);

  void exitVisitor(Visitor visitor);
}
