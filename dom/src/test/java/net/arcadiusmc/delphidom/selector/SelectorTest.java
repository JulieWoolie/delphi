package net.arcadiusmc.delphidom.selector;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
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

    PseudoFuncFunction<Integer> func = new PseudoFuncFunction<>(PseudoFunctions.NTH_CHILD, 0);
    assertTrue(func.test(null, div1));
    assertFalse(func.test(null, div2));
    assertFalse(func.test(null, div3));

    func = new PseudoFuncFunction<>(PseudoFunctions.NTH_CHILD, 1);
    assertFalse(func.test(null, div1));
    assertTrue(func.test(null, div2));
    assertFalse(func.test(null, div3));

    func = new PseudoFuncFunction<>(PseudoFunctions.NTH_CHILD, 2);
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
}