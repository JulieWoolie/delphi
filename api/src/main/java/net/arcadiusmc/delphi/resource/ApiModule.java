package net.arcadiusmc.delphi.resource;

import net.arcadiusmc.dom.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public non-sealed interface ApiModule extends ResourceModule {

  /**
   * Loads a document at the specified path.
   *
   * @param path Document path
   * @param factory Document factory
   *
   * @return Created document, or {@code null}, if the specified path does not point to a document.
   */
  @Nullable Document loadDocument(@NotNull ResourcePath path, @NotNull DocumentFactory factory);
}
