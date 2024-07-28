package net.arcadiusmc.delphi.resource;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.style.Stylesheet;

public interface ViewResources {

  /**
   * Gets the resource module.
   * @return Resource module
   */
  ResourceModule getModule();

  /**
   * Gets the name of the resource module
   * @return Resource module name
   */
  String getModuleName();

  /**
   * Attempts to load a document from the specified resource path.
   * <p>
   * Any errors thrown during the method's execution will be caught and returned in the
   * result.
   * <p>
   * If {@link #getModule()} is a {@link IoModule} then this method attempts to use the
   * {@link IoModule#loadString(ResourcePath)} to load the document's XML content. If the
   * underlying module is a {@link ApiModule}, then {@link ApiModule#loadDocument(ResourcePath, DocumentFactory)}
   * is called.
   *
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Message format</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code "Invalid Path"}</td>
   *     <td>The {@code uri} could not be parsed into a resource path.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "No Such File"}</td>
   *     <td>Module threw a {@link NoSuchFileException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Access Denied: %reason%"}</td>
   *     <td><Module threw a {@link AccessDeniedException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "IO Error: %message%"}</td>
   *     <td>Module threw an {@link IOException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Internal error creating parser"}</td>
   *     <td>Failed to instantiate SAX parser to parse the XML document.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Failed to parse document"}</td>
   *     <td>Unrecoverable failure trying to parse the XML data</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Unknown"}</td>
   *     <td>An unknown error caused the document parser to return a {@code null} document</td>
   *   </tr>
   * </table>
   *
   * @param uri file path
   *
   * @return Loaded document, or an erroneous result if the document cannot be loaded for
   *         any reason.
   */
  Result<Document, String> loadDocument(String uri);

  /**
   * Attempts to load a stylesheet from the specified resource path.
   * <p>
   * Any errors thrown during the method's execution will be caught and an erroneous
   * result returned.
   * <p>
   * If {@link #getModule()} is a {@link IoModule} then this method attempts to use
   * the {@link IoModule#loadString(ResourcePath)} to load the stylesheet. If the underlying
   * module is a {@link ApiModule}, then an erroneous result is returned.
   *
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Message format</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code "Invalid Path"}</td>
   *     <td>The {@code uri} could not be parsed into a resource path.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "API-MODULE"}</td>
   *     <td>The underlying module is an {@link ApiModule}, stylesheet loading not supported.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "No Such File"}</td>
   *     <td>Module threw a {@link NoSuchFileException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Access Denied: %reason%"}</td>
   *     <td>Module threw a {@link AccessDeniedException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "IO Error: %message%"}</td>
   *     <td>Module threw an {@link IOException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Fatal parser error: %message%"}</td>
   *     <td>Unrecoverable failure trying to parse the stylesheet data.</td>
   *   </tr>
   * </table>
   *
   * @param uri file path
   *
   * @return Loaded stylesheet, or an empty result if the stylesheet could not be loaded for any reason
   */
  Result<Stylesheet, String> loadStylesheet(String uri);
}
