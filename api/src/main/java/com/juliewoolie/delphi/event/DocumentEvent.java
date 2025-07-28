package com.juliewoolie.delphi.event;

import java.util.Objects;
import com.juliewoolie.delphi.DocumentView;
import com.juliewoolie.dom.Document;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired for each event fired inside an open document view.
 * <p>
 * This event is fired after the event has bubbled up through the document
 * tree, and after the {@link Document#getGlobalTarget()} has been called.
 */
public class DocumentEvent extends Event {

  private static final HandlerList handlerList = new HandlerList();

  private final com.juliewoolie.dom.event.Event domEvent;

  public DocumentEvent(@NotNull com.juliewoolie.dom.event.Event domEvent) {
    Objects.requireNonNull(domEvent, "Null dom event");
    this.domEvent = domEvent;
  }

  /**
   * Get the DOM event object
   * @return DOM event object
   */
  public @NotNull com.juliewoolie.dom.event.Event getDomEvent() {
    return domEvent;
  }

  /**
   * Get the document the event is fired on
   * @return Document the event was fired on
   */
  public @NotNull Document getDocument() {
    return domEvent.getDocument();
  }

  /**
   * Get the document's event view
   * @return Document event view
   */
  public @NotNull DocumentView getView() {
    return domEvent.getDocument().getView();
  }

  public static HandlerList getHandlerList() {
    return handlerList;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlerList;
  }
}
