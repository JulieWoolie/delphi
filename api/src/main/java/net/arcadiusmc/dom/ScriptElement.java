package net.arcadiusmc.dom;

import net.arcadiusmc.delphi.resource.ResourcePath;
import org.jetbrains.annotations.Nullable;

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
}
