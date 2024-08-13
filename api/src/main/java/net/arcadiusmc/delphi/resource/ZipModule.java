package net.arcadiusmc.delphi.resource;

import java.io.Closeable;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public interface ZipModule extends IoModule, Closeable {

  /**
   * Gets the underlying ZIP file system
   * @return ZIP file system
   */
  FileSystem getFileSystem();

  /**
   * Gets the ZIP file of this module
   * @return module zip file
   */
  Path getZipFile();
}
