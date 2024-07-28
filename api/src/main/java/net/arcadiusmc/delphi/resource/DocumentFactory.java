package net.arcadiusmc.delphi.resource;

import net.arcadiusmc.dom.Document;
import org.jetbrains.annotations.NotNull;

public interface DocumentFactory {

  /**
   * Creates a new document.
   * @return Created document
   */
  @NotNull Document createNewDocument();
}
