package net.arcadiusmc.delphidom;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class DelphiDocumentTest {

  @Test
  void createElement() {
    DelphiDocument doc = createDoc();
    DelphiElement div = doc.createElement("div");
    DelphiElement item = doc.createElement("item");

    assertInstanceOf(DelphiItemElement.class, item);
  }

  @Test
  void getElementById() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    div3.setId("test");

    doc.getBody().appendChild(div1);
    doc.getBody().appendChild(div2);
    div2.appendChild(div3);

    assertEquals(div3, doc.getElementById("test"));

    div2.setId("test2");
    assertEquals(div2, doc.getElementById("test2"));
  }

  @Test
  void adopt() {
    DelphiDocument doc1 = createDoc();
    DelphiDocument doc2 = createDoc();

    DelphiElement div1 = doc1.createElement("div");
    DelphiElement div2 = doc1.createElement("div");
    DelphiElement div3 = doc1.createElement("div");

    div1.appendChild(div2);
    div2.appendChild(div3);

    assertEquals(doc1, div1.getDocument());

    doc2.adopt(div1);
    assertEquals(doc2, div1.getDocument());
    assertEquals(doc2, div2.getDocument());
    assertEquals(doc2, div3.getDocument());
  }
}