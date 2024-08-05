package net.arcadiusmc.delphiplugin.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import net.arcadiusmc.delphi.resource.IoModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;

public abstract class FileSystemModule implements IoModule {

  protected final Path sourcePath;
  protected final FileSystem system;

  public FileSystemModule(Path sourcePath, FileSystem system) {
    this.sourcePath = sourcePath;
    this.system = system;
  }

  @Override
  public StringBuffer loadString(ResourcePath path) throws IOException {
    String elements = path.path();
    Path p = sourcePath.resolve(elements);

    StringWriter writer = new StringWriter();

    try (BufferedReader reader = Files.newBufferedReader(p)) {
      reader.transferTo(writer);
    }

    return writer.getBuffer();
  }

  @Override
  public @NotNull Collection<String> getModulePaths(ResourcePath pathSoFar) {
    Path path = sourcePath.resolve(pathSoFar.path());

    if (!Files.isDirectory(path)) {
      return List.of();
    }

    List<String> stringList = new ArrayList<>();

    try (Stream<Path> stream = Files.walk(path)) {
      stream.forEach(p -> {
        if (Files.isDirectory(p)) {
          return;
        }

        Path relative = path.relativize(p);

        // I hate windows
        String str = relative.toString().replace("\\", "/");

        if (str.contains(" ")) {
          String[] split = str.split("/+");
          StringBuilder builder = new StringBuilder();

          for (int i = 0; i < split.length; i++) {
            if (i != 0) {
              builder.append('/');
            }

            String el = split[i];

            if (el.contains(" ")) {
              builder.append('"')
                  .append(el)
                  .append('"');
            } else {
              builder.append(el);
            }
          }

          str = builder.toString();
        }

        stringList.add(str);
      });
    } catch (IOException e) {
      // Ignored, just return the list
    }

    return stringList;
  }
}
