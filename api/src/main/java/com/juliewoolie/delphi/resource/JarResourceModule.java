package com.juliewoolie.delphi.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * IO module that loads module files from a jar's resources
 */
public class JarResourceModule implements IoModule {

  private final ClassLoader loader;
  private final String directory;

  private Charset charset = Charset.defaultCharset();

  private List<String> filePaths = null;

  /**
   * Constructs a new jar resource module.
   *
   * @param loader The class loader used for accessing resources
   * @param directory The path of the directory in the jar's resources to load from. Leave blank
   *                  to use the jar's root directory.
   */
  public JarResourceModule(ClassLoader loader, String directory) {
    this.loader = loader;
    this.directory = directory;
  }

  public ClassLoader getLoader() {
    return loader;
  }

  public String getDirectory() {
    return directory;
  }

  /**
   * Sets the charset used to read resource files.
   *
   * @param charset Charset
   *
   * @throws NullPointerException If {@code charset} is {@code null}
   */
  public void setCharset(@NotNull Charset charset) {
    Objects.requireNonNull(charset, "Null charset");
    this.charset = charset;
  }

  public Charset getCharset() {
    return charset;
  }

  public @Nullable List<String> getFilePaths() {
    return filePaths;
  }

  /**
   * Sets the file paths returned by {@link #getModulePaths(ResourcePath)}.
   * <p>
   * Since it's difficult to reliably get a list of files in a java class path
   * (or plugin class path), this method sets the list of files {@link #getModulePaths(ResourcePath)}
   * will filter through.
   *
   * @param filePaths File path list.
   */
  public void setFilePaths(@Nullable List<String> filePaths) {
    this.filePaths = filePaths;
  }

  // Fully qualify path
  private String fqPath(ResourcePath path) {
    if (directory.endsWith("/")) {
      return directory + path.path();
    }

    return directory + "/" + path.path();
  }

  @Override
  public @NotNull StringBuffer loadString(@NotNull ResourcePath path) throws IOException {
    String fqPath = fqPath(path);
    URL url = loader.getResource(fqPath);

    if (url == null) {
      throw new NoSuchFileException(fqPath);
    }

    try (InputStream stream = url.openStream()) {
      try (InputStreamReader reader = new InputStreamReader(stream, charset)) {
        StringWriter writer = new StringWriter();
        reader.transferTo(writer);
        return writer.getBuffer();
      }
    }
  }

  @Override
  public @NotNull Collection<String> getModulePaths(@NotNull ResourcePath pathSoFar) {
    if (filePaths == null || filePaths.isEmpty()) {
      return List.of();
    }

    String prefix = pathSoFar.path();
    List<String> stringList = new ArrayList<>();

    for (String filePath : filePaths) {
      if (!filePath.startsWith(prefix)) {
        continue;
      }

      stringList.add(filePath);
    }

    return stringList;
  }
}
