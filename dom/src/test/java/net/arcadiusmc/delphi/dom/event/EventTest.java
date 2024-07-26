package net.arcadiusmc.delphi.dom.event;

import static net.arcadiusmc.delphi.TestUtil.createDoc;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.dom.event.EventTypes;
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

  class Ref<T> {
    T val;
  }
}