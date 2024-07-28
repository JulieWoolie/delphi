package net.arcadiusmc.delphiplugin.resource;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import net.arcadiusmc.delphi.resource.DirectoryModule;

public class DirectoryModuleImpl extends FileSystemModule implements DirectoryModule {

  public DirectoryModuleImpl(Path sourcePath, FileSystem system) {
    super(sourcePath, system);
  }

  @Override
  public Path getDirectory() {
    return sourcePath;
  }
}
