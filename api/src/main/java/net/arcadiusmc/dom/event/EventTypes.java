package net.arcadiusmc.dom.event;

import net.arcadiusmc.dom.Document;

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
   * Called when the mouse scrolls up or down.
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
  String MOUSE_SCROLL = "mouse-scroll";

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
   * Called when an {@link net.arcadiusmc.dom.InputElement}'s value is changed,
   * either by a player or by invoking the
   * {@link net.arcadiusmc.dom.InputElement#setValue(String)} method.
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
}
