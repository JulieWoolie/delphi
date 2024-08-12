package net.arcadiusmc.delphiplugin.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class RectangleTest {

  @Test
  void encompass() {
    Rectangle r1 = new Rectangle();
    Rectangle r2 = new Rectangle();

    r1.position.set(0f, 0f);
    r1.size.set(1f, 1f);

    r2.position.set(0.75f, -1f);
    r2.size.set(2f, 2f);

    Rectangle encompasing = new Rectangle();
    r1.encompass(r2, encompasing);

    assertEquals( 0f, encompasing.position.x);
    assertEquals(-1f, encompasing.position.y);

    Vector2f r1Max = new Vector2f();
    Vector2f r2Max = new Vector2f();

    r1.getMax(r1Max);
    r2.getMax(r2Max);

    assertEquals(1.25f, encompasing.size.x);
    assertEquals(1f, encompasing.size.y);
  }
}