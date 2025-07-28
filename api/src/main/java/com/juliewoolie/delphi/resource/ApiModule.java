package com.juliewoolie.delphi.resource;

import com.juliewoolie.delphi.util.Result;
import com.juliewoolie.dom.Document;
import org.jetbrains.annotations.NotNull;

/**
 * Modules that are defined programmatically by plugins.
 * <p>
 * API modules are only queried when a {@code .xml} document is loaded. It's presumed that an API
 * module creates documents and links style sheets to them programmatically so the ability to load
 * stylesheets and other external files (like item json files) is not provided.
 */
public non-sealed interface ApiModule extends ResourceModule {

  /**
   * Loads a document at the specified path.
   * <p>
   * The path given to this method will never have an empty file path. If a user asks for a page
   * with only a module name given, then the system will automatically add the {@code index.xml}
   * file to that path.
   * <p>
   * While this function is allowed to return any kind of error, the following errors are officially
   * supported, meaning that when this method is called through {@link ViewResources#loadDocument(String)}
   * they will not be prefixed with {@code "Module error: %reason%"}.
   *
   * <table>
   *   <caption>Supported errors</caption>
   *   <tr>
   *     <th>Error message</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code "No such file"}</td>
   *     <td>Means the path does not point to a document file</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Access Denied: %reason%}</td>
   *     <td>Access to the specified resource was denied</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "IO Error: %reason%"}</td>
   *     <td>Indicates an IO error some kind ocurred</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Missing plugins: %plugin-list%"}</td>
   *     <td>
   *       Indicates one or more required plugins was missing. {@code %plugin-list%} is a
   *       comma separated list of plugin names
   *     </td>
   *   </tr>
   * </table>
   * @param path Document path
   * @param context Document opening context
   *
   * @return Created document, or an erroneous result.
   */
  Result<Document, String> loadDocument(@NotNull ResourcePath path, @NotNull DocumentContext context);
}
