package com.juliewoolie.delphi.event;

import java.util.Objects;
import com.juliewoolie.delphi.DocumentView;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a {@link DocumentView} is closed.
 */
public class DocumentCloseEvent extends Event {

  private static final HandlerList handlerList = new HandlerList();

  private final DocumentView closedView;

  public DocumentCloseEvent(DocumentView closedView) {
    super();
    this.closedView = Objects.requireNonNull(closedView, "Null view");
  }

  /**
   * Get the view being closed.
   * @return Closed view
   */
  public DocumentView getClosedView() {
    return closedView;
  }

  public static HandlerList getHandlerList() {
    return handlerList;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlerList;
  }
}
