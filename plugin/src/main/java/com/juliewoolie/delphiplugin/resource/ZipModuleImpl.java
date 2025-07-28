package com.juliewoolie.delphiplugin.resource;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import lombok.Getter;
import com.juliewoolie.delphi.resource.ZipModule;

@Getter
public class ZipModuleImpl extends FileSystemModule implements ZipModule {

  private final Path zipFile;

  public ZipModuleImpl(Path sourcePath, FileSystem system, Path zipFile) {
    super(sourcePath, system);
    this.zipFile = zipFile;
  }

  @Override
  public FileSystem getFileSystem() {
    return system;
  }

  @Override
  public void close() throws IOException {
    system.close();
  }
}
