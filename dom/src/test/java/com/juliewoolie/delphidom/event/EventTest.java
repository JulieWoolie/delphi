package com.juliewoolie.delphidom.event;

import static com.juliewoolie.delphidom.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.dom.BodyElement;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.EventTypes;
import com.juliewoolie.dom.event.MutationEvent;
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