package net.arcadiusmc.delphi.resource;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import net.arcadiusmc.delphi.Delphi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a module name and a URI used for loading a document's resources.
 * <p>
 * Page paths are made up of 3 elements: the module name, the path and the query.
 *
 * <h2>Module Name</h2>
 * Name of a specific directory/zip archive in the {@link DelphiResources#getModuleDirectory()}
 * directory.
 *
 * <h2>Path</h2>
 * The file path of a file/directory in the directory/zip archive the module name
 * points to.
 *
 * <h2>Query</h2>
 * Optional search parameters that scripts can use.
 */
public sealed interface PagePath permits MutablePagePath, PathImpl {

  Pattern VALID_ELEMENT = Pattern.compile("[a-zA-Z0-9 .\\-_$]+");
  Pattern VALID_QUERY = Pattern.compile("[a-zA-Z0-9.\\-_$]+");

  /**
   * Creates a page path with a specified {@code moduleName}
   *
   * @param moduleName Path Module name
   * @return Created path
   *
   * @see MutablePagePath
   */
  static MutablePagePath create(@NotNull String moduleName) {
    return new MutablePathImpl(moduleName);
  }

  /**
   * Validates a filename.
   * <p>
   * For a filename to be "valid" it must not be null and match the {@link #VALID_ELEMENT}
   * regex pattern.
   *
   * @param element Element to validate
   *
   * @throws NullPointerException If the {@code element} is {@code null}
   * @throws IllegalArgumentException If the {@code element} is an invalid filename.
   */
  static void validateFilename(String element) {
    Objects.requireNonNull(element, "Null element");

    if (VALID_ELEMENT.matcher(element).matches()) {
      return;
    }

    throw new IllegalArgumentException("Invalid path element: " + element);
  }

  /**
   * Validates a query key/value.
   * <p>
   * For a query key or value to be "valid" it must not be null and must match the
   * {@link #VALID_QUERY} regex pattern.
   *
   * @param query Query element to validate.
   *
   * @throws NullPointerException If the {@code query} is null.
   * @throws IllegalArgumentException If the {@code query} is invalid.
   */
  static void validateQuery(String query) {
    Objects.requireNonNull(query, "Null query");

    if (VALID_QUERY.matcher(query).matches()) {
      return;
    }

    throw new IllegalArgumentException("Invalid element: " + query);
  }

  /**
   * Gets the module name element of the path.
   * @return Module name
   */
  @NotNull
  String getModuleName();

  /**
   * Gets the value of a query
   * @param key Query key
   * @return Query value, or {@code null}, if there's no query element with the
   *         specified {@code key}.
   */
  @Nullable
  String getQuery(String key);

  /**
   * Gets an unmodifiable set of the query keys.
   * @return Query keys
   */
  Set<String> getQueryKeys();

  /**
   * Gets an unmodifiable list of the path's elements
   * @return Unmodifiable path element list
   */
  List<String> getElements();

  /**
   * Gets the amount of elements in the path
   * @return Element count
   */
  int elementCount();

  /**
   * Gets an element at a specific index
   * <p>
   * Assume we have the path {@code foobar/filepath/filename.ext}, these would be the indexes:
   * <table><thead>
   *   <tr>
   *     <th>Index</th>
   *     <th>Element</th>
   *   </tr></thead>
   * <tbody>
   *   <tr>
   *     <td>0</td>
   *     <td>{@code foobar}</td>
   *   </tr>
   *   <tr>
   *     <td>1</td>
   *     <td>{@code filepath}</td>
   *   </tr>
   *   <tr>
   *     <td>2</td>
   *     <td>{@code filename.ext}</td>
   *   </tr>
   * </tbody>
   * </table>
   *
   * @param index Element index
   * @return Element
   *
   * @throws IndexOutOfBoundsException If {@code index} is less than 0, or greater/equal to
   *                                   {@link #elementCount()}.
   */
  @NotNull
  String getElement(int index) throws IndexOutOfBoundsException;

  /**
   * Gets the file path part of this path.
   * @return File path string, or {@code ""}, if no file elements were specified
   */
  @NotNull
  String elements();

  /**
   * Gets the query part of this path as a string.
   * <p>
   * Example: {@code ?foo=bar&bar=foo}
   * @return Query string, or {@code ""} if there are no query elements.
   */
  @NotNull
  String query();

  /**
   * Combines the module name, elements and queries into a string that can be given to
   * {@link Delphi#parsePath(String)} to parse.
   *
   * <h3>Examples</h3>
   * <ul>
   *   <li>{@code module}</li>
   *   <li>{@code module:file.ext}</li>
   *   <li>{@code module:"file with space.ext"}</li>
   *   <li>{@code module:"dir with space"/file.xml}</li>
   *   <li>{@code module:foobar/file.ext}</li>
   *   <li>{@code module:foobar/file.ext?foo=bar&bar=foo}</li>
   *   <li>{@code module:foobar/file.ext?foo&bar=false}</li>
   *   <li>{@code module:?foo=bar&bar=foo}</li>
   * </ul>
   *
   * @return String representation of this path
   */
  String toString();
}
