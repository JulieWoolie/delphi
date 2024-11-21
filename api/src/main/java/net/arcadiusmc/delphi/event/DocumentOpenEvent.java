package net.arcadiusmc.delphi.event;

import java.util.Objects;
import net.arcadiusmc.delphi.Delphi;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.resource.ResourcePath;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event fired when a delphi document is opened and shown to a viewer.
 * <p>
 * This event can be triggered by either {@link Delphi#openDocument(ResourcePath, Player)}, or
 * by doing the {@code /delphi open <player> <path>} command.
 * <p>
 * The Event is fired after the view is loaded, but before the entities that render the
 * view are spawned.
 */
public class DocumentOpenEvent extends Event {

  private static final HandlerList handlerList = new HandlerList();

  private final DocumentView openedView;

  public DocumentOpenEvent(DocumentView view) {
    this.openedView = Objects.requireNonNull(view, "Null view");
  }

  /**
   * Get the document view that was opened.
   * @return Opened view
   */
  public DocumentView getOpenedView() {
    return openedView;
  }

  public static HandlerList getHandlerList() {
    return handlerList;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlerList;
  }
}
