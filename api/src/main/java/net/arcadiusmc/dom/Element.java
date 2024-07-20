package net.arcadiusmc.dom;

import java.util.List;
import java.util.Set;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MutationEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Element extends Node, EventTarget, ParentNode {

  /**
   * Gets the value of an attribute.
   * @param key Attribute key
   * @return Attribute value, or {@code null}, if not set, or if {@code key} is {@code null}.
   */
  @Nullable String getAttribute(String key);

  /**
   * Sets an attribute's value.
   * <p>
   * If the specified {@code value} is already set as the value for this attribute, nothing
   * will change.
   * <br>
   * Otherwise, a {@link AttributeMutateEvent} will be called after the value is set.
   *
   * @param key Attribute key
   * @param value Attribute value, or {@code null} to remove.
   *
   * @throws NullPointerException if {@code key} is {@code null} or empty
   */
  void setAttribute(@NotNull String key, @Nullable String value);

  /**
   * Get a set of attribute keys that exist on this element.
   * @return Unmodifiable key set
   */
  Set<String> getAttributeKeys();

  /**
   * Shorthand for getting the id attribute's value
   * @return Element ID
   *
   * @see #getAttribute(String)
   * @see Attr#ID
   */
  default String getId() {
    return getAttribute(Attr.ID);
  }

  /**
   * Shorthand for setting the id attribute
   *
   * @param elementId Element ID
   *
   * @see #setAttribute(String, String)
   * @see Attr#ID
   */
  default void setId(@Nullable String elementId) {
    setAttribute(Attr.ID, elementId);
  }

  /**
   * Gets the element's tag name.
   * @return Tag name
   */
  String getTagName();

  /**
   * Gets the element's tooltip.
   * @return Tooltip node
   * @see ItemElement#getTooltip()
   */
  @Nullable Node getTooltip();

  /**
   * Gets the element's title node.
   * <p>
   * The title node is used as the element's hover tooltip.
   *
   * @return Title node
   */
  @Nullable Node getTitleNode();

  /**
   * Sets the element's title node.
   * <p>
   * The title node is used as the element's hover tooltip.
   *
   * @param title Title node
   */
  void setTitleNode(@Nullable Node title);

  /**
   * Appends a child node to this element.
   * <p>
   * If the specified {@code node} belongs to a different document, it will be adopted to the
   * current document. if the {@code node} already has a parent, it will be orphaned before
   * addition.
   * <p>
   * Will result in a {@link MutationEvent} call, with type {@link EventTypes#APPEND_CHILD}.
   *
   * @param node Node
   *
   * @throws NullPointerException if {@code node} is {@code null}
   */
  void appendChild(@NotNull Node node);

  /**
   * Inserts an element before the specified node
   * <p>
   * If the specified {@code node} belongs to a different document, it will be adopted to the
   * current document. if the {@code node} already has a parent, it will be orphaned before
   * addition.
   * <p>
   * If the specified {@code before} node does not belong to the children of this element,
   * then nothing happens.
   * <p>
   * Will result in a {@link MutationEvent} call, with type {@link EventTypes#APPEND_CHILD}.
   *
   * @param node Node to insert
   * @param before Node to insert before
   */
  void insertBefore(@NotNull Node node, @NotNull Node before);

  /**
   * Inserts an element after the specified node
   * <p>
   * If the specified {@code node} belongs to a different document, it will be adopted to the
   * current document. if the {@code node} already has a parent, it will be orphaned before
   * addition.
   * <p>
   * If the specified {@code after} node does not belong to the children of this element,
   * then nothing happens.
   * <p>
   * Will result in a {@link MutationEvent} call, with type {@link EventTypes#APPEND_CHILD}.
   *
   * @param node Node to insert
   * @param after Node to insert after
   */
  void insertAfter(@NotNull Node node, @NotNull Node after);

  /**
   * Removes a specified child from this element.
   * <p>
   * If the specified {@code node} is not a child of this element, then {@code false} will be
   * returned.
   * <p>
   * Will result in a {@link MutationEvent} call, with type {@link EventTypes#REMOVE_CHILD}.
   * The removed node will be orphaned after removal.
   *
   * @param node Node to remove
   * @return {@code true}, if the node was removed, {@code false} if the node was not a child of
   *         this element.
   *
   * @see #removeChild(int)
   */
  boolean removeChild(Node node);

  /**
   * Removes a child element by its index.
   * <p>
   * Will result in a {@link MutationEvent} call, with type {@link EventTypes#REMOVE_CHILD}.
   * The removed node will be orphaned after removal.
   *
   * @param childIndex Index of the child element to remove
   * @throws IndexOutOfBoundsException If the specified {@code childIndex} is invalid
   */
  void removeChild(int childIndex) throws IndexOutOfBoundsException;

  /**
   * Gets an immutable list of child nodes
   * @return Child node list
   */
  List<Node> getChildren();

  /**
   * Gets the first child of this element.
   * @return First child, or {@code null}, if this element has no children
   */
  @Nullable Node firstChild();

  /**
   * Gets the last child of this element.
   * @return Last child, or {@code null}, if this element has no children
   */
  @Nullable Node lastChild();

  /**
   * Appends an element and returns it.
   *
   * @param tagName Element tag name
   * @return Created element
   *
   * @throws NullPointerException if {@code tagName} is {@code null}
   */
  default @NotNull Element appendElement(@NotNull String tagName) {
    Element el = getOwningDocument().createElement(tagName);
    appendChild(el);
    return el;
  }

  /**
   * Creates a text node and appends it to this element
   * @param text Text content
   * @return Created node
   */
  default @NotNull TextNode appendText(@Nullable String text) {
    TextNode node = getOwningDocument().createText(text);
    appendChild(node);
    return node;
  }
}
