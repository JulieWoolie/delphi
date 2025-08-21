package com.juliewoolie.nlayout;

import com.google.common.base.Strings;
import com.juliewoolie.chimera.ComputedStyleSet;
import com.juliewoolie.chimera.parse.Chimera;
import com.juliewoolie.chimera.parse.ChimeraException;
import com.juliewoolie.chimera.parse.ChimeraParser;
import com.juliewoolie.chimera.parse.Interpreter;
import com.juliewoolie.chimera.parse.Scope;
import com.juliewoolie.chimera.parse.ast.Expression;
import com.juliewoolie.chimera.parse.ast.InlineStyleStatement;
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
          t.printSizes = loader.printSizes;
          t.breakpoint = loader.breakpoint;
          t.displayName = uri;
          t.uri = path;

          tests.add(DynamicTest.dynamicTest(uri, path.toUri(), t));
        }
      }
    }
  }

  static class SaxLoader extends DefaultHandler {

    private BoxNode rootNode;
    private Stack<TestNode> nodes = new Stack<>();

    float screenWidth = 3;
    float screenHeight = 2;

    boolean print = false;
    boolean printSizes = false;
    boolean breakpoint = false;

    Locator locator;

    @Override
    public void setDocumentLocator(Locator locator) {
      this.locator = locator;
    }

    float executeFloatExpr(String expr) throws SAXException {
      ChimeraParser parser = new ChimeraParser(expr);
      parser.getErrors().setSourceName(locator.getPublicId());
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
          LayoutItem item = new LayoutItem();
          item.measureFunc = (MeasureFunc) node;
          node.node = item;
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
          parser.getErrors().setSourceName(locator.getPublicId());
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
        i.funcWidth = executeFloatExpr(funcWidthStr);
        i.funcHeight = executeFloatExpr(funcHeightStr);
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
        String printSizesStr = attributes.getValue("print-sizes");
        String screenX = attributes.getValue("screen-width");
        String screenY = attributes.getValue("screen-height");

        this.breakpoint = !Strings.isNullOrEmpty(attributes.getValue("breakpoint"));

        if (!Strings.isNullOrEmpty(printStr)) {
          this.print = Boolean.parseBoolean(printStr);
        }
        if (!Strings.isNullOrEmpty(printSizesStr)) {
          this.printSizes = Boolean.parseBoolean(printSizesStr);
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
