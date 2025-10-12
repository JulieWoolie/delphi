package com.juliewoolie.dom;

import com.juliewoolie.delphi.resource.ResourcePath;
import org.jetbrains.annotations.Nullable;

/**
 * Script element for adding functionality to Delphi pages with JavaScript.
 * <p>
 * <h3>Notes</h3>
 * <ul>
 *   <li>
 *     Once a script element is added, it will be executed immediately. Unless the DOM is
 *     currently being loaded, and {@link #isDeferred()} returns true.
 *   </li>
 *   <li>
 *     Removing a script element from the DOM tree does nothing. Similarly, removing its content
 *     or its {@code src} attribute after the script has been loaded and executed also does
 *     nothing.
 *   </li>
 *   <li>
 *     The JavaScript VM is only closed when the page is closed.
 *   </li>
 * </ul>
 */
public interface ScriptElement extends Element {

  /**
   * Get the script source.
   * <p>
   * Shorthand for accessing the {@link Attributes#SOURCE} attribute.
   *
   * @return Script source
   */
  @Nullable String getSource();

  /**
   * Get the full source path of the script source file.
   * @return Script source path
   */
  @Nullable ResourcePath getSourcePath();

  /**
   * Get the value of the {@link Attributes#DEFER} attribute.
   * @return {@code true}, if the defer attribute was set to true, {@code false} otherwise.
   */
  boolean isDeferred();
}
