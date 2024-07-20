package net.arcadiusmc.dom;

import java.util.Set;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.AttributeMutateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Document extends EventTarget, ParentNode {

  /**
   * Amount of ticks an element remains 'active' for after being clicked
   */
  int ACTIVE_TICKS = 4;

  /**
   * Gets the value of the option
   * @param optionKey Option key
   * @return Option value, or {@code null}, if the option has no set value.
   */
  @Nullable String getOption(String optionKey);

  /**
   * Set an option's value
   * <p>
   * If the option value is {@code null} or empty, then this acts the same
   * as calling {@link #removeOption(String)}
   * <p>
   * Will cause a {@link AttributeMutateEvent} call after the value has been changed.
   *
   * @param optionKey Option key
   * @param value Option value
   *
   * @throws NullPointerException if {@code optionKey} is null
   */
  void setOption(@NotNull String optionKey, @Nullable String value);

  /**
   * Removes an option's value.
   * <p>
   * If the option already has no set value, this method does nothing. Otherwise it will cause
   * a {@link AttributeMutateEvent} call after changing the value.
   *
   * @param optionKey Option key
   *
   * @throws NullPointerException if {@code optionKey} is null
   */
  void removeOption(@NotNull String optionKey);

  /**
   * Gets a set of all option keys.
   * @return Unmodifiable option key set
   */
  Set<String> getOptionKeys();

  /**
   * Creates an element with a specified {@code tagName}
   * @param tagName Element's tag name.
   * @return Created element
   * @throws NullPointerException if {@code tagName} is {@code null}
   */
  Element createElement(@NotNull String tagName);

  /**
   * Creates an empty text node
   * @return Empty text node
   */
  TextNode createText();

  /**
   * Creates a text node with the specified {@code content}
   * @param content Node content
   * @return Created text node
   */
  TextNode createText(@Nullable String content);

  /**
   * Gets the last clicked element that's still active.
   * <p>
   * When an element is clicked, it will remain active for {@link #ACTIVE_TICKS} ticks.
   *
   * @return Active element
   */
  @Nullable Element getActiveElement();

  /**
   * Gets the element the viewer's cursor is currently hovered over
   * @return Hovered element
   */
  @Nullable Element getHoveredElement();

  /**
   * Gets the body element of the document.
   * <p>
   * The body element contains all the content of a document
   *
   * @return Document body
   */
  Element getBody();

  /**
   * Gets the first descendant element of with the specified {@code elementId}
   * @param elementId ID to search for
   * @return Found element, or {@code null}, if no element with the specified id exists
   */
  @Nullable Element getElementById(String elementId);

  /**
   * Gets the global event target.
   * <p>
   * The target returned by this method will be called after an event has finished propagation,
   * unless the event has been cancelled.
   * <p>
   * This target will be invoked after ALL events.
   *
   * @return Global event target
   */
  EventTarget getGlobalTarget();

  /**
   * Adopts a node if it belongs to another document instance.
   * @param node Node to adopt.
   *
   * @throws NullPointerException if {@code node} is {@code null}
   */
  void adopt(@NotNull Node node);

  /**
   * Forces the whole document tree to recalculate alignment and layout
   */
  void realign();
}
