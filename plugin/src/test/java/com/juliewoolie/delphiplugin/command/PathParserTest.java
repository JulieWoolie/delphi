package com.juliewoolie.delphiplugin.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphiplugin.command.PathParser.SuggestionMode;
import com.juliewoolie.delphiplugin.resource.PluginResources;
import org.junit.jupiter.api.Test;

class PathParserTest {

  static final File file;
  static final Path path;
  static final PluginResources PLUGIN_RESOURCES;

  static {
    file = new File(PathParserTest.class.getClassLoader().getResource("test-modules").getFile());
    path = file.toPath();
    PLUGIN_RESOURCES = new PluginResources(path);
  }

  @Test
  void testSuggestions() {
    assertSuggestionMode("mod", SuggestionMode.MODULE_NAMES);
    assertSuggestionMode("module", SuggestionMode.COLON);
    assertSuggestionMode("module:", SuggestionMode.FILE_PATHS);
    assertSuggestionMode("module:path.xml", SuggestionMode.FILE_PATHS);

    assertSuggestions("", List.of("module", "zipped"));
    assertSuggestions("mod", List.of("module"));
    assertSuggestions("module", List.of(":"));
    assertSuggestions("module:", List.of("subdir1/randomfile.json", "path.xml", "\"dir with space\"/file.xml"));
    assertSuggestions("module:subdir1", List.of("subdir1/randomfile.json"));
    assertSuggestions("module:subdir1/", List.of("randomfile.json"));
    assertSuggestions("module:path", List.of("path.xml"));
  }

  void assertSuggestions(String string, List<String> expected) {
    StringReader reader = new StringReader(string);
    PathParser<?> parser = new PathParser<>(PLUGIN_RESOURCES, reader);

    try {
      parser.parse();
    } catch (CommandSyntaxException exc) {
      // Ignored
    }

    SuggestionsBuilder builder = new SuggestionsBuilder(string, 0);
    Suggestions result = parser.getSuggestions(null, builder).resultNow();

    List<String> suggestions = new ArrayList<>(
        result.getList().stream().map(Suggestion::getText).toList()
    );

    System.out.printf("input='%s', returned: %s\n", string, suggestions);

    for (String s : expected) {
      int index = suggestions.indexOf(s);
      assertNotEquals(-1, index,
          "Expected suggestion '" + s + "' not found in returned list: " + suggestions
      );

      suggestions.remove(index);
    }

    assertTrue(suggestions.isEmpty(), "Too many returned suggestions: " + suggestions);
  }

  void assertSuggestionMode(String string, SuggestionMode mode) {
    StringReader reader = new StringReader(string);
    PathParser<?> parser = new PathParser<>(PLUGIN_RESOURCES, reader);

    try {
      parser.parse();
    } catch (CommandSyntaxException exc) {
      // Ignored
    }

    assertEquals(mode, parser.getSuggestionMode());
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
    assertEquals("p1/p2/p3/foobar.xml", assertValidFilePath("p1/p2/p3/foobar.xml").path());
    assertEquals("p1/foobar.xml", assertValidFilePath("p1/p2/../foobar.xml").path());
    assertEquals("foobar.xml", assertValidFilePath("p1/p2/./foobar.xml").path());
    assertEquals("\"with a space\"/foobar.xml", assertValidFilePath("\"with a space\"/foobar.xml").path());

    ResourcePath path = assertValidFilePath("./filename.json", parser -> {
      parser.setCwd(ResourcePath.create("module").addElement("foo").addElement("bar"));
    });

    assertEquals("foo/bar/filename.json", path.path());
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
    PathParser<?> parser = new PathParser<>(PLUGIN_RESOURCES, reader);

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