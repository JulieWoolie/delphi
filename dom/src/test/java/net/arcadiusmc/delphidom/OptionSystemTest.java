package net.arcadiusmc.delphidom;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.dom.OptionElement;
import net.arcadiusmc.dom.TagNames;
import org.junit.jupiter.api.Test;

public class OptionSystemTest {

  @Test
  void should_createOptionElementInHead_when_OptionSet() {
    DelphiDocument doc = createDoc();
    DelphiHeaderElement head = doc.getHeader();

    assertTrue(head.getChildCount() < 1);

    doc.setOption("foo", "bar");

    assertEquals(1, head.getChildCount());
    DelphiOptionElement opt = assertInstanceOf(DelphiOptionElement.class, head.getChild(0));

    assertEquals("foo", opt.getName());
    assertEquals("bar", opt.getValue());
  }

  @Test
  void should_changeOption_when_elementAttrsChanged() {
    DelphiDocument doc = createDoc();
    DelphiHeaderElement head = doc.getHeader();

    OptionElement opt = assertInstanceOf(OptionElement.class, head.appendElement(TagNames.OPTION));
    opt.setName("foo");
    opt.setValue("bar");

    assertEquals("bar", doc.getOption("foo"));
  }

  @Test
  void should_removeOption_when_elementNameRemoved() {
    DelphiDocument doc = createDoc();
    DelphiHeaderElement head = doc.getHeader();

    OptionElement opt = assertInstanceOf(OptionElement.class, head.appendElement(TagNames.OPTION));
    opt.setName("foo");
    opt.setValue("bar");

    assertEquals("bar", doc.getOption("foo"));

    opt.setName(null);

    assertNull(doc.getOption("foo"));
  }

  @Test
  void should_removeOption_when_elementValueRemoved() {
    DelphiDocument doc = createDoc();
    DelphiHeaderElement head = doc.getHeader();

    OptionElement opt = assertInstanceOf(OptionElement.class, head.appendElement(TagNames.OPTION));
    opt.setName("foo");
    opt.setValue("bar");

    assertEquals("bar", doc.getOption("foo"));

    opt.setValue(null);

    assertNull(doc.getOption("foo"));
  }

  @Test
  void should_changeOptionElementInHead_when_OptionSetAndElementAlreadyThere() {
    DelphiDocument doc = createDoc();
    DelphiHeaderElement head = doc.getHeader();

    OptionElement opt = assertInstanceOf(OptionElement.class, head.appendElement(TagNames.OPTION));
    opt.setName("foo");
    opt.setValue("bar");

    doc.setOption("foo", "foobar");

    assertEquals("foobar" ,opt.getValue());
  }

  @Test
  void should_removeOptionElementInHead_when_optionRemoved() {
    DelphiDocument doc = createDoc();
    DelphiHeaderElement head = doc.getHeader();

    OptionElement opt = assertInstanceOf(OptionElement.class, head.appendElement(TagNames.OPTION));
    opt.setName("foo");
    opt.setValue("bar");
    assertEquals("bar", doc.getOption("foo"));

    doc.removeOption("foo");

    assertEquals(0, head.getChildCount());
  }

  @Test
  void should_notChangeDocOptions_when_optionElementNotAdded() {
    DelphiDocument doc = createDoc();
    DelphiHeaderElement head = doc.getHeader();

    assertNull(doc.getOption("foo"));

    OptionElement opt = (OptionElement) doc.createElement(TagNames.OPTION);
    opt.setName("foo");
    opt.setValue("bar");

    assertNull(doc.getOption("foo"));
  }

  @Test
  void should_removeOption_when_optionElementRemoved() {
    DelphiDocument doc = createDoc();
    DelphiHeaderElement head = doc.getHeader();

    OptionElement opt = (OptionElement) doc.createElement(TagNames.OPTION);
    opt.setName("foo");
    opt.setValue("bar");
    head.appendChild(opt);

    assertEquals(1, head.getChildCount());

    doc.setOption("foo", null);

    assertEquals(0, head.getChildCount());
  }
}
