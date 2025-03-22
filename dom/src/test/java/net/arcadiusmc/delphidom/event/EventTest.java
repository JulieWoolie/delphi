package net.arcadiusmc.delphidom.event;

import static net.arcadiusmc.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.dom.BodyElement;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.EventTypes;
import net.arcadiusmc.dom.event.MutationEvent;
import org.junit.jupiter.api.Test;

class EventTest {

  @Test
  void addListeners() {
    DelphiDocument doc = createDoc();

    assertThrows(NullPointerException.class, () -> {
      doc.addEventListener(null, event -> {});
    });

    assertThrows(NullPointerException.class, () -> {
      doc.addEventListener(EventTypes.APPEND_CHILD, null);
    });

    Ref<Boolean> ref = new Ref<>();
    ref.val = false;

    doc.addEventListener(EventTypes.MODIFY_OPTION, event -> {
      ref.val = true;
    });

    doc.setOption("opt", "true");

    assertTrue(ref.val);
  }

  @Test
  void should_setAppendChild_when_set() {
    Document doc = createDoc();
    BodyElement body = doc.getBody();

    Ref<Boolean> b = new Ref<>();
    b.val = false;

    EventListener.Typed<MutationEvent> listener = event -> {
      b.val = true;
    };


    body.onAppendChild(listener);
  }

  class Ref<T> {
    T val;
  }
}