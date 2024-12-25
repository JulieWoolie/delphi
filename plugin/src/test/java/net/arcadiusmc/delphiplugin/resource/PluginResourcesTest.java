package net.arcadiusmc.delphiplugin.resource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.DirectoryModule;
import net.arcadiusmc.delphi.resource.JarResourceModule;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ZipModule;
import net.arcadiusmc.delphi.util.Result;
import org.junit.jupiter.api.Test;

class PluginResourcesTest {

  static final File file;
  static final Path path;
  static final PluginResources PLUGIN_RESOURCES;

  static {
    file = new File(PluginResourcesTest.class.getClassLoader().getResource("test-modules").getFile());
    path = file.toPath();
    PLUGIN_RESOURCES = new PluginResources(path);
  }

  @Test
  void testModulePathList() {
    Result<ResourceModule, DelphiException> opt = PLUGIN_RESOURCES.findModule("module");
    assertTrue(opt.isSuccess());

    DirectoryModule module = assertInstanceOf(DirectoryModule.class, opt.getOrThrow());

    Collection<String> list = module.getModulePaths(ResourcePath.create("module"));
    assertEquals(3, list.size());
    assertTrue(list.contains("path.xml"));
    assertTrue(list.contains("subdir1/randomfile.json"));
    assertTrue(list.contains("\"dir with space\"/file.xml"));

    list = module.getModulePaths(ResourcePath.create("module").addElement("subdir1"));
    assertEquals(list.size(), 1);
    assertTrue(list.contains("randomfile.json"));
  }

  @Test
  void testJarModule() {
    JarResourceModule module = new JarResourceModule(getClass().getClassLoader(), "test-modules/module");
    assertDoesNotThrow(() -> {
      module.loadString(ResourcePath.create("asd").addElement("path.xml"));
    });
  }

  @Test
  void testZipModule() {
    Result<ResourceModule, DelphiException> opt = PLUGIN_RESOURCES.findModule("zipped");
    assertTrue(opt.isSuccess());

    ZipModule module = assertInstanceOf(ZipModule.class, opt.getOrThrow());
    assertDoesNotThrow(() -> {
      module.loadString(ResourcePath.create("zipped").addElement("path.xml"));
    });
  }

  @Test
  void testRegularModule() {
    Result<ResourceModule, DelphiException> opt = PLUGIN_RESOURCES.findModule("module");
    assertTrue(opt.isSuccess());

    DirectoryModule val = assertInstanceOf(DirectoryModule.class, opt.getOrThrow());
    assertDoesNotThrow(() -> {
      val.loadString(ResourcePath.create("module").addElement("path.xml"));
    });
  }
}