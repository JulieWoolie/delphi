package com.juliewoolie.nlayout;

import com.google.common.base.Stopwatch;
import com.juliewoolie.chimera.ValueOrAuto;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import java.nio.file.Files;
import java.nio.file.Path;
import org.joml.Vector2f;
import org.junit.jupiter.api.function.Executable;

class LayoutTest implements Executable {

  static final float SCREEN_SCALE = 100;

  BoxNode rootNode;
  float screenWidth;
  float screenHeight;
  boolean print;
  boolean printSizes;
  boolean breakpoint;
  String displayName;
  Path uri;

  static void breakpoint() {

  }

  @Override
  public void execute() throws Throwable {
    Vector2f screen = new Vector2f();
    screen.x = screenWidth;
    screen.y = screenHeight;

    LayoutContext ctx = new LayoutContext(screen);
    LayoutBox node = (LayoutBox) rootNode.node;

    node.cstyle.width = ValueOrAuto.valueOf(Primitive.create(100, Unit.VW));
    node.cstyle.height = ValueOrAuto.valueOf(Primitive.create(100, Unit.VH));
    node.cstyle.boxSizing = BoxSizing.BORDER_BOX;

    if (breakpoint) {
      breakpoint();
    }

    node.position.y = screen.y;

    Stopwatch timer = Stopwatch.createStarted();
    try {
      node.reflow(ctx);
    } finally {
      timer.stop();
    }

    if (print) {
      Path output = Path.of("test-prints", displayName + ".png").toAbsolutePath();
      Path parent = output.getParent();
      if (!Files.isDirectory(parent)) {
        Files.createDirectories(parent);
      }

      int pxw = (int) (screenWidth * SCREEN_SCALE);
      int pxy = (int) (screenHeight * SCREEN_SCALE);
      LayoutPrinter.dumpLayout(output, node, pxw, pxy, new Vector2f(SCREEN_SCALE), screen);
    }

    if (printSizes) {
      System.out.printf("Dumping layout test tree structure for '%s'\n", displayName);
      printNodeSizes(rootNode, 0);
    }

    System.out.printf("Executed layout test '%s': took %s, result: ", displayName, timer);

    try {
      rootNode.runTestRecursive();
      System.out.print("PASS\n");
    } catch (Throwable t) {
      System.out.print("FAIL\n");
      throw t;
    }
  }

  void printNodeSizes(TestNode node, int ident) {
    String identStr = "  ".repeat(ident);

    System.out.printf("%s%s on line %s col %s: pos=%s size=%s\n",
        identStr,
        node.getClass().getSimpleName(),
        node.lineno,
        node.colno,
        node.node.position,
        node.node.size
    );

    if (node instanceof BoxNode box) {
      for (TestNode childNode : box.childNodes) {
        printNodeSizes(childNode, ident + 1);
      }
    }
  }
}
