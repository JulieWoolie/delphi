package net.arcadiusmc.delphi.resource;

import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.dom.Document;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Document loading context. Created by {@link ViewResources} and passed to {@link ApiModule}
 * instances.
 *
 * @see ApiModule#loadDocument(ResourcePath, DocumentContext)
 * @see ViewResources#loadDocument(String)
 */
public interface DocumentContext {

  /**
   * Creates a new document.
   * <p>
   * The returned document's view will be set to {@link #getView()}, but the result of
   * {@link DocumentView#getDocument()} will remain null until after the module has returned
   * the created document.
   *
   * @return Created document
   */
  @NotNull Document newDocument();

  /**
   * Get the player the document is being opened for.
   * @return Player
   */
  @NotNull Player getPlayer();

  /**
   * Get the document view.
   * <p>
   * The returned view will always have a null {@link DocumentView#getDocument()}, as the
   * view's document is set after the module's load method has returned a value.
   *
   * @return Document view
   */
  @NotNull DocumentView getView();
}
