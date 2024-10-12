package net.arcadiusmc.delphidom;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import net.arcadiusmc.dom.Element;
import org.junit.jupiter.api.Test;

class DelphiElementTest {

  @Test
  void setAttribute() {
    DelphiDocument doc = createDoc();
    DelphiElement div = doc.createElement("div");

    assertThrows(NullPointerException.class, () -> div.setAttribute(null, "hello"));

    String attr = "foo";
    String val = "bar";

    assertDoesNotThrow(() -> div.setAttribute(attr, val));
    String gotten = div.getAttribute(attr);
    assertEquals(val, gotten);

    div.setAttribute(attr, null);
    gotten = div.getAttribute(attr);

    assertNull(gotten);
  }

  @Test
  void getAttributeNames() {
    DelphiDocument doc = createDoc();
    DelphiElement div = doc.createElement("div");

    Set<String> names = div.getAttributeNames();
    assertNotNull(names);
    assertTrue(names.isEmpty());

    div.setAttribute("foo", "true");
    div.setAttribute("bar", "false");
    div.setAttribute("foobar", "truefalse");

    names = div.getAttributeNames();
    assertNotNull(names);
    assertEquals(3, names.size());

    assertTrue(names.contains("foo"));
    assertTrue(names.contains("bar"));
    assertTrue(names.contains("foobar"));
  }

  @Test
  void appendChild() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();

    assertEquals(0, body.getChildCount());
    assertFalse(body.hasChildren());

    assertThrows(NullPointerException.class, () -> body.appendChild(null));

    body.appendChild(div1);
    assertEquals(1, body.getChildCount());

    // Append again
    body.appendChild(div1);
    assertEquals(1, body.getChildCount());

    body.appendChild(div2);
    assertEquals(2, body.getChildCount());

    // Double append again
    body.appendChild(div2);
    assertEquals(2, body.getChildCount());

    body.appendChild(div3);
    assertEquals(3, body.getChildCount());

    assertEquals(div1, body.getChild(0));
    assertEquals(div2, body.getChild(1));
    assertEquals(div3, body.getChild(2));

    body.appendChild(div2);

    assertEquals(div1, body.getChild(0));
    assertEquals(div3, body.getChild(1));
    assertEquals(div2, body.getChild(2));
  }

  @Test
  void prependChild() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.prependChild(div2);
    body.appendChild(div3);

    assertEquals(div2, body.getChild(0));
    assertEquals(div1, body.getChild(1));
    assertEquals(div3, body.getChild(2));
  }

  @Test
  void hasChild() {
    DelphiDocument doc = createDoc();
    DelphiElement body = doc.getBody();

    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");

    assertFalse(body.hasChild(div1));

    body.appendChild(div1);
    div1.appendChild(div2);

    assertFalse(body.hasChild(div2));
    assertTrue(body.hasChild(div1));
    assertTrue(div1.hasChild(div2));

    assertFalse(body.hasChild(null));
  }

  @Test
  void indexOf() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();

    assertEquals(-1, body.indexOf(div1));

    body.appendChild(div1);
    body.appendChild(div2);

    div2.appendChild(div3);

    assertEquals(0, body.indexOf(div1));
    assertEquals(1, body.indexOf(div2));
    assertEquals(-1, body.indexOf(div3));
    assertEquals(0, div2.indexOf(div3));
  }

  @Test
  void insertBefore() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();

    assertThrows(NullPointerException.class, () -> body.insertBefore(null, div1));
    assertThrows(NullPointerException.class, () -> body.insertBefore(div1, null));
    assertThrows(NullPointerException.class, () -> body.insertBefore(null, null));

    { // Div 2 hasn't been added, nothing should change
      body.insertBefore(div1, div2);
      assertFalse(body.hasChild(div1));
    }

    body.appendChild(div2);

    { // Div2 has been added, div1 can be added before it
      body.insertBefore(div1, div2);
      assertTrue(body.hasChild(div1));
    }

    body.insertBefore(div3, div2);

    assertEquals(3, body.getChildCount());
    assertEquals(div1, body.getChild(0));
    assertEquals(div3, body.getChild(1));
    assertEquals(div2, body.getChild(2));

    body.insertBefore(div1, div1);

    assertEquals(div1, body.getChild(0));
    assertEquals(div3, body.getChild(1));
    assertEquals(div2, body.getChild(2));
  }

  @Test
  void insertAfter() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();

    assertThrows(NullPointerException.class, () -> body.insertAfter(null, div1));
    assertThrows(NullPointerException.class, () -> body.insertAfter(div2, null));
    assertThrows(NullPointerException.class, () -> body.insertAfter(null, null));

    { // div2 not added, div1 shouldn't be either
      body.insertAfter(div1, div2);
      assertFalse(body.hasChild(div1));
    }

    body.appendChild(div2);
    body.appendChild(div3);

    body.insertAfter(div1, div2);
    assertTrue(body.hasChild(div2));

    assertEquals(div2, body.getChild(0));
    assertEquals(div1, body.getChild(1));
    assertEquals(div3, body.getChild(2));
  }

  @Test
  void removeChild() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();

    body.appendChild(div3);
    body.appendChild(div2);

    div2.appendChild(div1);

    assertTrue(body.removeChild(div3));
    assertFalse(body.removeChild(div1));
    assertEquals(1, body.getChildCount());
  }

  @Test
  void clearChildren() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    assertEquals(3, body.children.size());

    body.clearChildren();

    assertEquals(0, body.children.size());
  }

  @Test
  void firstChild() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();
    assertNull(body.firstChild());

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    assertEquals(div1, body.firstChild());
  }

  @Test
  void lastChild() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");

    DelphiElement body = doc.getBody();
    assertNull(body.lastChild());

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    assertEquals(div3, body.lastChild());
  }

  @Test
  void getElementsByTagName() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("cooler-div");

    DelphiElement body = doc.getBody();

    List<Element> list = body.getElementsByTagName("div");
    assertTrue(list.isEmpty());

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    list = body.getElementsByTagName("div");
    assertEquals(2, list.size());
    assertTrue(list.contains(div1));
    assertTrue(list.contains(div2));
    assertFalse(list.contains(div3));

    assertEquals(div1, list.get(0));
    assertEquals(div2, list.get(1));
  }

  @Test
  void querySelectorAll() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("cooler-div");

    DelphiElement body = doc.getBody();

    final String selector = "div cooler-div";

    List<Element> list = body.querySelectorAll(selector);
    assertTrue(list.isEmpty());

    body.appendChild(div1);
    body.appendChild(div2);

    div2.appendChild(div3);

    list = body.querySelectorAll(selector);
    assertEquals(1, list.size());
    assertTrue(list.contains(div3));
    assertEquals(div3, list.getFirst());
  }

  @Test
  void querySelector() {
    DelphiDocument doc = createDoc();
    DelphiElement div1 = doc.createElement("div");
    DelphiElement div2 = doc.createElement("div");
    DelphiElement div3 = doc.createElement("div");
    DelphiElement body = doc.getBody();

    final String selector = "div#cool";

    assertNull(body.querySelector(selector));

    body.appendChild(div1);
    body.appendChild(div2);
    body.appendChild(div3);

    div2.setId("cool");

    Element found = body.querySelector(selector);
    assertEquals(div2, found);
  }
}