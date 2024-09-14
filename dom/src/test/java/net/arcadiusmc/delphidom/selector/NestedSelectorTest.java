package net.arcadiusmc.delphidom.selector;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.chimera.parse.ChimeraException;
import net.arcadiusmc.chimera.parse.ChimeraParser;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.parse.ast.SheetStatement;
import net.arcadiusmc.chimera.selector.Selector;
import net.arcadiusmc.chimera.selector.Spec;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.dom.NodeFlag;
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

    assertTrue(regular.test(null, el));
    assertFalse(hoverTest.test(null, el));

    el.addFlag(NodeFlag.HOVERED);

    assertTrue(regular.test(null, el));
    assertTrue(hoverTest.test(null, el));

    Spec regularSpec = new Spec();
    Spec hoverSpec = new Spec();

    regular.appendSpec(regularSpec);
    hoverTest.appendSpec(hoverSpec);

    assertTrue(regularSpec.compareTo(hoverSpec) < 0);
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
