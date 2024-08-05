package net.arcadiusmc.dom;

import java.util.List;
import java.util.Set;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.dom.event.EventTarget;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.style.Stylesheet;
import net.arcadiusmc.dom.style.StylesheetBuilder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Document extends EventTarget, ParentNode {

  /**
   * Amount of ticks an element remains 'active' for after being clicked
   */
  int ACTIVE_TICKS = 4;

  /**
   * Gets the document view.
   * @return Document view, or {@code null}, if the document hasn't been shown to a player yet.
   */
  DocumentView getView();

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
   * Will trigger an {@link EventTypes#MODIFY_OPTION} event after changing the value.
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
   * If the option already has no set value, this method does nothing. Otherwise, it will trigger
   * an {@link EventTypes#MODIFY_OPTION} event after changing the value.
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
   * Creates a component node.
   * <p>
   * Component nodes differ from {@link TextNode}s by the fact they use a fully-styled,
   * {@link Component}. It is not recommended to use this, because it can conflict with
   * document styling.
   * <p>
   * Components are required in certain instances, like in item tooltips, where it's not
   * possible to dissect each line of the tooltip, and instead, a component node is used
   * for the content.
   *
   * @return Created component node
   */
  ComponentNode createComponent();

  /**
   * Create a component node.
   * <p>
   * Component nodes differ from {@link TextNode}s by the fact they use a fully-styled,
   * {@link Component}. It is not recommended to use this, because it can conflict with
   * document styling.
   * <p>
   * Components are required in certain instances, like in item tooltips, where it's not
   * possible to dissect each line of the tooltip, and instead, a component node is used
   * for the content.
   *
   * @param component Node content
   *
   * @return Created component node
   */
  ComponentNode createComponent(@Nullable Component component);

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
   * Adds a stylesheet to this document.
   *
   * @param stylesheet Style sheet
   *
   * @throws NullPointerException if {@code stylesheet} is {@code null}
   */
  void addStylesheet(@NotNull Stylesheet stylesheet);

  /**
   * Gets an unmodifiable list of stylesheets this document has.
   * @return Unmodifiable stylesheet list.
   */
  @NotNull List<Stylesheet> getStylesheets();

  /**
   * Creates a new style sheet builder.
   * <p>
   * When {@link StylesheetBuilder#build()} is called on the returned sheet, the
   * stylesheet is automatically added to this document's stylesheet list.
   *
   * @return Created stylesheet builder
   */
  @NotNull StylesheetBuilder createStylesheet();
}
