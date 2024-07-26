package net.arcadiusmc.delphi.resource;

import java.nio.file.Path;

public interface DirectoryModule extends IoModule {

  /**
   * Gets the module's directory
   * @return Module directory
   */
  Path getDirectory();
}
