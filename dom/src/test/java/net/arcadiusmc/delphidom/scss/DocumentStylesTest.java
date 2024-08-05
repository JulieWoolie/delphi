package net.arcadiusmc.delphidom.scss;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.style.Primitive;
import net.arcadiusmc.dom.style.Primitive.Unit;
import net.arcadiusmc.dom.style.Stylesheet;
import org.junit.jupiter.api.Test;

class DocumentStylesTest {

  @Test
  void testInline() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    body.setAttribute(Attributes.STYLE, "padding-left: 4px;");
    ReadonlyMap map = body.getCurrentStyle();
    InlineStyle inline = body.getInlineStyle();

    Primitive prim = Primitive.create(4, Unit.PX);
    assertEquals(prim, map.getPaddingLeft());
    assertEquals(prim, inline.getPaddingLeft());
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
    Primitive v = Primitive.create(4, Unit.PX);

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

    ReadonlyMap map = body.getCurrentStyle();
    InlineStyle inline = body.getInlineStyle();

    Primitive v = Primitive.create(4, Unit.PX);

    Sheet sheet = doc.createStylesheet()
        .addRule(".test", prop -> prop.setPaddingLeft(v))
        .build();

    assertEquals(v, sheet.getRule(0).getProperties().getPaddingLeft());

    Rule r = sheet.getRule(0);
    assertEquals(v, r.getProperties().getPaddingLeft());
    assertTrue(r.getSelectorObj().test(null, body));

    assertEquals(v, map.getPaddingLeft());
    assertEquals(Properties.PADDING_LEFT.getDefaultValue(), inline.getPaddingLeft());
  }
}