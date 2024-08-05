package net.arcadiusmc.delphi.resource;

import java.io.IOException;

public non-sealed interface IoModule extends ResourceModule {

  /**
   * Load a string buffer from the file pointed to by the specified path.
   *
   * @param path File path
   * @return The read file contents
   *
   * @throws IOException If an IO exception ocurred while reading the file
   */
  StringBuffer loadString(ResourcePath path) throws IOException;
}
