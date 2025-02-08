package net.arcadiusmc.delphidom.selector;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.selector.AnB;
import net.arcadiusmc.chimera.selector.ClassNameSelector;
import net.arcadiusmc.chimera.selector.IdSelector;
import net.arcadiusmc.chimera.selector.IndexSelector;
import net.arcadiusmc.chimera.selector.PseudoClass;
import net.arcadiusmc.chimera.selector.PseudoClassSelector;
import net.arcadiusmc.chimera.selector.PseudoFuncSelector;
import net.arcadiusmc.chimera.selector.PseudoFunctions;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.SimpleIndexSelector;
import net.arcadiusmc.chimera.selector.TagNameSelector;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.Element;
import org.junit.jupiter.api.Test;

class SelectorTest {

  @Test
  void testClassName() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    ClassNameSelector f = new ClassNameSelector("test");

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

    TagNameSelector function = new TagNameSelector("div");
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

    IdSelector function = new IdSelector("test-id");
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

    PseudoClassSelector function = new PseudoClassSelector(PseudoClass.FIRST_CHILD);
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

    PseudoClassSelector function = new PseudoClassSelector(PseudoClass.LAST_CHILD);
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

    PseudoFuncSelector<IndexSelector> func = new PseudoFuncSelector<>(PseudoFunctions.NTH_CHILD, ix0);
    assertTrue(func.test(null, div1));
    assertFalse(func.test(null, div2));
    assertFalse(func.test(null, div3));

    func = new PseudoFuncSelector<>(PseudoFunctions.NTH_CHILD, ix1);
    assertFalse(func.test(null, div1));
    assertTrue(func.test(null, div2));
    assertFalse(func.test(null, div3));

    func = new PseudoFuncSelector<>(PseudoFunctions.NTH_CHILD, ix2);
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

    PseudoFuncSelector<Selector> func = new PseudoFuncSelector<>(
        PseudoFunctions.NOT,
        Chimera.parseSelector("div")
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

    Selector selector = Chimera.parseSelector("div2 ~ div3");
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

    Selector selector = Chimera.parseSelector("div2 ~ div3");
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

    Selector selector = Chimera.parseSelector("div2 + div3");
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

    Selector selector = Chimera.parseSelector("div2 + div3");
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

    Selector selector = Chimera.parseSelector(":nth-child(1)");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertFalse(selector.test(body, div3));

    selector = Chimera.parseSelector(":nth-child(1 of div)");
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

    Selector selector = Chimera.parseSelector(":nth-child(2n+1)");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, div3));

    selector = Chimera.parseSelector(":nth-child(2n)");
    assertFalse(selector.test(body, div1));
    assertTrue(selector.test(body, div2));
    assertFalse(selector.test(body, div3));

    selector = Chimera.parseSelector(":nth-last-child(2n+1)");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, div3));

    selector = Chimera.parseSelector(":nth-last-child(2n)");
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

    Selector group = Chimera.parseSelector("div");
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

    Selector selector = Chimera.parseSelector(":nth-last-child(1 of div)");
    assertFalse(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, div3));

    selector = Chimera.parseSelector(":nth-last-child(1)");
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

    Selector selector = Chimera.parseSelector(":nth-of-type(1)");
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

    Selector selector = Chimera.parseSelector(":nth-last-of-type(1)");
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

    Selector selector = Chimera.parseSelector(":only-of-type");
    assertFalse(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, span1));
    assertFalse(selector.test(body, div3));
  }

  @Test
  void testFirstOfType() {
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

    Selector selector = Chimera.parseSelector(":first-of-type");
    assertTrue(selector.test(body, div1));
    assertFalse(selector.test(body, div2));
    assertTrue(selector.test(body, span1));
    assertFalse(selector.test(body, div3));
    assertFalse(selector.test(body, span2));
    assertFalse(selector.test(body, span3));
  }

  @Test
  void testListOfTagNames() {
    DelphiDocument doc = createDoc();
    DelphiElement u = doc.createElement("u");
    DelphiElement underlined = doc.createElement("underlined");
    DelphiElement div = doc.createElement("div");

    DelphiElement body = doc.getBody();
    body.appendChild(u);
    body.appendChild(underlined);
    body.appendChild(div);

    Selector selector = Chimera.parseSelector("u, underlined");
    assertTrue(selector.test(null, u));
    assertTrue(selector.test(null, underlined));
    assertFalse(selector.test(null, div));
  }
}