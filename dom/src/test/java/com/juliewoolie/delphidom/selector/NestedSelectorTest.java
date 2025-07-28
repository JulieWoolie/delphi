package com.juliewoolie.delphidom.selector;

import static com.juliewoolie.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.juliewoolie.chimera.ChimeraStylesheet;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.parse.ChimeraException;
import com.juliewoolie.chimera.parse.ChimeraParser;
import com.juliewoolie.chimera.parse.CompilerErrors;
import com.juliewoolie.chimera.parse.ast.SheetStatement;
import com.juliewoolie.chimera.selector.Selector;
import com.juliewoolie.chimera.selector.Spec;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.dom.NodeFlag;
import org.junit.jupiter.api.Test;

public class NestedSelectorTest {

  @Test
  void testPseudo() {
    Selector[] selectors = parseSelectors("button { &:hover {} }");
    assertEquals(2, selectors.length);

    Selector regular = selectors[1];
    Selector hoverTest = selectors[0];

    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    DelphiElement el = doc.createElement("button");
    body.appendChild(el);

    assertTrue(regular.test(el));
    assertFalse(hoverTest.test(el));

    el.addFlag(NodeFlag.HOVERED);

    assertTrue(regular.test(el));
    assertTrue(hoverTest.test(el));

    Spec regularSpec = new Spec();
    Spec hoverSpec = new Spec();

    regular.appendSpec(regularSpec);
    hoverTest.appendSpec(hoverSpec);

    assertTrue(regularSpec.compareTo(hoverSpec) < 0);
  }

  @Test
  void testSelectorList() {
    Selector[] selectors = parseSelectors("u, ul, underlined { div {} }");
    assertEquals(2, selectors.length);

    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    DelphiElement u = doc.createElement("u");
    DelphiElement ul = doc.createElement("ul");
    DelphiElement underlined = doc.createElement("underlined");

    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement div4 = doc.createElement("div");
    DelphiElement span = doc.createElement("span");

    u.appendChild(div1);
    u.appendChild(span);

    ul.appendChild(div2);
    ul.appendChild(div3);

    underlined.appendChild(div4);

    body.appendChild(u);
    body.appendChild(ul);
    body.appendChild(underlined);

    Selector first = selectors[0]; // :is(u, ul, underlined) div
    Selector second = selectors[1]; // u, ul, underlined

    assertTrue(second.test(u));
    assertTrue(second.test(ul));
    assertTrue(second.test(underlined));

    assertFalse(second.test(div1));
    assertFalse(second.test(div2));
    assertFalse(second.test(div3));
    assertFalse(second.test(div4));

    assertTrue(first.test(div1));
    assertTrue(first.test(div2));
    assertTrue(first.test(div3));
    assertTrue(first.test(div4));

    assertFalse(first.test(span));
  }

  @Test
  void testDoubleNesting() {
    Selector[] selectors = parseSelectors(
        """
        .rule1 {
          &[attr] {
            &:hover {
            }
          }
        }"""
    );

    // [0] = .rule1[attr]:hover
    // [1] = .rule1[attr]
    // [2] = .rule1
    assertEquals(3, selectors.length);

    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    DelphiElement d1 = (DelphiElement) body.appendElement("div");
    d1.setClassName("rule1");

    DelphiElement d2 = (DelphiElement) body.appendElement("div");
    d2.setClassName("rule1");
    d2.setAttribute("attr", "yay");

    DelphiElement d3 = (DelphiElement) body.appendElement("div");
    d3.setClassName("rule1");
    d3.setAttribute("attr", "yay");
    d3.addFlag(NodeFlag.HOVERED);

    DelphiElement d4 = (DelphiElement) body.appendElement("div");
    d4.setClassName("rule1");
    d4.addFlag(NodeFlag.HOVERED);

    for (Selector selector : selectors) {
      System.out.println(selector);
    }

    assertFalse(selectors[0].test(d1));
    assertFalse(selectors[0].test(d2));
    assertTrue(selectors[0].test(d3));
    assertFalse(selectors[0].test(d4));

    assertFalse(selectors[1].test(d1));
    assertTrue(selectors[1].test(d2));
    assertTrue(selectors[1].test(d3));
    assertFalse(selectors[1].test(d4));

    assertTrue(selectors[2].test(d1));
    assertTrue(selectors[2].test(d2));
    assertTrue(selectors[2].test(d3));
    assertTrue(selectors[2].test(d4));
  }

  static Selector[] parseSelectors(String str) {
    ChimeraParser parser = new ChimeraParser(str);

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName("test-src.scss");
    errors.setListener(error -> {
      throw new ChimeraException(error);
    });

    SheetStatement sheet = parser.stylesheet();
    ChimeraStylesheet chimeraStylesheet = Chimera.compileSheet(sheet, parser.createContext());

    Selector[] selectors = new Selector[chimeraStylesheet.getLength()];
    for (int i = 0; i < selectors.length; i++) {
      selectors[i] = chimeraStylesheet.getRule(i).getSelectorObject();
    }

    return selectors;
  }
}
