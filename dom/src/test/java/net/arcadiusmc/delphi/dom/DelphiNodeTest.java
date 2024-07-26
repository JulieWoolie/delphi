package net.arcadiusmc.delphi.dom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DelphiNodeTest {

  @Test
  void testSiblingMethods() {
    DelphiDocument doc = new DelphiDocument();
    DelphiElement body = doc.createElement("body");
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    doc.setBody(body);
    body.appendChild(div1);
    body.appendChild(div2);

    assertEquals(div1.nextSibling(), div2);
    assertEquals(div2.previousSibling(), div1);
  }

  @Test
  void getOwningDocument() {
    DelphiDocument doc = new DelphiDocument();
    DelphiElement body = doc.createElement("body");
    assertEquals(body.getOwningDocument(), doc);
  }

  @Test
  void getSiblingIndex() {
    DelphiDocument doc = new DelphiDocument();
    DelphiElement body = doc.createElement("body");
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    doc.setBody(body);

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
    DelphiDocument doc = new DelphiDocument();
    DelphiElement body = doc.createElement("body");
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    doc.setBody(body);
    body.appendChild(div1);
    div1.appendChild(div2);

    assertEquals(body.getDepth(), 0);
    assertEquals(div1.getDepth(), 1);
    assertEquals(div2.getDepth(), 2);

    body.removeChild(div1);

    assertEquals(div1.getDepth(), 0);
    assertEquals(div2.getDepth(), 1);
  }

  @Test
  void getParent() {
    DelphiDocument doc = new DelphiDocument();
    DelphiElement body = doc.createElement("body");
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    assertNull(div1.getParent());
    assertNull(div2.getParent());

    doc.setBody(body);
    body.appendChild(div1);
    div1.appendChild(div2);

    assertNull(body.getParent());
    assertEquals(div1.getParent(), body);
    assertEquals(div2.getParent(), div1);

    body.removeChild(div1);

    assertNull(div1.getParent());
  }
}