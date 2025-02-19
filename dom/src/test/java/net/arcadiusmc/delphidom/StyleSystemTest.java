package net.arcadiusmc.delphidom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.delphidom.event.EventListenerList;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.TextNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StyleSystemTest {

  @BeforeAll
  static void onStart() {
    EventListenerList.testEnvironment = false;
  }

  @AfterAll
  static void onEnd() {
    EventListenerList.testEnvironment = true;
  }

  @Test
  void should_useTextContent_whenTextContentSet() {
    DelphiDocument doc = DelphiDocument.createEmpty();

    DelphiStyleElement style = (DelphiStyleElement) doc.createElement(TagNames.STYLE);
    TextNode node = doc.createText(".test {color: red;}");

    assertNull(style.getStylesheet());

    style.appendChild(node);
    doc.getHeader().appendChild(style);

    ChimeraStylesheet stylesheet = style.getStylesheet();
    assertNotNull(stylesheet);
    assertEquals(1, stylesheet.getLength());
  }

  @Test
  void should_notAddSheet_when_elementNotAdded() {
    DelphiDocument doc = DelphiDocument.createEmpty();

    DelphiStyleElement style = (DelphiStyleElement) doc.createElement(TagNames.STYLE);
    TextNode node = doc.createText(".test {color: red;}");
    style.appendChild(node);

    assertEquals(0, doc.getStylesheets().size());
  }

  @Test
  void should_addSheet_when_elementAdded() {
    DelphiDocument doc = DelphiDocument.createEmpty();
    DelphiHeaderElement header = doc.getHeader();

    DelphiStyleElement style = (DelphiStyleElement) doc.createElement(TagNames.STYLE);
    TextNode node = doc.createText(".test {color: red;}");
    style.appendChild(node);
    header.appendChild(style);

    assertEquals(1, doc.getStylesheets().size());
    assertNotNull(style.getStylesheet());
  }

  @Test
  void should_removeSheet_when_elementRemoved() {
    DelphiDocument doc = DelphiDocument.createEmpty();
    DelphiHeaderElement header = doc.getHeader();

    DelphiStyleElement style = (DelphiStyleElement) doc.createElement(TagNames.STYLE);
    TextNode node = doc.createText(".test {color: red;}");
    style.appendChild(node);
    header.appendChild(style);

    header.removeChild(style);

    assertNotNull(style.getStylesheet());
    assertEquals(0, doc.getStylesheets().size());
  }

  @Test
  void should_persistSheet_when_removedAndAddedAgain() {
    DelphiDocument doc = DelphiDocument.createEmpty();
    DelphiHeaderElement header = doc.getHeader();

    DelphiStyleElement style = (DelphiStyleElement) doc.createElement(TagNames.STYLE);
    TextNode node = doc.createText(".test {color: red;}");
    style.appendChild(node);

    header.appendChild(style);

    assertEquals(1, doc.getStylesheets().size());
    header.removeChild(style);
    assertEquals(0, doc.getStylesheets().size());

    assertNotNull(style.getStylesheet());

    header.appendChild(style);
    assertEquals(1, doc.getStylesheets().size());
  }
}