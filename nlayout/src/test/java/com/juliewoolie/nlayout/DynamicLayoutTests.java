package com.juliewoolie.nlayout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.base.Strings;
import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.PropertySet;
import com.juliewoolie.chimera.ValueOrAuto;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.parse.ChimeraException;
import com.juliewoolie.chimera.parse.ChimeraParser;
import com.juliewoolie.chimera.parse.Interpreter;
import com.juliewoolie.chimera.parse.Scope;
import com.juliewoolie.chimera.parse.ast.Expression;
import com.juliewoolie.chimera.parse.ast.InlineStyleStatement;
import com.juliewoolie.dom.style.BoxSizing;
import com.juliewoolie.dom.style.Primitive;
import com.juliewoolie.dom.style.Primitive.Unit;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.joml.Vector2f;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DynamicLayoutTests {

  @TestFactory
  Iterable<DynamicTest> loadTests() throws IOException, ParserConfigurationException, SAXException {
    Path resourcesDirectory = Path.of("src", "test", "resources").toAbsolutePath();

    List<DynamicTest> tests = new ObjectArrayList<>();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    loadFromDir(resourcesDirectory, "", factory, tests);

    return tests;
  }

  void loadFromDir(Path dir, String namePrefix, SAXParserFactory factory, List<DynamicTest> tests)
      throws IOException, ParserConfigurationException, SAXException
  {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
      for (Path path : stream) {
        if (Files.isDirectory(path)) {
          String name = path.getFileName().toString();
          loadFromDir(path, namePrefix + name + "/", factory, tests);
          continue;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
          SaxLoader loader = new SaxLoader();
          SAXParser parser = factory.newSAXParser();

          String uri = namePrefix + path.getFileName().toString();
          int dotIdx = uri.lastIndexOf('.');
          if (dotIdx != -1) {
            uri = uri.substring(0, dotIdx);
          }

          InputSource source = new InputSource(reader);
          source.setPublicId(uri);
          source.setSystemId(uri);

          parser.parse(source, loader);

          if (loader.rootNode == null) {
            continue;
          }

          LayoutTest t = new LayoutTest();
          t.rootNode = loader.rootNode;
          t.screenWidth = loader.screenWidth;
          t.screenHeight = loader.screenHeight;
          t.print = loader.print;
          t.displayName = uri;
          t.uri = path;

          tests.add(DynamicTest.dynamicTest(uri, path.toUri(), t));
        }
      }
    }
  }

  static class LayoutTest implements Executable {
    static final float SCREEN_SCALE = 100;

    BoxNode rootNode;
    float screenWidth;
    float screenHeight;
    boolean print;
    String displayName;
    Path uri;

    @Override
    public void execute() throws Throwable {
      System.out.printf("Executing layout test '%s': ", displayName);

      Vector2f screen = new Vector2f();
      screen.x = screenWidth;
      screen.y = screenHeight;

      LayoutContext ctx = new LayoutContext(screen);
      LayoutBox node = (LayoutBox) rootNode.node;

      node.cstyle.width = ValueOrAuto.valueOf(Primitive.create(100, Unit.VW));
      node.cstyle.height = ValueOrAuto.valueOf(Primitive.create(100, Unit.VH));
      node.cstyle.boxSizing = BoxSizing.BORDER_BOX;

      node.position.y = screen.y;
      node.reflow(ctx);

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

      try {
        rootNode.runTestRecursive();
        System.out.print("PASS\n");
      } catch (Throwable t) {
        System.out.print("FAIL\n");
        throw t;
      }
    }
  }

  static abstract class TestNode {
    LayoutNode node;

    Float expectedX = null;
    Float expectedY = null;
    Float expectedWidth = null;
    Float expectedHeight = null;

    int lineno = 0;
    int colno = 0;

    void runTestRecursive() {
      assertEq(expectedX, node.position.x, "x position");
      assertEq(expectedY, node.position.y, "x position");
      assertEq(expectedWidth, node.size.x, "width");
      assertEq(expectedHeight, node.size.y, "height");
    }

    void assertEq(Float expect, float actual, String field) {
      if (expect == null) {
        return;
      }

      assertEquals(
          expect,
          actual,
          "Found different " + field + " than expected,"
              + " line: " + lineno + " column: " + colno
      );
    }
  }

  static abstract class BoxNode extends TestNode {
    PropertySet styleProperties = new PropertySet();
    private final List<TestNode> childNodes = new ObjectArrayList<>();

    public void addChild(TestNode child) {
      child.node.domIndex = childNodes.size();
      childNodes.add(child);

      LayoutBox box = (LayoutBox) this.node;
      box.nodes.add(child.node);
    }

    @Override
    void runTestRecursive() {
      super.runTestRecursive();

      for (TestNode childNode : childNodes) {
        childNode.runTestRecursive();
      }
    }
  }

  static class FlowNode extends BoxNode {

  }

  static class FlexNode extends BoxNode {

  }

  static class ItemNode extends TestNode implements MeasureFunc {
    float funcWidth;
    float funcHeight;

    @Override
    public void measure(Vector2f out) {
      out.x = funcWidth;
      out.y = funcHeight;
    }
  }

  static class SaxLoader extends DefaultHandler {

    private BoxNode rootNode;
    private Stack<TestNode> nodes = new Stack<>();

    float screenWidth = 3;
    float screenHeight = 2;
    boolean print = false;

    Locator locator;

    @Override
    public void setDocumentLocator(Locator locator) {
      this.locator = locator;
    }

    float executeFloatExpr(String expr) throws SAXException {
      ChimeraParser parser = new ChimeraParser(expr);
      parser.getErrors().setListener(error -> {
        throw new ChimeraException(error);
      });

      Expression parsed = parser.expr();
      Interpreter inter = new Interpreter(parser.createContext(), Scope.createTopLevel());
      Object o = parsed.visit(inter);

      Primitive primitive = Chimera.coerceValue(Primitive.class, o);
      if (primitive == null) {
        throw new SAXException("Invalid expect expression: " + expr);
      }
      if (primitive.getUnit() == Unit.PERCENT) {
        throw new SAXException("'%' unit not supported in expression: " + expr);
      }

      LayoutContext ctx = new LayoutContext(new Vector2f(screenWidth, screenHeight));
      ctx.parentSizes.push(ctx.screenSize);

      return LayoutBox.resolvePrimitive(primitive, ctx, LayoutBox.X);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
    {
      TestNode node;
      switch (qName) {
        case "flow":
          node = new FlowNode();
          node.node = new FlowLayoutBox(new LayoutStyle(), new ComputedStyleSet());
          break;
        case "flex":
        case "flexbox":
          node = new FlexNode();
          node.node = new FlexLayoutBox(new LayoutStyle(), new ComputedStyleSet());
          break;
        case "item":
          node = new ItemNode();
          node.node = new LayoutItem();
          break;

        default:
          throw new SAXException("Invalid test node: " + qName);
      }

      node.lineno = locator.getLineNumber();
      node.colno = locator.getColumnNumber();

      if (node instanceof BoxNode box) {
        LayoutBox lbox = (LayoutBox) box.node;
        String style = attributes.getValue("style");

        if (!Strings.isNullOrEmpty(style)) {
          ChimeraParser parser = new ChimeraParser(style);
          parser.getErrors().setListener(error -> {
            throw new ChimeraException(error);
          });

          InlineStyleStatement inline = parser.inlineStyle();
          Chimera.compileInline(inline, box.styleProperties, parser.createContext());

          lbox.cstyle.putAll(box.styleProperties);
        }
      } else if (node instanceof ItemNode i) {
        String funcWidthStr = attributes.getValue("width");
        String funcHeightStr = attributes.getValue("height");
        i.funcWidth = Float.parseFloat(funcWidthStr);
        i.funcHeight = Float.parseFloat(funcHeightStr);
      }

      for (int i = 0; i < attributes.getLength(); i++) {
        String name = attributes.getLocalName(i);
        String val = attributes.getValue(i);

        switch (name.toLowerCase()) {
          case "expect-x":
            node.expectedX = executeFloatExpr(val);
            break;
          case "expect-y":
            node.expectedY = executeFloatExpr(val);
            break;
          case "expect-width":
            node.expectedWidth = executeFloatExpr(val);
            break;
          case "expect-height":
            node.expectedHeight = executeFloatExpr(val);
            break;

          default:
            break;
        }
      }

      if (rootNode == null) {
        if (!(node instanceof BoxNode box)) {
          throw new SAXException("Root element must NOT be <item>");
        }

        rootNode = box;

        String printStr = attributes.getValue("print");
        String screenX = attributes.getValue("screen-width");
        String screenY = attributes.getValue("screen-height");

        if (!Strings.isNullOrEmpty(printStr)) {
          this.print = Boolean.parseBoolean(printStr);
        }
        if (!Strings.isNullOrEmpty(screenX)) {
          this.screenWidth = Float.parseFloat(screenX);
        }
        if (!Strings.isNullOrEmpty(screenY)) {
          this.screenHeight = Float.parseFloat(screenY);
        }
      }

      if (!nodes.isEmpty() && nodes.peek() instanceof BoxNode box) {
        box.addChild(node);
      }

      nodes.push(node);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      nodes.pop();
    }
  }
}
