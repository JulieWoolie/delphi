package net.arcadiusmc.delphi.resource;

import java.nio.file.Path;

/**
 * Module which is linked to a directory and loads files and page documents from that directory.
 */
public interface DirectoryModule extends IoModule {

  /**
   * Gets the module's directory
   * @return Module directory
   */
  Path getDirectory();
}
