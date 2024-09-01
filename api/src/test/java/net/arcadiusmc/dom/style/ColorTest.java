package net.arcadiusmc.dom.style;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ColorTest {

  @Test
  void testArgb() {
    assertEquals(0xFFFFFFFF, NamedColor.WHITE.argb());
  }

  @Test
  void testToString() {
    assertEquals("red", NamedColor.RED.toString());
    assertEquals("red", Color.rgb(255, 0, 0).toString());
    assertEquals("#ffccff", Color.rgb(0x00FFCCFF).toString());
  }
}