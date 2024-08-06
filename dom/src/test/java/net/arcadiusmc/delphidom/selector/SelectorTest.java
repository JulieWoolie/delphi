package net.arcadiusmc.delphidom.selector;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.Element;
import org.junit.jupiter.api.Test;

class SelectorTest {

  @Test
  void testClassName() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    ClassNameFunction f = new ClassNameFunction("test");

    assertFalse(f.test(null, body));
    body.setClassName("test");
    assertTrue(f.test(null, body));
  }

  @Test
  void testTagName() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();
    DelphiElement el = doc.createElement("div");

    body.appendChild(el);

    TagNameFunction function = new TagNameFunction("div");
    assertTrue(function.test(null, el));
    assertFalse(function.test(null, body));
  }

  @Test
  void testId() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();
    DelphiElement el = doc.createElement("div");

    el.setId("test-id");
    body.appendChild(el);

    IdFunction function = new IdFunction("test-id");
    assertTrue(function.test(null, el));
    assertFalse(function.test(null, body));
  }

  @Test
  void testFirstChild() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    PseudoClassFunction function = new PseudoClassFunction(PseudoClass.FIRST_CHILD);
    assertTrue(function.test(null, div1));
    assertFalse(function.test(null, div2));
    assertFalse(function.test(null, div3));
  }

  @Test
  void testLastChild() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    PseudoClassFunction function = new PseudoClassFunction(PseudoClass.LAST_CHILD);
    assertFalse(function.test(null, div1));
    assertFalse(function.test(null, div2));
    assertTrue(function.test(null, div3));
  }

  @Test
  void testNthChild() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    final var ix0 = new SimpleIndexSelector(new AnB(0, 1));
    final var ix1 = new SimpleIndexSelector(new AnB(0, 2));
    final var ix2 = new SimpleIndexSelector(new AnB(0, 3));

    PseudoFuncFunction<IndexSelector> func = new PseudoFuncFunction<>(PseudoFunctions.NTH_CHILD, ix0);
    assertTrue(func.test(null, div1));
    assertFalse(func.test(null, div2));
    assertFalse(func.test(null, div3));

    func = new PseudoFuncFunction<>(PseudoFunctions.NTH_CHILD, ix1);
    assertFalse(func.test(null, div1));
    assertTrue(func.test(null, div2));
    assertFalse(func.test(null, div3));

    func = new PseudoFuncFunction<>(PseudoFunctions.NTH_CHILD, ix2);
    assertFalse(func.test(null, div1));
    assertFalse(func.test(null, div2));
    assertTrue(func.test(null, div3));
  }

  @Test
  void testNotPseudo() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("span");
    DelphiElement div3 = doc.createElement("p");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    PseudoFuncFunction<SelectorGroup> func = new PseudoFuncFunction<>(
        PseudoFunctions.NOT,
        SelectorGroup.parse("div")
    );

    assertFalse(func.test(null, div1));
    assertTrue(func.test(null, div2));
    assertTrue(func.test(null, div3));
  }

  @Test
  void testSiblingCombinator() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div1");
    DelphiElement div2 = doc.createElement("div2");
    DelphiElement div3 = doc.createElement("div3");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    Selector selector = Selector.parse("div2 ~ div3");
    assertFalse(selector.test(null, div1));
    assertFalse(selector.test(null, div2));
    assertTrue(selector.test(null, div3));

    Element el = body.querySelector("div2 ~ div3");
    assertEquals(div3, el);
  }

  @Test
  void testSiblingCombinator_indirect() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div1");
    DelphiElement div2 = doc.createElement("div2");
    DelphiElement div3 = doc.createElement("div3");
    DelphiElement body = doc.getBody();

    body.appendChild(div2);
    body.appendChild(div1);
    body.appendChild(div3);

    Selector selector = Selector.parse("div2 ~ div3");
    assertFalse(selector.test(null, div1));
    assertFalse(selector.test(null, div2));
    assertTrue(selector.test(null, div3));

    Element el = body.querySelector("div2 ~ div3");
    assertEquals(div3, el);
  }

  @Test
  void testDirectSiblingCombinator() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div1");
    DelphiElement div2 = doc.createElement("div2");
    DelphiElement div3 = doc.createElement("div3");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    Selector selector = Selector.parse("div2 + div3");
    assertFalse(selector.test(null, div1));
    assertFalse(selector.test(null, div2));
    assertTrue(selector.test(null, div3));

    Element el = body.querySelector("div2 ~ div3");
    assertEquals(div3, el);
  }

  @Test
  void testDirectSiblingCombinator_indirect() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div1");
    DelphiElement div2 = doc.createElement("div2");
    DelphiElement div3 = doc.createElement("div3");
    DelphiElement body = doc.getBody();

    body.appendChild(div2);
    body.appendChild(div1);
    body.appendChild(div3);

    Selector selector = Selector.parse("div2 + div3");
    assertFalse(selector.test(null, div1));
    assertFalse(selector.test(null, div2));
    assertFalse(selector.test(null, div3));

    Element el = body.querySelector("div2 + div3");
    assertNull(el);
  }

  @Test
  void testNthChild_parsed() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    Selector selector = Selector.parse(":nth-child(1)");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertFalse(selector.test(body, div3));

    selector = Selector.parse(":nth-child(1 of div)");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertFalse(selector.test(body, div3));
  }

  @Test
  void testNthChild_anb() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    Selector selector = Selector.parse(":nth-child(2n+1)");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, div3));

    selector = Selector.parse(":nth-child(2n)");
    assertFalse(selector.test(body, div1));
    assertTrue(selector.test(body, div2));
    assertFalse(selector.test(body, div3));

    selector = Selector.parse(":nth-last-child(2n+1)");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, div3));

    selector = Selector.parse(":nth-last-child(2n)");
    assertFalse(selector.test(body, div1));
    assertTrue(selector.test(body, div2));
    assertFalse(selector.test(body, div3));
  }

  @Test
  void simpleSelectorTest() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    SelectorGroup group = SelectorGroup.parse("div");
    assertTrue(group.test(body, div1));
    assertTrue(group.test(body, div2));
    assertTrue(group.test(body, div3));
  }

  @Test
  void testNthLastChild_parsed() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    Selector selector = Selector.parse(":nth-last-child(1 of div)");
    assertFalse(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, div3));

    selector = Selector.parse(":nth-last-child(1)");
    assertFalse(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, div3));
  }

  @Test
  void testNthOfType() {
    DelphiDocument doc = createDoc();

    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement span1 = doc.createElement("span");
    DelphiElement span2 = doc.createElement("span");
    DelphiElement span3 = doc.createElement("span");

    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(span1);
    body.appendChild(div3);
    body.appendChild(span2);
    body.appendChild(span3);

    Selector selector = Selector.parse(":nth-of-type(1)");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, span1));
    assertFalse(selector.test(body, div3));
    assertFalse(selector.test(body, span2));
    assertFalse(selector.test(body, span3));
  }

  @Test
  void testNthLastOfType() {
    DelphiDocument doc = createDoc();

    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement span1 = doc.createElement("span");
    DelphiElement span2 = doc.createElement("span");
    DelphiElement span3 = doc.createElement("span");

    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(span1);
    body.appendChild(div3);
    body.appendChild(span2);
    body.appendChild(span3);

    Selector selector = Selector.parse(":nth-last-of-type(1)");
    assertFalse(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertFalse(selector.test(body, span1));
    assertTrue(selector.test(body, div3));
    assertFalse(selector.test(body, span2));
    assertTrue(selector.test(body, span3));
  }

  @Test
  void testOnlyOfType() {
    DelphiDocument doc = createDoc();

    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement span1 = doc.createElement("span");

    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(span1);
    body.appendChild(div3);

    Selector selector = Selector.parse(":only-of-type");
    assertFalse(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, span1));
    assertFalse(selector.test(body, div3));
  }
}