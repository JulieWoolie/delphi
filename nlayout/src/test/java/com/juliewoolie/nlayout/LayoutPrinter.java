package com.juliewoolie.nlayout;

import com.juliewoolie.delphidom.Rect;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class LayoutPrinter {

  private static final Color[] COLORS = {
      Color.BLUE,
      Color.RED,
      Color.GREEN,
      Color.CYAN,
      Color.PINK,
      Color.YELLOW,
      Color.GRAY
  };
  private static int colorIdx = 0;

  public static BufferedImage print(
      LayoutBox rootNode,
      int width,
      int height,
      Vector2f elementScale,
      Vector2f screenSize
  ) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = img.createGraphics();
    paint(rootNode, graphics, elementScale, screenSize);
    return img;
  }

  public static void dumpLayout(
      LayoutBox rootNode,
      int width,
      int height,
      Vector2f elementScale,
      Vector2f screenSize
  ) throws IOException {
    BufferedImage img = print(rootNode, width, height, elementScale, screenSize);
    Path path = Path.of("layout.png");

    try (OutputStream stream = Files.newOutputStream(path)) {
      ImageIO.write(img, "PNG", stream);
    }
  }

  private static void paint(LayoutNode node, Graphics2D out, Vector2f scale, Vector2f screenSize) {
    Vector2i size = new Vector2i();
    size.x = (int) Math.floor(node.size.x * scale.x);
    size.y = (int) Math.floor(node.size.y * scale.y);

    Vector2i pos = new Vector2i();
    pos.x  = (int) Math.floor(node.position.x * scale.x);
    pos.y  = (int) Math.floor((screenSize.y - node.position.y) * scale.y);

    System.out.printf("pixelpos=%s pixelsize=%s node.size=%s node.pos=%s\n", pos, size, node.size, node.position);

    Color baseColor = nextColor();

    out.setColor(baseColor);
    out.fillRect(pos.x, pos.y, size.x, size.y);

    if (node instanceof LayoutItem) {
      return;
    }

    LayoutBox box = (LayoutBox) node;

    Rect outline = box.style.outline;
    Rect border = box.style.border;
    Rect padding = box.style.padding;

    offsetBy(pos, size, outline, scale);
    out.setColor(baseColor = baseColor.darker());
    out.fillRect(pos.x, pos.y, size.x, size.y);

    offsetBy(pos, size, border, scale);
    out.setColor(baseColor = baseColor.darker());
    out.fillRect(pos.x, pos.y, size.x, size.y);

    offsetBy(pos, size, padding, scale);
    out.fillRect(pos.x, pos.y, size.x, size.y);

    for (LayoutNode layoutNode : box.nodes) {
      paint(layoutNode, out, scale, screenSize);
    }
  }

  private static void offsetBy(Vector2i pos, Vector2i size, Rect box, Vector2f scale) {
    pos.x += (int) Math.floor(box.left * scale.x);
    pos.y += (int) Math.floor(box.top * scale.y);

    size.x -= (int) Math.floor(box.x() * scale.x);
    size.y -= (int) Math.floor(box.y() * scale.y);
  }

  private static Color nextColor() {
    int idx = colorIdx++ % COLORS.length;
    return COLORS[idx];
  }
}
