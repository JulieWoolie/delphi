package com.juliewoolie.delphidom.selector;

import static com.juliewoolie.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.selector.AnB;
import com.juliewoolie.chimera.selector.ClassNameSelector;
import com.juliewoolie.chimera.selector.IdSelector;
import com.juliewoolie.chimera.selector.IndexSelector;
import com.juliewoolie.chimera.selector.PseudoClass;
import com.juliewoolie.chimera.selector.PseudoClassSelector;
import com.juliewoolie.chimera.selector.PseudoFuncSelector;
import com.juliewoolie.chimera.selector.PseudoFunctions;
import com.juliewoolie.chimera.selector.Selector;
import com.juliewoolie.chimera.selector.SimpleIndexSelector;
import com.juliewoolie.chimera.selector.TagNameSelector;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.Element;
import org.junit.jupiter.api.Test;

class SelectorTest {

  @Test
  void testClassName() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    ClassNameSelector f = new ClassNameSelector("test");

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

    TagNameSelector function = new TagNameSelector("div");
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

    IdSelector function = new IdSelector("test-id");
    assertTrue(function.test(el));
    assertFalse(function.test(body));
  }

  @Test
  void testParsedId() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();
    DelphiElement el = doc.createElement("div");

    el.setId("test-id");
    body.appendChild(el);

    Selector function = Chimera.parseSelector("#test-id");
    assertTrue(function.test(el));
    assertFalse(function.test(body));
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
    assertTrue(function.test(div1));
    assertFalse(function.test(div2));
    assertFalse(function.test(div3));
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
    assertFalse(function.test(div1));
    assertFalse(function.test(div2));
    assertTrue(function.test(div3));
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
    assertTrue(func.test(div1));
    assertFalse(func.test(div2));
    assertFalse(func.test(div3));

    func = new PseudoFuncSelector<>(PseudoFunctions.NTH_CHILD, ix1);
    assertFalse(func.test(div1));
    assertTrue(func.test(div2));
    assertFalse(func.test(div3));

    func = new PseudoFuncSelector<>(PseudoFunctions.NTH_CHILD, ix2);
    assertFalse(func.test(div1));
    assertFalse(func.test(div2));
    assertTrue(func.test(div3));
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

    assertFalse(func.test(div1));
    assertTrue(func.test(div2));
    assertTrue(func.test(div3));
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
    assertFalse(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(div3));

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
    assertFalse(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(div3));

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
    assertFalse(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(div3));

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
    assertFalse(selector.test(div1));
    assertFalse(selector.test(div2));
    assertFalse(selector.test(div3));

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
    assertTrue(selector.test(div1));
    assertFalse(selector.test(div2));
    assertFalse(selector.test(div3));

    selector = Chimera.parseSelector(":nth-child(1 of div)");
    assertTrue(selector.test(div1));
    assertFalse(selector.test(div2));
    assertFalse(selector.test(div3));
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
    assertTrue(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(div3));

    selector = Chimera.parseSelector(":nth-child(2n)");
    assertFalse(selector.test(div1));
    assertTrue(selector.test(div2));
    assertFalse(selector.test(div3));

    selector = Chimera.parseSelector(":nth-last-child(2n+1)");
    assertTrue(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(div3));

    selector = Chimera.parseSelector(":nth-last-child(2n)");
    assertFalse(selector.test(div1));
    assertTrue(selector.test(div2));
    assertFalse(selector.test(div3));
  }

  @Test
  void simpleSelectorTest() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    Selector group = Chimera.parseSelector("div");
    assertTrue(group.test(div1));
    assertTrue(group.test(div2));
    assertTrue(group.test(div3));
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
    assertFalse(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(div3));

    selector = Chimera.parseSelector(":nth-last-child(1)");
    assertFalse(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(div3));
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
    assertTrue(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(span1));
    assertFalse(selector.test(div3));
    assertFalse(selector.test(span2));
    assertFalse(selector.test(span3));
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
    assertFalse(selector.test(div1));
    assertFalse(selector.test(div2));
    assertFalse(selector.test(span1));
    assertTrue(selector.test(div3));
    assertFalse(selector.test(span2));
    assertTrue(selector.test(span3));
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
    assertFalse(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(span1));
    assertFalse(selector.test(div3));
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
    assertTrue(selector.test(div1));
    assertFalse(selector.test(div2));
    assertTrue(selector.test(span1));
    assertFalse(selector.test(div3));
    assertFalse(selector.test(span2));
    assertFalse(selector.test(span3));
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
    assertTrue(selector.test(u));
    assertTrue(selector.test(underlined));
    assertFalse(selector.test(div));
  }
}