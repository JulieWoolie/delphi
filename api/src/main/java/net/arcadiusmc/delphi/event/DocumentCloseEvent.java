package net.arcadiusmc.delphi.event;

import java.util.Objects;
import net.arcadiusmc.delphi.DocumentView;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a {@link DocumentView} is closed.
 */
public class DocumentCloseEvent extends PlayerEvent {

  private static final HandlerList handlerList = new HandlerList();

  private final DocumentView closedView;

  public DocumentCloseEvent(@NotNull Player who, DocumentView closedView) {
    super(who);
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
