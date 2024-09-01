package net.arcadiusmc.chimera.parse;

import static net.arcadiusmc.chimera.parse.Chimera.parseSelector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.arcadiusmc.chimera.selector.AttributeOperation;
import net.arcadiusmc.chimera.selector.AttributeSelector;
import net.arcadiusmc.chimera.selector.ClassNameSelector;
import net.arcadiusmc.chimera.selector.IdSelector;
import net.arcadiusmc.chimera.selector.IndexSelector;
import net.arcadiusmc.chimera.selector.PseudoFuncSelector;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SelectorList;
import net.arcadiusmc.chimera.selector.SelectorList.ListStyle;
import net.arcadiusmc.chimera.selector.SimpleIndexSelector;
import net.arcadiusmc.chimera.selector.TagNameSelector;
import net.arcadiusmc.dom.ParserException;
import org.junit.jupiter.api.Test;

public class SelectorParseTest {

  @Test
  void testClassName() {
    Selector selector = parseSelector(".className");
    ClassNameSelector cname = assertInstanceOf(ClassNameSelector.class, selector);
    assertEquals("className", cname.className());
    assertThrows(ParserException.class, () -> parseSelector("."));
  }

  @Test
  void testIdSelector() {
    Selector selector = parseSelector("#elementId");
    IdSelector cname = assertInstanceOf(IdSelector.class, selector);
    assertEquals("elementId", cname.elementId());
    assertThrows(ParserException.class, () -> parseSelector("#"));
  }

  @Test
  void testTagName() {
    Selector selector = parseSelector("tagName");
    TagNameSelector tname = assertInstanceOf(TagNameSelector.class, selector);
    assertEquals("tagName", tname.tagName());
  }

  @Test
  void testAttribute() {
    Selector selector = parseSelector("[attr]");
    AttributeSelector attr = assertInstanceOf(AttributeSelector.class, selector);

    assertEquals("attr", attr.attributeKey());
    assertEquals(AttributeOperation.HAS, attr.op());

    assertNull(attr.value());

    assertThrows(ParserException.class, () -> parseSelector("[attr"));
    assertThrows(ParserException.class, () -> parseSelector("[attr]]"));

    selector = parseSelector("[attr=\"value\"]");
    attr = assertInstanceOf(AttributeSelector.class, selector);

    assertEquals("value", attr.value());
    assertEquals(AttributeOperation.EQUALS, attr.op());

    testAttributeOp("[attr=\"value\"]", AttributeOperation.EQUALS, "value");
    testAttributeOp("[attr~=\"value\"]", AttributeOperation.CONTAINS_WORD, "value");
    testAttributeOp("[attr|=\"value\"]", AttributeOperation.DASH_PREFIXED, "value");
    testAttributeOp("[attr^=\"value\"]", AttributeOperation.STARTS_WITH, "value");
    testAttributeOp("[attr*=\"value\"]", AttributeOperation.CONTAINS_SUBSTRING, "value");
    testAttributeOp("[attr$=\"value\"]", AttributeOperation.ENDS_WITH, "value");
    testAttributeOp("[attr]", AttributeOperation.HAS, null);
  }

  @Test
  void testCombined() {
    String in = "tag.className";
    Selector selector = parseSelector(in);
    SelectorList list = assertInstanceOf(SelectorList.class, selector);

    assertEquals(ListStyle.COMPACT, list.getStyle());
    assertEquals(2, list.getSize());

    TagNameSelector tagName = assertInstanceOf(TagNameSelector.class, list.get(0));
    ClassNameSelector cname = assertInstanceOf(ClassNameSelector.class, list.get(1));

    assertEquals("tag", tagName.tagName());
    assertEquals("className", cname.className());

    assertEquals(in, selector.toString());
  }

  @Test
  void testCommaList() {
    String in = "tag, #elementId";
    Selector selector = parseSelector(in);

    SelectorList list = assertInstanceOf(SelectorList.class, selector);

    assertEquals(ListStyle.COMMA_LIST, list.getStyle());
    assertEquals(2, list.getSize());

    TagNameSelector tagName = assertInstanceOf(TagNameSelector.class, list.get(0));
    IdSelector cname = assertInstanceOf(IdSelector.class, list.get(1));

    assertEquals("tag", tagName.tagName());
    assertEquals("elementId", cname.elementId());

    assertEquals(in, selector.toString());
  }
  
  @Test
  void testAnB() {
    Selector selector = parseSelector(":nth-child(3)");
    PseudoFuncSelector<IndexSelector> idxSel = assertInstanceOf(PseudoFuncSelector.class, selector);
    SimpleIndexSelector idxSelector = assertInstanceOf(SimpleIndexSelector.class, idxSel.argument());
    assertEquals(0, idxSelector.anb().a());
    assertEquals(3, idxSelector.anb().b());

    idxSel = assertInstanceOf(PseudoFuncSelector.class, parseSelector(":nth-child(even)"));
    idxSelector = assertInstanceOf(SimpleIndexSelector.class, idxSel.argument());
    assertEquals(2, idxSelector.anb().a());
    assertEquals(0, idxSelector.anb().b());

    idxSel = assertInstanceOf(PseudoFuncSelector.class, parseSelector(":nth-child(odd)"));
    idxSelector = assertInstanceOf(SimpleIndexSelector.class, idxSel.argument());
    assertEquals(2, idxSelector.anb().a());
    assertEquals(1, idxSelector.anb().b());

    idxSel = assertInstanceOf(PseudoFuncSelector.class, parseSelector(":nth-child(3n+4)"));
    idxSelector = assertInstanceOf(SimpleIndexSelector.class, idxSel.argument());
    assertEquals(3, idxSelector.anb().a());
    assertEquals(4, idxSelector.anb().b());
    assertEquals("3n+4", idxSelector.anb().toString());

    idxSel = assertInstanceOf(PseudoFuncSelector.class, parseSelector(":nth-child(-n+4)"));
    idxSelector = assertInstanceOf(SimpleIndexSelector.class, idxSel.argument());
    assertEquals(-1, idxSelector.anb().a());
    assertEquals(4, idxSelector.anb().b());
    assertEquals("-n+4", idxSelector.anb().toString());

    idxSel = assertInstanceOf(PseudoFuncSelector.class, parseSelector(":nth-child(-n)"));
    idxSelector = assertInstanceOf(SimpleIndexSelector.class, idxSel.argument());
    assertEquals(-1, idxSelector.anb().a());
    assertEquals(0, idxSelector.anb().b());
    assertEquals("-n", idxSelector.anb().toString());
  }

  static void testAttributeOp(String str, AttributeOperation op, String value) {
    Selector selector = parseSelector(str);
    AttributeSelector attr = assertInstanceOf(AttributeSelector.class, selector);

    assertEquals(op, attr.op());

    if (value != null) {
      assertNotNull(attr.value());
      assertEquals(value, attr.value());
    }
  }
}
