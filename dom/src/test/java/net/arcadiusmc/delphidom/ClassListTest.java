package net.arcadiusmc.delphidom;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ClassListTest {

  @Test
  void should_changeClassAttr_when_listAdded() {
    DelphiDocument doc = createDoc();
    DelphiElement el = doc.getBody();

    ClassList classList = (ClassList) el.getClassList();
    classList.add("foo");

    String after = el.getClassName();

    assertEquals("foo", after);
  }

  @Test
  void should_changeClassList_when_attrSet() {
    DelphiDocument doc = createDoc();
    DelphiElement el = doc.getBody();

    List<String> list = el.getClassList();

    el.setClassName("foo bar");

    assertEquals(2, list.size());
    assertTrue(list.contains("foo"));
    assertTrue(list.contains("bar"));
  }

  @Test
  void should_changeClassList_when_listCreatedAfterSet() {
    DelphiDocument doc = createDoc();
    DelphiElement el = doc.getBody();

    assertNull(el.classList, "Class list should not yet be instantiated");

    el.setClassName("foo bar");

    List<String> list = el.getClassList();

    assertEquals(2, list.size());
    assertTrue(list.contains("foo"));
    assertTrue(list.contains("bar"));
  }
}