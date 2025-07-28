package com.juliewoolie.delphidom.scss;

import static com.juliewoolie.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.Rule;
import com.juliewoolie.chimera.system.StyleNode;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.style.StyleProperties;
import com.juliewoolie.dom.style.StylePropertiesReadonly;
import com.juliewoolie.dom.style.Stylesheet;
import org.junit.jupiter.api.Test;

class DocumentStylesTest {

  @Test
  void testInline() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    body.setAttribute(Attributes.STYLE, "padding-left: 4px;");
    StylePropertiesReadonly map = body.getCurrentStyle();
    StyleProperties inline = body.getInlineStyle();

    assertEquals("4px", map.getPaddingLeft());
    assertEquals("4px", inline.getPaddingLeft());
  }

  @Test
  void testInlineSyntaxError() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    assertDoesNotThrow(() -> {
      body.setAttribute(Attributes.STYLE, "paddi: 4px;");
    });

    assertDoesNotThrow(() -> {
      body.setAttribute(Attributes.STYLE, "padding: $non-existent-variable;");
    });
  }

  @Test
  void testSheetBuilder() {
    DelphiDocument doc = createDoc();
    String v = "4px";

    Stylesheet sheet = doc.createStylesheet()
        .addRule(".test", prop -> prop.setPaddingLeft(v))
        .build();

    assertEquals(sheet.getLength(), 1);
    assertEquals(v, sheet.getRule(0).getProperties().getPaddingLeft());
    assertEquals(1, doc.getStylesheets().size());

    Stylesheet gotten = doc.getStylesheets().get(0);
    assertEquals(sheet, gotten);
    assertEquals(1, gotten.getLength());
    assertEquals(v, gotten.getRule(0).getProperties().getPaddingLeft());
  }

  @Test
  void testStylesheet() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();
    body.setClassName("test");

    assertEquals("test", body.getAttribute(Attributes.CLASS));

    StylePropertiesReadonly map = body.getCurrentStyle();
    StyleProperties inline = body.getInlineStyle();

    String v = "4px";

    ChimeraStylesheet sheet = doc.createStylesheet()
        .addRule(".test", prop -> prop.setPaddingLeft(v))
        .build();

    assertEquals(v, sheet.getRule(0).getProperties().getPaddingLeft());

    Rule r = sheet.getRule(0);
    assertEquals(v, r.getProperties().getPaddingLeft());
    assertTrue(r.getSelectorObject().test(body));

    StyleNode styleNode = doc.getStyles().getStyleNode(body);

    assertTrue(body.matches(".test"), "Body doesn't match .test selector");

    assertEquals(v, map.getPaddingLeft());
    assertNull(inline.getPaddingLeft());
  }
}