package net.arcadiusmc.delphiplugin.resource;

import com.google.common.base.Strings;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphi.resource.DelphiResources;
import net.arcadiusmc.delphi.resource.DirectoryModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ZipModule;
import net.arcadiusmc.delphi.util.Result;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class Modules implements DelphiResources {

  private static final Logger LOGGER = Loggers.getLogger("DelphiResources");

  static final String ZIP_EXT = ".zip";

  private final Map<String, RegisteredModule> registered = new HashMap<>();
  private final Map<Path, FileSystemModule> cachedFileModules = new HashMap<>();

  private final Path directory;
  private FileSystemProvider zipProvider;

  public Modules(Path directory) {
    this.directory = directory;
  }

  @Override
  public Path getModuleDirectory() {
    return directory;
  }

  @Override
  public boolean registerModule(@NotNull String moduleName, @NotNull ResourceModule module) {
    Objects.requireNonNull(moduleName, "Null module name");
    Objects.requireNonNull(module, "Null module");

    ResourcePath.validateQuery(moduleName);

    String key = moduleName.toLowerCase();

    if (registered.containsKey(key)) {
      return false;
    }

    RegisteredModule registeredModule = new RegisteredModule(moduleName, module);
    registered.put(key, registeredModule);

    return true;
  }

  @Override
  public Result<ResourceModule, String> findModule(String moduleName) {
    if (Strings.isNullOrEmpty(moduleName)) {
      return Result.err("Null/empty module name");
    }

    String lower = moduleName.toLowerCase();
    RegisteredModule found = registered.get(lower);

    if (found != null) {
      return Result.ok(found.module);
    }

    if (!Files.isDirectory(directory)) {
      return Result.err("Module directory doesn't exist");
    }

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path path : stream) {
        String fname = path.getFileName().toString();

        if (Files.isDirectory(path)) {
          if (fname.equalsIgnoreCase(moduleName)) {
            return Result.ok(createDirectoryModule(path));
          }

          continue;
        }

        if (fname.endsWith(ZIP_EXT)) {
          String extRemoved = fname.substring(0, fname.length() - ZIP_EXT.length());

          if (!extRemoved.equalsIgnoreCase(moduleName)) {
            continue;
          }

          try {
            return Result.ok(getZipModule(path));
          } catch (AccessDeniedException acc) {
            return Result.formatted("Zip access denied: %s", acc.getReason());
          } catch (IOException exc) {
            return Result.formatted("IO error: %s", exc.getMessage());
          }
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error attempting to iterate module directory", e);
    }

    return Result.err("Unknown module");
  }

  ZipModule getZipModule(Path path) throws IOException {
    FileSystemModule cached = cachedFileModules.get(path);
    if (cached instanceof ZipModuleImpl zip) {
      return zip;
    }

    FileSystemProvider provider = getZipProvider();
    FileSystem system = provider.newFileSystem(path, Map.of());

    ZipModuleImpl module = new ZipModuleImpl(system.getPath(""), system, path);
    cachedFileModules.put(path, module);

    return module;
  }

  DirectoryModule createDirectoryModule(Path path) {
    return (DirectoryModule) cachedFileModules.computeIfAbsent(
        path,
        p -> new DirectoryModuleImpl(p, p.getFileSystem())
    );
  }

  private FileSystemProvider getZipProvider() {
    if (zipProvider != null) {
      return zipProvider;
    }

    return zipProvider = FileSystemProvider.installedProviders().stream()
        .filter(provider -> provider.getScheme().equals("jar")) // JAR ???????
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<String> getModuleNames() {
    List<String> result = new ArrayList<>();

    for (RegisteredModule value : registered.values()) {
      result.add(value.name);
    }

    if (!Files.isDirectory(directory)) {
      return result;
    }

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path path : stream) {
        String fname = path.getFileName().toString();

        if (Files.isDirectory(path)) {
          result.add(fname);
        } else if (fname.endsWith(ZIP_EXT)) {
          String extRemoved = fname.substring(0, fname.length() - ZIP_EXT.length());
          result.add(extRemoved);
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error attempting to iterate module directory");
    }

    return result;
  }

  record RegisteredModule(String name, ResourceModule module) {

  }
}
