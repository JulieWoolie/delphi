package net.arcadiusmc.delphi.resource;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.style.Stylesheet;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Resource manager for an open {@link DocumentView}
 */
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
   * Gets the document view these resource belong to.
   * @return View
   */
  DocumentView getView();

  /**
   * Loads an item stack from a JSON file.
   * <p>
   * Any errors thrown during the method's execution will be caught and returned in the
   * result.
   * <P>
   * If the underlying module is not an {@link IoModule} then an erroneous result is returned,
   * otherwise {@link IoModule#loadString(ResourcePath)} is called, and it attempts to parse JSON
   * from the returned buffer.
   * <p>
   * The item is loaded from minecraft's JSON format. Example: <pre><code>
   * {
   *   "id": "minecraft:netherite_sword",
   *   "count": 1,
   *   "components": {
   *     "minecraft:enchantments": {
   *       "levels": {
   *         "minecraft:sharpness": 5
   *       }
   *     }
   *   }
   * } </code></pre>
   *
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Error code</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_INVALID_PATH}</td>
   *     <td>The {@code uri} could not be parsed into a resource path.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_API_MODULE}</td>
   *     <td>If the module is not an {@link IoModule}.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_NO_FILE}</td>
   *     <td>Module threw a {@link NoSuchFileException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_ACCESS_DENIED}</td>
   *     <td>Module threw a {@link AccessDeniedException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_IO_ERROR}</td>
   *     <td>Module threw an {@link IOException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SYNTAX}</td>
   *     <td>Failed to parse JSON from file contents</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SCHEMA_ERROR}</td>
   *     <td>If the item fails to load from the loaded JSON</td>
   *   </tr>
   * </table>
   *
   * @param uri file path
   * @return Loaded item, or an erroneous result, if the item cannot be loaded for any reason
   */
  Result<ItemStack, DelphiException> loadItemStack(String uri);

  /**
   * Loads an item stack from the specified JSON string.
   * <p>
   * Any errors thrown during the method's execution will be caught and returned in the
   * result.
   * <p>
   * The item is loaded from minecraft's JSON format. Example: <pre><code>
   * {
   *   "id": "minecraft:netherite_sword",
   *   "count": 1,
   *   "components": {
   *     "minecraft:enchantments": {
   *       "levels": {
   *         "minecraft:sharpness": 5
   *       }
   *     }
   *   }
   * } </code></pre>
   *
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Error code</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_INVALID_PATH}</td>
   *     <td>The {@code uri} could not be parsed into a resource path.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SYNTAX}</td>
   *     <td>Failed to parse JSON from file contents</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SCHEMA_ERROR}</td>
   *     <td>If the item fails to load from the loaded JSON</td>
   *   </tr>
   * </table>
   *
   * @param json Item JSON data
   * @return Loaded item, or an erroneous result, if the item cannot be loaded for any reason
   */
  Result<ItemStack, DelphiException> parseItemStack(String json);

  /**
   * Attempts to load a document from the specified resource path.
   * <p>
   * Any errors thrown during the method's execution will be caught and returned in the
   * result.
   * <p>
   * If {@link #getModule()} is a {@link IoModule} then this method attempts to use the
   * {@link IoModule#loadString(ResourcePath)} to load the document's XML content. If the
   * underlying module is a {@link ApiModule}, then {@link ApiModule#loadDocument(ResourcePath, DocumentContext)}
   * is called.
   *
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Error code</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_INVALID_PATH}</td>
   *     <td>The {@code uri} could not be parsed into a resource path.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_MODULE_ERROR}</td>
   *     <td>If {@link #getModule()} is an {@link ApiModule} and it returned an erroneous result.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_NO_FILE}</td>
   *     <td>Module threw a {@link NoSuchFileException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_ACCESS_DENIED}</td>
   *     <td>Module threw a {@link AccessDeniedException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_IO_ERROR}</td>
   *     <td>Module threw an {@link IOException}</td>
   *   </tr>
   *   <tr>
   *     <td style="width: 25%;">{@code ERR_SAX_PARSER_INIT}</td>
   *     <td>Failed to instantiate SAX parser to parse the XML document.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_DOC_PARSE}</td>
   *     <td>Unrecoverable failure trying to parse the XML data</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_MISSING_PLUGINS}</td>
   *     <td>
   *       One or more of the document's required plugins are missing.The returned exception's
   *       message will contain a comma separated list of plugins that it failed to find.
   *     </td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_UNKNOWN}</td>
   *     <td>An unknown error caused the document parser to return a {@code null} document</td>
   *   </tr>
   * </table>
   *
   * @param uri file path
   *
   * @return Loaded document, or an erroneous result if the document cannot be loaded for
   *         any reason.
   */
  Result<Document, DelphiException> loadDocument(String uri);

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
   *     <th>Error code</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_INVALID_PATH}</td>
   *     <td>The {@code uri} could not be parsed into a resource path.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_API_MODULE}</td>
   *     <td>The underlying module is an {@link ApiModule}, stylesheet loading not supported.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_NO_FILE}</td>
   *     <td>Module threw a {@link NoSuchFileException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_ACCESS_DENIED}</td>
   *     <td>Module threw a {@link AccessDeniedException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_IO_ERROR}</td>
   *     <td>Module threw an {@link IOException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SYNTAX}</td>
   *     <td>Unrecoverable failure trying to parse the stylesheet data.</td>
   *   </tr>
   * </table>
   *
   * @param uri file path
   *
   * @return Loaded stylesheet, or an empty result if the stylesheet could not be loaded for any reason
   *
   * @see DelphiException
   */
  Result<Stylesheet, DelphiException> loadStylesheet(String uri);

  /**
   * Attempts to load a chat component.
   * <p>
   * Any errors thrown during the methods execution will be caught and an erroneous result
   * returned.
   * <p>
   * If {@link #getModule()} is a {@link IoModule} then this method attempts to use
   * the {@link IoModule#loadString(ResourcePath)} to load the chat component. If the underlying
   * module is a {@link ApiModule}, then an erroneous result is returned.
   *
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Error code</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_INVALID_PATH}</td>
   *     <td>The {@code uri} could not be parsed into a resource path.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_API_MODULE}</td>
   *     <td>The underlying module is an {@link ApiModule}, stylesheet loading not supported.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_NO_FILE}</td>
   *     <td>Module threw a {@link NoSuchFileException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_ACCESS_DENIED}</td>
   *     <td>Module threw a {@link AccessDeniedException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_IO_ERROR}</td>
   *     <td>Module threw an {@link IOException}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SYNTAX}</td>
   *     <td>Unrecoverable failure trying to parse the component data.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SCHEMA_ERROR}</td>
   *     <td>Only thrown when reading JSON, means the JSON is incorrect.</td>
   *   </tr>
   * </table>
   *
   * @param uri Path to the component data.
   * @param format Format to load from, if {@code null}, uses {@link ComponentFormat#JSON}
   *
   * @return Loaded component, or an empty resul, if the component couldn't be loaded for any
   *         reason.
   */
  Result<Component, DelphiException> loadComponent(@NotNull String uri, @Nullable ComponentFormat format);

  /**
   * Parses the specified data string into a component.
   * <p>
   * Any errors thrown during the methods execution will be caught and an erroneous result
   * returned.
   *
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Error code</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SYNTAX}</td>
   *     <td>Unrecoverable failure trying to parse the component data.</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_SCHEMA_ERROR}</td>
   *     <td>Only thrown when reading JSON, means the JSON is incorrect.</td>
   *   </tr>
   * </table>
   *
   * @param data
   * @param format
   * @return
   */
  Result<Component, DelphiException> parseComponent(@NotNull String data, @Nullable ComponentFormat format);

  /**
   * Component format
   */
  enum ComponentFormat {
    /**
     * Normal JSON format
     * <p>
     * Example: {@code {"text":"Hello, world!","bold":true}}
     */
    JSON,

    /**
     * Mini message XML-like format
     * <p>
     * Example: {@code <bold>Hello, world!}
     */
    MINIMESSAGE,
    ;
  }
}
