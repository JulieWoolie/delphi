package com.juliewoolie.delphi.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PathTest {

  @Test
  void should_parse_when_onlyModuleNameSpecified() {
    ResourcePath path = ResourcePath.create("module");

    assertEquals("module", path.getModuleName());
    assertEquals(0, path.elementCount());
    assertEquals(0, path.getQueryKeys().size());
  }

  @Test
  void should_parse_when_moduleAndFilePathGiven() {
    ResourcePath path = ResourcePath.create("module:file");

    assertEquals("module", path.getModuleName());
    assertEquals(1, path.elementCount());
    assertEquals("file", path.getElement(0));
  }

  @Test
  void should_parse_when_moduleAndMultipleFilePathElementsGiven() {
    ResourcePath path = ResourcePath.create("module:dir/file.txt");

    assertEquals("module", path.getModuleName());
    assertEquals(2, path.elementCount());
    assertEquals("dir", path.getElement(0));
    assertEquals("file.txt", path.getElement(1));
  }

  @Test
  void should_parse_when_quotedElementsSpecified() {
    ResourcePath path = ResourcePath.create("module:\"dir with spaces\"/file.txt");

    assertEquals("module", path.getModuleName());
    assertEquals(2, path.elementCount());
    assertEquals("dir with spaces", path.getElement(0));
    assertEquals("file.txt", path.getElement(1));
  }

  @Test
  void should_parse_when_moduleAndFileAndQuerySpecified() {
    ResourcePath path = ResourcePath.create("module:file?query=foo");

    assertEquals("module", path.getModuleName());

    assertEquals(1, path.elementCount());
    assertEquals("file", path.getElement(0));

    assertEquals("foo", path.getQuery("query"));
  }

  @Test
  void should_parse_when_moduleAndFileAndMultipleQueriesSpecified() {
    ResourcePath path = ResourcePath.create("module:file?query=foo&bar=foobar");

    assertEquals("module", path.getModuleName());

    assertEquals(1, path.elementCount());
    assertEquals("file", path.getElement(0));

    assertEquals("foo", path.getQuery("query"));
    assertEquals("foobar", path.getQuery("bar"));
  }

  @Test
  void should_parse_when_onlyModuleQueriesSpecifiedButWithColon() {
    ResourcePath path = ResourcePath.create("module:?query=foo");

    assertEquals("module", path.getModuleName());
    assertEquals(0, path.elementCount());
    assertEquals("foo", path.getQuery("query"));
  }

  @Test
  void should_parse_when_onlyModuleQueriesSpecified() {
    ResourcePath path = ResourcePath.create("module?query=foo");
    assertEquals("module", path.getModuleName());
    assertEquals("foo", path.getQuery("query"));
  }

  @Test
  void should_parse_when_onlyModuleAndQueryWithNoValue() {
    ResourcePath path = ResourcePath.create("module?query");
    assertEquals("module", path.getModuleName());
    assertEquals("", path.getQuery("query"));
  }

  @Test
  void should_parse_when_onlyModuleAndQueriesWithNoValues() {
    ResourcePath path = ResourcePath.create("module?query&foo&foobar");
    assertEquals("module", path.getModuleName());
    assertEquals("", path.getQuery("query"));
    assertEquals("", path.getQuery("foo"));
    assertEquals("", path.getQuery("foobar"));
  }

  @Test
  void should_not_parse_when_invalidModuleNameSpecified() {
    assertThrows(IllegalArgumentException.class, () -> {
      ResourcePath.create("invalid module name");
    });
  }

  @Test
  void should_not_parse_when_invalidFilePathSpecified() {
    assertThrows(IllegalArgumentException.class, () -> {
      ResourcePath.create("module:invalid\"file");
    });
  }

  @Test
  void should_not_parse_when_invalidQuerySpecified() {
    assertThrows(IllegalArgumentException.class, () -> {
      ResourcePath.create("module?bad/query=false");
    });
  }

  @Test
  void should_parse_when_singleDotPathUsed() {
    ResourcePath path = ResourcePath.create("module:./");

    assertEquals("module", path.getModuleName());
    assertEquals(1, path.elementCount());
    assertEquals(".", path.getElement(0));
  }

  @Test
  void should_parse_when_doubleDotPathUsed() {
    ResourcePath path = ResourcePath.create("module:a/../b");

    assertEquals("module", path.getModuleName());

    assertEquals(3, path.elementCount());

    assertEquals("a", path.getElement(0));
    assertEquals("..", path.getElement(1));
    assertEquals("b", path.getElement(2));
  }

  @Test
  void should_goBackOneDirectory_when_cwdCanGoBack() {
    ResourcePath path = ResourcePath.create("module:../foo.txt");
    ResourcePath cwd = ResourcePath.create("module:dir1/dir2");

    ResourcePath norm = path.normalize(cwd);

    assertEquals(2, norm.elementCount());
    assertEquals("dir1", norm.getElement(0));
    assertEquals("foo.txt", norm.getElement(1));
  }
}