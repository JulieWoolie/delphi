package net.arcadiusmc.dom.event;

import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;

public interface Event {

  /**
   * Gets the event type.
   * @return Event type
   * @see EventTypes
   */
  String getType();

  /**
   * Gets the node this event was called on
   * @return Event target
   */
  Element getTarget();

  /**
   * Gets the event target currently executing this event
   * @return Current event target
   */
  EventTarget getCurrentTarget();

  /**
   * Gets the document this event was fired in.
   * @return Owning document
   */
  Document getDocument();

  boolean isCancelled();

  boolean isBubbling();

  boolean isPropagationStopped();

  boolean isCancellable();

  void stopPropagation();

  void preventDefault();
}
