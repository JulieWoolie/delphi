package net.arcadiusmc.dom.style;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

  @Test
  void rgb() {
    Color c = Color.rgb(255, 10, 10);
    assertEquals(255, c.getRed());
    assertEquals(10, c.getGreen());
    assertEquals(10, c.getBlue());

    c = Color.rgb(0xFFc0c0);
    assertEquals(0xFF, c.getRed());
    assertEquals(0xc0, c.getGreen());
    assertEquals(0xc0, c.getBlue());
  }

  @Test
  void argb() {
    Color c = Color.argb(25, 255, 10, 10);
    assertEquals(25, c.getAlpha());
    assertEquals(255, c.getRed());
    assertEquals(10, c.getGreen());
    assertEquals(10, c.getBlue());

    c = Color.argb(0xabFFc0c0);
    assertEquals(0xab, c.getAlpha());
    assertEquals(0xFF, c.getRed());
    assertEquals(0xc0, c.getGreen());
    assertEquals(0xc0, c.getBlue());
  }

  @Test
  void brighten() {
    Color pre = NamedColor.DARK_GREEN;
    Color after = pre.brighten();

    assertNotEquals(pre, after);
  }

  @Test
  void darken() {
  }
}