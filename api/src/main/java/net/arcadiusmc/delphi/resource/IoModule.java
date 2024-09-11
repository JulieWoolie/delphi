package net.arcadiusmc.delphi.resource;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;

/**
 * A module which loads text files from some kind of IO, for example the file system.
 */
public non-sealed interface IoModule extends ResourceModule {

  /**
   * Load a string buffer from the file pointed to by the specified path.
   *
   * @param path File path
   * @return The read file contents
   *
   * @throws IOException If an IO exception ocurred while reading the file
   */
  @NotNull StringBuffer loadString(@NotNull ResourcePath path) throws IOException;
}
