package net.arcadiusmc.delphi.resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import net.arcadiusmc.delphi.util.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DelphiResources {

  /**
   * Gets the module directory.
   * <p>
   * The returned path is the {@code modules} directory inside the Delphi plugin's data folder.
   *
   * @return Module directory
   */
  Path getModuleDirectory();

  /**
   * Create a directory module with the specified {@code path} as the directory.
   *
   * @param path Directory path
   * @return Created module.
   *
   * @throws NullPointerException If {@code path} is {@code null}
   */
  DirectoryModule createDirectoryModule(@NotNull Path path);

  /**
   * Create a zip module from the specified {@code zipPath}.
   * <p>
   * This method will attempt to immediately open and read the zip file into memory, and will return
   * an erroneous result if it fails to do so for any reason.
   *
   * <table>
   *   <caption>Error messages</caption>
   *   <tr>
   *     <th>Format</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code "No Such File"}</td>
   *     <td>{@code zipPath} file doesn't exist</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "Access Denied: %reason%"}</td>
   *     <td>Read access was denied to the zip file</td>
   *   </tr>
   *   <tr>
   *     <td>{@code "IO Error: %message%"}</td>
   *     <td>Threw an {@link IOException}</td>
   *   </tr>
   * </table>
   *
   * @param zipPath Zip file path
   * @return A successful result if the zip archive was opened, otherwise an erroneous result.
   *
   * @throws NullPointerException If {@code zipPath} is {@code null}
   */
  Result<ZipModule, DelphiException> createZipModule(@NotNull Path zipPath);

  /**
   * Registers a module.
   * <p>
   * This method will return {@code false} if another module has already been registered
   * with the specified {@code moduleName}.
   *
   * @param moduleName Name of the module.
   * @param module Module.
   *
   * @return {@code true}, if the module was registered,
   *         {@code false}, if the specified name is already in use.
   *
   * @throws NullPointerException If either {@code moduleName} or {@code module} is {@code null}.
   * @throws IllegalArgumentException If the module name doesn't pass the {@link ResourcePath#validateQuery(String)} check.
   */
  boolean registerModule(@NotNull String moduleName, @NotNull ResourceModule module);

  /**
   * Unregisters a module
   * <p>
   * When a module is unregistered, all pages open to the specified module are closed as well.
   * <p>
   * This method only unregisters modules that were registered with the {@link #registerModule(String, ResourceModule)}
   * method. Any modules inside the {@link #getModuleDirectory()} cannot be unregistered.
   *
   * @param moduleName Module name
   *
   * @return {@code true} if a module with the {@code moduleName} was found and was removed,
   *         {@code false} otherwise.
   */
  @Contract("null -> false")
  boolean unregisterModule(@Nullable String moduleName);

  /**
   * Attempts to locate a module.
   * <p>
   * This will first attempt to find a module registered with {@link #registerModule(String, ResourceModule)}.
   * No registered module is found, it will attempt to find a directory or a zip archive with the
   * specified name.
   * <p>
   * This method assumes the input has no file extension.
   *
   * <table>
   *   <caption>Result errors</caption>
   *   <tr>
   *     <th>Error code</th>
   *     <th>Description</th>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_EMPTY_MODULE_NAME}</td>
   *     <td>The module name was empty or {@code null}</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_MODULE_DIRECTORY_NOT_FOUND}</td>
   *     <td>Couldn't find any registered modules matching the name, and the module directory doesn't exist</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_MODULE_ZIP_ACCESS_DENIED}</td>
   *     <td>Attempted to open a {@code .zip} module file, but access was denied</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_IO_ERROR}</td>
   *     <td>An IO error occurred while trying to read a {@code .zip} module file</td>
   *   </tr>
   *   <tr>
   *     <td>{@code ERR_MODULE_UNKNOWN}</td>
   *     <td>No registered module, directory or {@code .zip} module was found that matched the specified {@code moduleName}</td>
   *   </tr>
   * </table>
   *
   * @param moduleName Name of the module
   * @return Found module, or an erroneous result if no module was found.
   */
  Result<ResourceModule, DelphiException> findModule(String moduleName);

  /**
   * Gets an array list of all module names. This list inclues all registered modules
   * as well as all directory and zip archive modules installed in {@link #getModuleDirectory()}
   *
   * @return Module name list.
   */
  List<String> getModuleNames();
}
