package net.arcadiusmc.dom;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.style.StyleProperties;
import net.arcadiusmc.dom.style.StylePropertiesReadonly;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Element extends Node, EventTarget, ParentNode {

  /**
   * Get the modifiable inline style properties.
   * <p>
   * Any changes made to the returned properties will be reflected in the {@link Attributes#STYLE}
   * attribute, and any changes to the {@link Attributes#STYLE} attribute will be reflected in
   * the returned properties.
   *
   * @return Inline style
   */
  StyleProperties getInlineStyle();

  /**
   * Get the element's current style.
   * <p>
   * The returned result's properties will be the computed style values of all applicable
   * stylesheet rules and the inline style for the element.
   *
   * @return Unmodifiable style properties
   */
  StylePropertiesReadonly getCurrentStyle();

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
   * Otherwise, an {@link EventTypes#MODIFY_ATTR} event will be triggered after the value is set.
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
  Set<String> getAttributeNames();

  /**
   * Gets a set of attribute entries that exist on this element.
   * @return Unmodifiable entry set
   */
  Set<Entry<String, String>> getAttributeEntries();

  /**
   * Shorthand for getting the id attribute's value
   * @return Element ID
   *
   * @see #getAttribute(String)
   * @see Attributes#ID
   */
  default String getId() {
    return getAttribute(Attributes.ID);
  }

  /**
   * Shorthand for setting the id attribute
   *
   * @param elementId Element ID
   *
   * @see #setAttribute(String, String)
   * @see Attributes#ID
   */
  default void setId(@Nullable String elementId) {
    setAttribute(Attributes.ID, elementId);
  }

  /**
   * Gets the element's {@link Attributes#CLASS} attribute value.
   * @return Class name
   */
  default @Nullable String getClassName() {
    return getAttribute(Attributes.CLASS);
  }

  /**
   * Sets the element's {@link Attributes#CLASS} attribute value
   * @param className Class name
   */
  default void setClassName(String className) {
    setAttribute(Attributes.CLASS, className);
  }

  /**
   * Gets the element's class array list.
   * @return An array list containing all the classes of this element.
   */
  default @NotNull List<String> getClassList() {
    String str = getClassName();

    if (Strings.isNullOrEmpty(str)) {
      return new ArrayList<>();
    }

    String[] split = str.split("\\s+");
    List<String> stringList = new ArrayList<>();

    Collections.addAll(stringList, split);

    return stringList;
  }

  /**
   * Sets the element's class list.
   * @param classList Class list, or {@code null}, to remove classes.
   */
  default void setClassList(@Nullable Collection<String> classList) {
    if (classList == null) {
      setAttribute(Attributes.CLASS, null);
      return;
    }

    StringJoiner joiner = new StringJoiner(" ");
    for (String s : classList) {
      joiner.add(s);
    }

    setAttribute(Attributes.CLASS, joiner.toString());
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
   * Adds a child node to the end of this element's child nodes.
   * <p>
   * If the specified {@code node} belongs to a different document, it will be adopted to the
   * current document. if the {@code node} already has a parent, it will be orphaned before
   * addition.
   * <p>
   * Will trigger an {@link EventTypes#APPEND_CHILD} event. Can additionally trigger a
   * {@link EventTypes#REMOVE_CHILD} event on its previous parent, if it has one.
   *
   *
   * @param node Node
   *
   * @throws NullPointerException if {@code node} is {@code null}
   *
   * @apiNote If {@link #canHaveChildren()} returns {@code false}, this does nothing.
   */
  void appendChild(@NotNull Node node);

  /**
   * Adds a child node to start of this element's child nodes.
   * <p>
   * If the specified {@code node} belongs to a different document, it will be adopted to the
   * current document. if the {@code node} already has a parent, it will be orphaned before
   * addition.
   * <p>
   * Will trigger an {@link EventTypes#APPEND_CHILD} event. Can additionally trigger a
   * {@link EventTypes#REMOVE_CHILD} event on its previous parent, if it has one.
   *
   * @param node Prepended node
   *
   * @throws NullPointerException if {@code node} is {@code null}
   *
   * @apiNote If {@link #canHaveChildren()} returns {@code false}, this does nothing.
   */
  void prependChild(@NotNull Node node);

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
   * Will trigger an {@link EventTypes#APPEND_CHILD} event. Can additionally trigger a
   * {@link EventTypes#REMOVE_CHILD} event on its previous parent, if it has one.
   *
   * @param node Node to insert
   * @param before Node to insert before
   *
   * @throws NullPointerException If either {@code node} or {@code before} are {@code null}
   *
   * @apiNote If {@link #canHaveChildren()} returns {@code false}, this does nothing.
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
   * Will trigger an {@link EventTypes#APPEND_CHILD} event. Can additionally trigger a
   * {@link EventTypes#REMOVE_CHILD} event on its previous parent, if it has one.
   *
   * @param node Node to insert
   * @param after Node to insert after
   *
   * @throws NullPointerException If either {@code node} or {@code after} are {@code null}
   *
   * @apiNote If {@link #canHaveChildren()} returns {@code false}, this does nothing.
   */
  void insertAfter(@NotNull Node node, @NotNull Node after);

  /**
   * Removes a specified child from this element.
   * <p>
   * If the specified {@code node} is not a child of this element, then {@code false} will be
   * returned.
   * <p>
   * Will trigger a {@link EventTypes#REMOVE_CHILD} event. The removed node will be orphaned
   * after removal.
   *
   * @param node Node to remove
   * @return {@code true}, if the node was removed, {@code false} if the node was not a child of
   *         this element.
   *
   * @see #removeChild(int)
   *
   * @throws NullPointerException if {@code node} is {@code null}
   */
  boolean removeChild(@NotNull Node node);

  /**
   * Removes a child element by its index.
   * <p>
   * Will trigger a {@link EventTypes#REMOVE_CHILD} event. The removed node will be orphaned
   * after removal.
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
   * Tests if this element has children.
   * @return {@code true}, if the element has children, {@code false} otherwise
   */
  boolean hasChildren();

  /**
   * Tests if the specified {@code node} is a direct child of this element
   *
   * @param node Node to test
   *
   * @return {@code true}, if {@code node} is a direct child of this element,
   *         {@code false} otherwise.
   */
  @Contract("null -> false")
  boolean hasChild(@Nullable Node node);

  /**
   * Gets the index of a node that is the direct child of this element.
   *
   * @param node Direct child node
   *
   * @return The node's index, or {@code -1}, if the node is not a direct child of this element
   */
  int indexOf(@Nullable Node node);

  /**
   * Gets the amount of children the element has
   * @return Child count
   */
  int getChildCount();

  /**
   * Tests if this element is capable of having child nodes.
   * <p>
   * This returns {@code true} in most cases. It returns false if the element's tag is
   * {@code <item>}.
   *
   * @return {@code true}, if this element can support child noes, {@code false} otherwise.
   */
  boolean canHaveChildren();

  /**
   * Gets the nth child of this element.
   *
   * @param index Child index from 0 (inclusive) to {@link #getChildCount()} (exclusive)
   * @return Child node.
   *
   * @throws IndexOutOfBoundsException If {@code index} is less than 0 or greater/equal to
   *                                   {@link #getChildCount()}.
   */
  Node getChild(int index) throws IndexOutOfBoundsException;

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
   * @see #appendChild(Node)
   */
  default @NotNull Element appendElement(@NotNull String tagName) {
    Element el = getOwningDocument().createElement(tagName);
    appendChild(el);
    return el;
  }

  /**
   * Creates a text node and appends it to this element
   *
   * @param text Text content
   * @return Created node
   *
   * @see #appendChild(Node)
   */
  default @NotNull TextNode appendText(@Nullable String text) {
    TextNode node = getOwningDocument().createText(text);
    appendChild(node);
    return node;
  }
}
