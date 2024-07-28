package net.arcadiusmc.delphi.dom.selector;

import static net.arcadiusmc.delphi.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.delphi.dom.DelphiElement;
import org.junit.jupiter.api.Test;

class SelectorTest {

  @Test
  void testClassName() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    ClassNameFunction f = new ClassNameFunction("test");

    assertFalse(f.test(body));
    body.setClassName("test");
    assertTrue(f.test(body));
  }

  @Test
  void testTagName() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();
    DelphiElement el = doc.createElement("div");

    body.appendChild(el);

    TagNameFunction function = new TagNameFunction("div");
    assertTrue(function.test(el));
    assertFalse(function.test(body));
  }

  @Test
  void testId() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();
    DelphiElement el = doc.createElement("div");

    el.setId("test-id");
    body.appendChild(el);

    IdFunction function = new IdFunction("test-id");
    assertTrue(function.test(el));
    assertFalse(function.test(body));
  }
}