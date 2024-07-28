package net.arcadiusmc.delphi.resource;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public sealed interface ResourceModule permits IoModule, ApiModule {

  /**
   * Gets a collection of files/documents contained in this module.
   * <p>
   * The resulting file paths are used mainly for suggestions in the
   * {@code /delphi open <player> <path>} command.
   * <p>
   * Assuming we have the following files in the module: <pre><code>
   * index.xml
   * item-data.json
   * global-style.scss
   * admin-tab/index.xml
   * admin-tab/head-item.json
   * admin-tab/confirm/dialogue.xml
   * admin-tab/confirm/style.scss
   * spectator-tab/index.xml
   * </code></pre>
   * The following inputs should yield these results:
   *
   * <table>
   *  <tr>
   *    <th>Input</th>
   *    <th>Expected result</th>
   *  </tr>
   *  <tr>
   *    <td>Empty input</td>
   *    <td>Returns the entire file list</td>
   *  </tr>
   *  <tr>
   *    <td>{@code admin-tab/}</td>
   *    <td>{@code index.xml}, {@code head-item.json}, {@code confirm/dialogue.xml}, {@code confirm/style.scss}</td>
   *  </tr>
   *  <tr>
   *    <td>{@code admin-tab/confirm/}</td>
   *    <td>{@code dialogue.xml}, {@code style.scss}</td>
   *  </tr>
   *  <tr>
   *    <td>{@code spectator-tab/}</td>
   *    <td>{@code index.xml}</td>
   *  </tr>
   * </table>
   * @param pathSoFar The current page path.
   * @return A list of files contained in the directory specified by {@code pathSoFar}.
   */
  @NotNull Collection<String> getModulePaths(ResourcePath pathSoFar);
}
