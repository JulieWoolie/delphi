package net.arcadiusmc.delphiplugin.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class ScreenTest {

  static final float width = 3;
  static final float height = 2;
  static final float hw = width * 0.5f;
  static final float hh = height * 0.5f;

  @Test
  void test() {
    Screen screen = new Screen();

    screen.set(new Vector3f(), new Vector3f(1, 0, 0), width, height);
    System.out.println(screen);

    assertEquals(width, screen.dimensions.x);
    assertEquals(height, screen.dimensions.y);

    assertEquals(width, screen.worldDimensions.x);
    assertEquals(height, screen.worldDimensions.y);

    assertEquals(1.0f, screen.screenScale.x);
    assertEquals(1.0f, screen.screenScale.y);

    Vector3f hiRight = new Vector3f(0,  hh,  hw);
    Vector3f loRight = new Vector3f(0, -hh,  hw);
    Vector3f hiLeft = new Vector3f(0,  hh, -hw);
    Vector3f loLeft = new Vector3f(0, -hh, -hw);

    assertEquals(hiRight, screen.hiRight);
    assertEquals(loRight, screen.loRight);
    assertEquals(hiLeft, screen.hiLeft);
    assertEquals(loLeft, screen.loLeft);

    Quaternionf lrot = new Quaternionf();
    lrot.rotateY((float) Math.toRadians(45));

    lrot.transform(hiRight);
    lrot.transform(loRight);
    lrot.transform(hiLeft);
    lrot.transform(loLeft);

    screen.multiply(new Vector3f(1), lrot, new Quaternionf());

    assertEquals(hiRight, screen.hiRight);
    assertEquals(loRight, screen.loRight);
    assertEquals(hiLeft, screen.hiLeft);
    assertEquals(loLeft, screen.loLeft);
  }
}