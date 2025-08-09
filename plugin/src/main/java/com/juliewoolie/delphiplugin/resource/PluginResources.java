package com.juliewoolie.delphiplugin.resource;

import static com.juliewoolie.delphi.resource.DelphiException.ERR_EMPTY_MODULE_NAME;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_IO_ERROR;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_MODULE_DIRECTORY_NOT_FOUND;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_MODULE_UNKNOWN;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_MODULE_ZIP_ACCESS_DENIED;

import com.google.common.base.Strings;
import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.Rule;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.delphi.resource.DelphiException;
import com.juliewoolie.delphi.resource.DelphiResources;
import com.juliewoolie.delphi.resource.DirectoryModule;
import com.juliewoolie.delphi.resource.ResourceModule;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphi.resource.ZipModule;
import com.juliewoolie.delphi.util.Result;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.delphiplugin.DelphiPlugin;
import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.delphiplugin.devtools.DevtoolModule;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.StackWalker.Option;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PluginResources implements DelphiResources {

  static final String DEVTOOLS_MODULE = "devtools";
  static final String DEFAULT_STYLE = "data/default-style.scss";
  static final String SCRIPTING_TOGGLE = "scripting.properties";

  private static final Logger LOGGER = Loggers.getLogger("DelphiResources");

  static final String ZIP_EXT = ".zip";

  @Getter
  private final Map<String, RegisteredModule> registered = new HashMap<>();
  private final Map<Path, FileSystemModule> cachedFileModules = new HashMap<>();

  private final Path directory;
  private FileSystemProvider zipProvider;

  @Getter
  private ChimeraStylesheet defaultStyle;

  public DelphiPlugin plugin;

  public PluginResources(DelphiPlugin plugin) {
    this.plugin = plugin;
    this.directory = plugin.getDataPath().resolve("modules");

    ensureDirectoryExists();
    registerDevtools();
  }

  private void registerDevtools() {
    RegisteredModule mod = new RegisteredModule(DEVTOOLS_MODULE, new DevtoolModule());
    mod.hidden = true;
    registered.put(DEVTOOLS_MODULE, mod);
  }

  private void ensureDirectoryExists() {
    if (Files.isDirectory(directory)) {
      return;
    }

    try {
      Files.createDirectories(directory);
    } catch (IOException exc) {
      LOGGER.error("Failed to create modules directory", exc);
    }
  }

  public void loadDefaultStyle() {
    URL url = getClass().getClassLoader().getResource(DEFAULT_STYLE);

    if (url == null) {
      LOGGER.error("Failed to load {}, resource not found", DEFAULT_STYLE);
      return;
    }

    StringBuffer buf;

    try (InputStream stream = url.openStream()) {
      StringWriter writer = new StringWriter();
      InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
      reader.transferTo(writer);
      buf = writer.getBuffer();
    } catch (IOException exc) {
      LOGGER.error("Error loading {}: IO Error", DEFAULT_STYLE, exc);
      defaultStyle = null;
      return;
    }

    defaultStyle = Chimera.parseSheet(buf, DEFAULT_STYLE);
    defaultStyle.setFlags(ChimeraStylesheet.FLAG_DEFAULT_STYLE);

    for (int i = 0; i < defaultStyle.getLength(); i++) {
      Rule rule = defaultStyle.getRule(i);

      rule.getSpec().idColumn -= 10;
      rule.getSpec().classColumn -= 10;
      rule.getSpec().typeColumn -= 10;
    }
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

    Class<?> callerClass = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass();
    Plugin plugin;

    if (callerClass.getClassLoader() instanceof ConfiguredPluginClassLoader loader) {
      plugin = loader.getPlugin();
    } else {
      plugin = null;
    }

    RegisteredModule registeredModule = new RegisteredModule(moduleName, module);
    registeredModule.plugin = plugin;

    registered.put(key, registeredModule);

    return true;
  }

  @Override
  public Result<ResourceModule, DelphiException> findModule(String moduleName) {
    if (Strings.isNullOrEmpty(moduleName)) {
      return Result.err(new DelphiException(ERR_EMPTY_MODULE_NAME, "Empty/null module name"));
    }

    String lower = moduleName.toLowerCase();
    RegisteredModule found = registered.get(lower);

    if (found != null) {
      return Result.ok(found.module);
    }

    if (!Files.isDirectory(directory)) {
      return Result.err(
          new DelphiException(ERR_MODULE_DIRECTORY_NOT_FOUND, "Module directory doesn't exist")
      );
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
            return Result.err(
                new DelphiException(
                    ERR_MODULE_ZIP_ACCESS_DENIED,
                    "Zip access denied: " + acc.getReason(),
                    acc
                )
            );
          } catch (IOException exc) {
            return Result.err(new DelphiException(ERR_IO_ERROR, exc));
          }
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error attempting to iterate module directory", e);
    }

    return Result.err(new DelphiException(ERR_MODULE_UNKNOWN, moduleName));
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

  public DirectoryModule createDirectoryModule(@NotNull Path path) {
    Objects.requireNonNull(path, "Null directory path");

    return (DirectoryModule) cachedFileModules.computeIfAbsent(
        path,
        p -> new DirectoryModuleImpl(p, p.getFileSystem())
    );
  }

  @Override
  public Result<ZipModule, DelphiException> createZipModule(@NotNull Path zipPath) {
    Objects.requireNonNull(zipPath, "Null zip path");

    try {
      ZipModule mod = getZipModule(zipPath);
      return Result.ok(mod);
    } catch (IOException exc) {
      return Result.ioError(exc);
    }
  }

  private FileSystemProvider getZipProvider() {
    if (zipProvider != null) {
      return zipProvider;
    }

    return zipProvider = findZipProvider();
  }

  static FileSystemProvider findZipProvider() {
    return FileSystemProvider.installedProviders().stream()
        .filter(provider -> provider.getScheme().equals("jar")) // JAR ???????
        .findFirst()
        .orElse(null);
  }

  @Override
  public boolean unregisterModule(String moduleName) {
    if (Strings.isNullOrEmpty(moduleName)) {
      return false;
    }

    RegisteredModule found = registered.get(moduleName);
    if (found == null) {
      return false;
    }

    if (plugin != null) {
      List<PageView> withModule = new ArrayList<>();
      for (PageView openView : plugin.getViewManager().getOpenViews()) {
        if (!openView.getPath().getModuleName().equals(found.name)) {
          continue;
        }
        withModule.add(openView);
      }

      for (PageView view : withModule) {
        view.close();
      }
    }

    registered.remove(moduleName);
    return true;
  }

  @Override
  public List<String> getModuleNames() {
    return getModulesNames(true);
  }

  public List<String> getNonHiddenModuleNames() {
    return getModulesNames(false);
  }

  private List<String> getModulesNames(boolean includeHidden) {
    List<String> result = new ArrayList<>();

    for (RegisteredModule value : registered.values()) {
      if (value.hidden && !includeHidden) {
        continue;
      }

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

  public static class RegisteredModule {
    public final String name;
    public final ResourceModule module;
    public Plugin plugin;
    public boolean hidden = false;

    public RegisteredModule(String name, ResourceModule module) {
      this.name = name;
      this.module = module;
    }
  }
}