package net.arcadiusmc.delphidom;

import java.util.Objects;
import net.arcadiusmc.dom.Canvas;
import net.arcadiusmc.dom.CanvasElement;
import net.arcadiusmc.dom.style.Color;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Vector4fc;
import org.joml.Vector4i;
import org.joml.Vector4ic;

public class DelphiCanvas implements Canvas {

  static final int CHANNELS = 3;
  static final int CH_RED = 0;
  static final int CH_GREEN = 1;
  static final int CH_BLUE = 2;
  static final float MAX_VALUE = 255.0f;

  private int width;
  private int height;

  private final DelphiCanvasElement element;

  private byte[] data;

  public DelphiCanvas(DelphiCanvasElement element) {
    this.element = element;
    this.width = 0;
    this.height = 0;
  }

  public void setSize(int w, int h) {
    int size = w * h * CHANNELS;

    if (data == null) {
      data = new byte[size];
    } else {
      data = resize(data, width, height, w, h);
    }

    this.width = w;
    this.height = h;
  }

  private static byte[] resize(byte[] data, int ow, int oh, int nw, int nh) {
    int nsize = nw * nh * CHANNELS;
    byte[] narr = new byte[nsize];

    return resizePixelArray(data, narr, ow, oh, nw, nh, CHANNELS);
  }

  public static <T> T resizePixelArray(
      T from,
      T to /* t to... hehe teto */,
      int ow, int oh,
      int nw, int nh,
      int channels
  ) {
    int newRowLen = nw * channels;
    int oldRowLen = ow * channels;

    int rows = Math.min(oh, nh);
    int rowLen = Math.min(newRowLen, oldRowLen);

    for (int row = 0; row < rows; row++) {
      int oldStart = oldRowLen * row;
      int newStart = newRowLen * row;
      System.arraycopy(from, oldStart, to, newStart, rowLen);
    }

    return to;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public @NotNull CanvasElement getElement() {
    return element;
  }

  private void testBounds(int x, int y) {
    if (x < 0 || x >= width) {
      throw new IllegalArgumentException("x coordinate (" + x + ") out of range 0.." + width);
    }
    if (y < 0 || y >= height) {
      throw new IllegalArgumentException("y coordinate (" + y + ") out of range 0.." + height);
    }
  }

  private int pixelIndex(int x, int y) {
    testBounds(x, y);
    int idx = x + (y * width);
    return idx * CHANNELS;
  }

  private int toChannel(float f) {
    if (f < 0) {
      return 0;
    }
    if (f > 1) {
      return 255;
    }
    return (int) (f * MAX_VALUE);
  }

  public void sample(int idx, Vector3i out) {
    idx *= CHANNELS;
    out.x = data[idx + CH_RED] & 0xff;
    out.y = data[idx + CH_GREEN] & 0xff;
    out.z = data[idx + CH_BLUE] & 0xff;
  }

  @Override
  public Vector3f getColorf(int x, int y, @NotNull Vector3f out) {
    Objects.requireNonNull(out, "out is null");

    int idx = pixelIndex(x, y);

    float r = data[idx + CH_RED] & 0xff;
    float g = data[idx + CH_GREEN] & 0xff;
    float b = data[idx + CH_BLUE] & 0xff;

    out.x = r / MAX_VALUE;
    out.y = g / MAX_VALUE;
    out.z = b / MAX_VALUE;

    return out;
  }

  @Override
  public Vector3i getColori(int x, int y, @NotNull Vector3i out) {
    Objects.requireNonNull(out, "out is null");

    int idx = pixelIndex(x, y);

    out.x = data[idx + CH_RED] & 0xff;
    out.y = data[idx + CH_GREEN] & 0xff;
    out.z = data[idx + CH_BLUE] & 0xff;

    return out;
  }

  @Override
  public @NotNull Vector3f getColorf(int x, int y) {
    return getColorf(x, y, new Vector3f());
  }

  @Override
  public @NotNull Vector3i getColori(int x, int y) {
    return getColori(x, y, new Vector3i());
  }

  @Override
  public Color getColor(int x, int y) throws IllegalArgumentException {
    Vector3i c = new Vector3i();
    getColori(x, y, c);
    return Color.rgb(c.x, c.y, c.z);
  }

  @Override
  public void setColor(int x, int y, Color color)
      throws NullPointerException, IllegalArgumentException
  {
    Vector4i vec = new Vector4i();
    vec.x = color.getRed();
    vec.y = color.getGreen();
    vec.z = color.getBlue();
    vec.w = color.getAlpha();
    setColori(x, y, vec);
  }

  private int combineAlpha(int idx, int v, float a) {
    int ev = data[idx];
    if (a <= 0.0) {
      return ev;
    }

    return (int) (v * a + ev * (1.0f - a));
  }

  @Override
  public void setColorf(int x, int y, @NotNull Vector4fc color) {
    Objects.requireNonNull(color, "color is null");

    float a = color.w();
    if (a <= 0.0) {
      return;
    }

    int r = toChannel(color.x());
    int g = toChannel(color.y());
    int b = toChannel(color.z());

    if (a < 1.0) {
      int idx = pixelIndex(x, y);
      r = combineAlpha(idx + CH_RED, r, a);
      g = combineAlpha(idx + CH_GREEN, g, a);
      b = combineAlpha(idx + CH_BLUE, b, a);
    }

    setColorInternal(x, y, r, g, b);
  }

  @Override
  public void setColori(int x, int y, @NotNull Vector4ic color) {
    Objects.requireNonNull(color, "color is null");

    int ai = color.w();
    if (ai <= 0) {
      return;
    }

    int r = (color.x() & 0xff);
    int g = (color.y() & 0xff);
    int b = (color.z() & 0xff);

    if (ai < 255) {
      int idx = pixelIndex(x, y);
      float a = ai / MAX_VALUE;

      r = combineAlpha(idx + CH_RED, r, a);
      g = combineAlpha(idx + CH_GREEN, g, a);
      b = combineAlpha(idx + CH_BLUE, b, a);
    }

    setColorInternal(x, y, r, g, b);
  }

  @Override
  public void setColorf(int x, int y, @NotNull Vector3fc color) {
    Objects.requireNonNull(color, "color is null");

    int r = toChannel(color.x());
    int g = toChannel(color.y());
    int b = toChannel(color.z());
    setColorInternal(x, y, r, g, b);
  }

  @Override
  public void setColori(int x, int y, @NotNull Vector3ic color) {
    Objects.requireNonNull(color, "color is null");

    int r = (color.x() & 0xff);
    int g = (color.y() & 0xff);
    int b = (color.z() & 0xff);
    setColorInternal(x, y, r, g, b);
  }

  private void setColorInternal(int x, int y, int r, int g, int b) {
    int idx = pixelIndex(x, y);

    int or = data[idx + 0] & 0xff;
    int og = data[idx + 1] & 0xff;
    int ob = data[idx + 2] & 0xff;

    if (or == r && og == r && ob == b) {
      return;
    }

    data[idx + 0] = (byte) r;
    data[idx + 1] = (byte) g;
    data[idx + 2] = (byte) b;

    ExtendedView view = element.document.view;
    if (view != null) {
      view.contentChanged(element);
    }
  }
}
