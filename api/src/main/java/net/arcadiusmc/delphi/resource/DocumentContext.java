package net.arcadiusmc.delphi.resource;

import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.PlayerSet;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.style.Stylesheet;
import net.arcadiusmc.dom.style.StylesheetBuilder;
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
   * Create a new document.
   * <p>
   * The returned document's view will be set to {@link #getView()}, but the result of
   * {@link DocumentView#getDocument()} will remain null until after the module has returned
   * the created document.
   *
   * @return Created document
   */
  @NotNull Document newDocument();

  /**
   * Create a new style sheet builder that can be added to any document.
   * @return New style sheet builder
   */
  @NotNull StylesheetBuilder newStylesheet();

  /**
   * Parse a stylesheet from a string.
   * <p>
   * This function does <i>not</i> throw any parsing exceptions, rather it makes it best
   * attempt at parsing the input and logs any errors in the console.
   *
   * @param string Stylesheet string
   *
   * @return Parsed stylesheet
   *
   * @throws NullPointerException If {@code string} is {@code null}
   */
  @NotNull Stylesheet parseStylesheet(@NotNull String string);

  /**
   * Parse a document from a string
   * <p>
   * Attempts to parse a document from a string input. If this method fails to parse the specified
   * {@code string}, a {@link DelphiException} is thrown. Uses similar error codes to
   * {@link ViewResources#loadDocument(String)}
   *
   * @param string Document source
   * @return Parsed document
   *
   * @throws DelphiException If the specified {@code string} could not be parsed
   * @throws NullPointerException If {@code string} is {@code null}
   */
  @NotNull Document parseDocument(@NotNull String string) throws DelphiException;

  /**
   * Get the player the document is being opened for.
   * @return Player
   */
  @NotNull PlayerSet getPlayers();

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
