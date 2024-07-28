package net.arcadiusmc.delphiplugin.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphiplugin.resource.Modules;
import org.junit.jupiter.api.Test;

class PathParserTest {

  static final File file;
  static final Path path;
  static final Modules modules;

  static {
    file = new File(PathParserTest.class.getClassLoader().getResource("test-modules").getFile());
    path = file.toPath();
    modules = new Modules(path);
  }

  @Test
  void testCanParseFull() {
    assertDoesNotThrow(() -> assertValidPath("module:index.xml", null, PathParser::parse));
    assertDoesNotThrow(() -> assertValidPath("module:path1/path2", null, PathParser::parse));
    assertDoesNotThrow(() -> assertValidPath("module:\"path spaced\"/path2", null, PathParser::parse));
    assertDoesNotThrow(() -> assertValidPath("module:path1/path2?foo=bar", null, PathParser::parse));
    assertDoesNotThrow(() -> assertValidPath("module:path1/path2?foo", null, PathParser::parse));
    assertDoesNotThrow(() -> assertValidPath("module:?foo", null, PathParser::parse));
    assertDoesNotThrow(() -> assertValidPath("module:path1/path2?&foo", null, PathParser::parse));
    assertDoesNotThrow(() -> assertValidPath("module:path1/path2?&foo&", null, PathParser::parse));
    assertDoesNotThrow(() -> assertValidPath("module:path1/path2?&foo=bar&", null, PathParser::parse));
  }

  @Test
  void testFilePaths() {
    assertEquals(assertValidFilePath("p1/p2/p3/foobar.xml").path(), "p1/p2/p3/foobar.xml");
    assertEquals(assertValidFilePath("p1/p2/../foobar.xml").path(), "p1/foobar.xml");
    assertEquals(assertValidFilePath("p1/p2/./foobar.xml").path(), "foobar.xml");
    assertEquals(assertValidFilePath("\"with a space\"/foobar.xml").path(), "\"with a space\"/foobar.xml");

    ResourcePath path = assertValidFilePath("./filename.json", parser -> {
      parser.setCwd(ResourcePath.create("module").addElement("foo").addElement("bar"));
    });

    assertEquals(path.path(), "foo/bar/filename.json");
  }

  @Test
  void testInvalidPaths() {
    assertThrows(RuntimeException.class, () -> {
      assertValidFilePath("\"with/slash\"/file.xml");
    });

    assertThrows(RuntimeException.class, () -> {
      assertValidFilePath("\"with slash\"file.xml");
    });

    assertThrows(RuntimeException.class, () -> {
      assertValidFilePath("slash\"/file.xml");
    });
  }

  @Test
  void testQueries() {
    ResourcePath path = assertValidPath("?foo=bar", null, PathParser::parseQuery);
    assertEquals("bar", path.getQuery("foo"));

    path = assertValidPath("?foo", null, PathParser::parseQuery);
    assertEquals("", path.getQuery("foo"));

    path = assertValidPath("?foo=", null, PathParser::parseQuery);
    assertEquals("", path.getQuery("foo"));

    path = assertValidPath("?foo=bar&foobar=false", null, PathParser::parseQuery);
    assertEquals("bar", path.getQuery("foo"));
    assertEquals("false", path.getQuery("foobar"));

    path = assertValidPath("?foo&foobar=false", null, PathParser::parseQuery);
    assertEquals("", path.getQuery("foo"));
    assertEquals("false", path.getQuery("foobar"));

    path = assertValidPath("?foo=&foobar=false", null, PathParser::parseQuery);
    assertEquals("", path.getQuery("foo"));
    assertEquals("false", path.getQuery("foobar"));

    assertDoesNotThrow(() -> assertValidPath("?foo=false&", null, PathParser::parseQuery));
    assertDoesNotThrow(() -> assertValidPath("?&foo=false", null, PathParser::parseQuery));
    assertDoesNotThrow(() -> assertValidPath("?&foo=false&", null, PathParser::parseQuery));

    path = assertValidPath("?&foo", null, PathParser::parseQuery);
    assertEquals("", path.getQuery("foo"));
  }

  @Test
  void testInvalidQueries() {
    assertThrows(RuntimeException.class, () -> {
      assertValidPath("?foo/", null, PathParser::parseQuery);
    });
    assertThrows(RuntimeException.class, () -> {
      assertValidPath("?&bar:false", null, PathParser::parseQuery);
    });
  }

  ResourcePath assertValidFilePath(String path) {
    return assertValidFilePath(path, null);
  }

  ResourcePath assertValidFilePath(String path, Consumer<PathParser<?>> setup) {
    return assertValidPath(path, setup, PathParser::parsePath);
  }

  ResourcePath assertValidPath(String path, Consumer<PathParser<?>> setup, ReaderExec exec) {
    StringReader reader = new StringReader(path);
    PathParser<?> parser = new PathParser<>(modules, reader);

    parser.setPath(ResourcePath.create("module"));

    if (setup != null) {
      setup.accept(parser);
    }

    try {
      exec.execute(parser);
    } catch (CommandSyntaxException exc) {
      throw new RuntimeException(exc);
    }
    return parser.getPath();
  }

  interface ReaderExec {
    void execute(PathParser<?> parser) throws CommandSyntaxException;
  }
}