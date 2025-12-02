package com.juliewoolie.dom.event;

import com.juliewoolie.delphi.DocumentView;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.InputElement;

/**
 * Event type constants
 */
public interface EventTypes {

  /**
   * Called when a cursor enter an element's bounds.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code true}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link MouseEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String MOUSE_ENTER = "mouse-enter";

  /**
   * Called when a cursor leaves an element's bounds.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code true}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link MouseEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String MOUSE_LEAVE = "mouse-exit";

  /**
   * Called when the mouse is moved inside of an element.
   * <p>
   * This event will only be called if the mouse movement happens inside of an element,
   * if the mouse moves in or out of an element, then {@link #MOUSE_LEAVE} or {@link #MOUSE_ENTER}
   * will be called instead.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link MouseEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String MOUSE_MOVE = "mouse-move";

  /**
   * Called when the mouse is clicked
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link MouseEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String CLICK = "click";

  /**
   * Called when an element stops being active after being clicked.
   * <p>
   * Length of time between clicking and the active state expiring is determined by
   * {@link Document#ACTIVE_TICKS}
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link MouseEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String CLICK_EXPIRE = "click-expire";

  /**
   * Called when a node is appended onto another node.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link MutationEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String APPEND_CHILD = "append-child";

  /**
   * Called when a node is removed from another node
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link MutationEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String REMOVE_CHILD = "remove-child";

  /**
   * Called when an attribute is modified.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link AttributeMutateEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String MODIFY_ATTR = "modify-attribute";

  /**
   * Called when an option is modified.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link AttributeMutateEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   *
   * @see Document#setOption(String, String)
   */
  String MODIFY_OPTION = "modify-option";

  /**
   * Called when the DOM has been fully loaded and is about to be spawned.
   * <p>
   * This event is only dispatched on {@link Document} instances.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link Event}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>None, only triggered on the {@link Document} itself</td>
   *   </tr>
   * </table>
   */
  String DOM_LOADED = "load";

  /**
   * Called when the DOM has been spawned after being loaded
   * <p>
   * This event is only dispatched on {@link Document} instances.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link Event}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>None, only triggered on the {@link Document} itself</td>
   *   </tr>
   * </table>
   */
  String DOM_SPAWNED = "spawned";

  /**
   * Called when a document is about to be closed.
   * <p>
   * This event is only dispatched on {@link Document} instances.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link Event}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>None, only triggered on the {@link Document} itself</td>
   *   </tr>
   * </table>
   */
  String DOM_CLOSING = "close";

  /**
   * Called when an {@link InputElement}'s value is changed,
   * either by a player or by invoking the
   * {@link InputElement#setValue(String)} method.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code true}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link Event}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>None, only triggered on the {@link Document} itself</td>
   *   </tr>
   * </table>
   */
  String INPUT = "input";

  /**
   * Called when a text node's content is changed. This event is fired on the text node's
   * parent element.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code true}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link Event}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String CONTENT_CHANGED = "content-changed";

  /**
   * Called when the current document view is moved, or it's location changes.
   * Only called on {@link Document#getDocumentElement()} instances.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link Event}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>{@link Document}</td>
   *   </tr>
   * </table>
   */
  String VIEW_MOVED = "view-moved";

  /**
   * Called when an element's tooltip is changed.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code true}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link TooltipEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>All</td>
   *   </tr>
   * </table>
   */
  String TOOLTIP_CHANGED = "tooltip";

  /**
   * Called when a player is added to a {@link com.juliewoolie.delphi.DocumentView}s player set.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link PlayerSetEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>{@link com.juliewoolie.dom.TagNames#ROOT}</td>
   *   </tr>
   * </table>
   *
   * @see DocumentView#getPlayers()
   */
  String PLAYER_ADDED = "player-added";

  /**
   * Called when a player is removed from a {@link com.juliewoolie.delphi.DocumentView}s
   * player set.
   *
   * <table>
   *   <caption>Details</caption>
   *   <tr>
   *     <td>Bubbles</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Cancellable</td>
   *     <td>{@code false}</td>
   *   </tr>
   *   <tr>
   *     <td>Type</td>
   *     <td>{@link PlayerSetEvent}</td>
   *   </tr>
   *   <tr>
   *     <td>Tags</td>
   *     <td>{@link com.juliewoolie.dom.TagNames#ROOT}</td>
   *   </tr>
   * </table>
   *
   * @see DocumentView#getPlayers()
   */
  String PLAYER_REMOVED = "player-removed";

}
