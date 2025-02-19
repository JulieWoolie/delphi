package net.arcadiusmc.delphidom;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class DelphiNodeTest {

  @Test
  void testSiblingMethods() {
    DelphiDocument doc = createDoc();
    DelphiBodyElement body = doc.getBody();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    body.appendChild(div1);
    body.appendChild(div2);

    assertEquals(div1.nextSibling(), div2);
    assertEquals(div2.previousSibling(), div1);
  }

  @Test
  void getOwningDocument() {
    DelphiDocument doc = new DelphiDocument();
    DelphiBodyElement body = (DelphiBodyElement) doc.createElement("body");
    assertEquals(body.getDocument(), doc);
  }

  @Test
  void getSiblingIndex() {
    DelphiDocument doc = createDoc();
    DelphiBodyElement body = doc.getBody();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    assertEquals(div1.getSiblingIndex(), -1);
    assertEquals(div2.getSiblingIndex(), -1);

    body.appendChild(div1);
    body.appendChild(div2);

    assertEquals(div1.getSiblingIndex(), 0);
    assertEquals(div2.getSiblingIndex(), 1);

    body.removeChild(div1);

    assertEquals(div1.getSiblingIndex(), -1);
  }

  @Test
  void getDepth() {
    DelphiDocument doc = createDoc();
    DelphiBodyElement body = doc.getBody();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    body.appendChild(div1);
    div1.appendChild(div2);

    assertEquals(body.getDepth(), 1);
    assertEquals(div1.getDepth(), 2);
    assertEquals(div2.getDepth(), 3);

    body.removeChild(div1);

    assertEquals(div1.getDepth(), 0);
    assertEquals(div2.getDepth(), 1);
  }

  @Test
  void getParent() {
    DelphiDocument doc = createDoc();
    DelphiBodyElement body = doc.getBody();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    assertNull(div1.getParent());
    assertNull(div2.getParent());

    body.appendChild(div1);
    div1.appendChild(div2);

    assertEquals(div1.getParent(), body);
    assertEquals(div2.getParent(), div1);

    body.removeChild(div1);

    assertNull(div1.getParent());
  }

  @Test
  void depthTest() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiBodyElement body = doc.getBody();
    body.appendChild(div1);
    div1.appendChild(div2);
    div2.appendChild(div3);

    assertEquals(1, body.getDepth());
    assertEquals(2, div1.getDepth());
    assertEquals(3, div2.getDepth());
    assertEquals(4, div3.getDepth());
  }
}