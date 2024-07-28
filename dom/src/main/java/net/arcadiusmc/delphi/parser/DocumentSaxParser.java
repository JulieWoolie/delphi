package net.arcadiusmc.delphi.parser;

import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.delphi.dom.DelphiElement;
import net.arcadiusmc.delphi.dom.DelphiNode;
import net.arcadiusmc.delphi.dom.Text;
import net.arcadiusmc.delphi.parser.ParserErrors.Error;
import net.arcadiusmc.delphi.parser.ParserErrors.ErrorLevel;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.dom.Attr;
import net.arcadiusmc.dom.TagNames;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

@Getter
public class DocumentSaxParser extends DefaultHandler {

  public static final SAXParserFactory PARSER_FACTORY = SAXParserFactory.newInstance();

  static final String ROOT_ELEMENT = "page";
  static final String HEADER_ELEMENT = "header";
  static final String BODY_ELEMENT = TagNames.BODY;
  static final String OPTION_ELEMENT = "option";
  static final String SCREEN_ELEMENT = "screen";
  static final String STYLE_ELEMENT = "style";

  static final String ATTR_WIDTH = "width";
  static final String ATTR_HEIGHT = "height";

  private final ViewResources resources;

  private DelphiDocument document;

  private final Stack<DelphiNode> nodes = new Stack<>();
  private final Stack<LoadMode> modes = new Stack<>();

  private int depth = 0;
  private Integer ignoreDepth = null;
  private String ignoreElement = null;
  private boolean ignoreWarningLogged = false;

  private float width;
  private float height;

  private Locator locator;

  private DelphiElement root;

  private final List<Error> errors = new ArrayList<>();
  private boolean failed = false;

  @Setter
  private ErrorListener listener;

  public DocumentSaxParser(ViewResources resources) {
    this.resources = resources;
  }

  public static DocumentSaxParser runParser(ViewResources resources, InputSource source)
      throws ParserConfigurationException, SAXException, IOException
  {
    DocumentSaxParser handler = new DocumentSaxParser(resources);

    SAXParser parser = PARSER_FACTORY.newSAXParser();
    PARSER_FACTORY.setXIncludeAware(false);

    parser.parse(source, handler);

    return handler;
  }

  LoadMode mode() {
    return modes.isEmpty() ? LoadMode.NONE : modes.peek();
  }

  void beginIgnoringChildren(String tagName) {
    this.ignoreElement = tagName;
    this.ignoreDepth = depth;
    this.ignoreWarningLogged = false;
  }

  void stopIgnoringChildren() {
    this.ignoreElement = null;
    this.ignoreDepth = null;
  }

  void warnChildrenIgnored() {
    if (Strings.isNullOrEmpty(ignoreElement) || ignoreWarningLogged) {
      return;
    }

    warn("<%s/> elements cannot have children... ignoring", ignoreElement);
    ignoreWarningLogged = true;
  }

  void pushNode(DelphiNode n) {
    if (root == null) {
      root = (DelphiElement) n;
      document.setBody(root);
    }

    if (!nodes.isEmpty()) {
      DelphiElement p = (DelphiElement) nodes.peek();
      p.appendChild(n);
    }

    nodes.push(n);
  }

  void popNode() {
    nodes.pop();
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  @Override
  public void startDocument() throws SAXException {
    document = new DelphiDocument();

    depth++;
  }

  @Override
  public void endDocument() throws SAXException {
    depth--;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException
  {
    depth++;

    if (ignoreDepth != null) {
      warnChildrenIgnored();
      return;
    }

    LoadMode m = mode();
    switch (m) {
      case NONE:
      case DOCUMENT:
        switch (qName) {
          case ROOT_ELEMENT:
            modes.push(LoadMode.DOCUMENT);
            return;

          case HEADER_ELEMENT:
            modes.push(LoadMode.HEADER);
            return;

          default:
            return;

          case BODY_ELEMENT:
            // Fall through to BODY case
        }

      case BODY:
        DelphiElement element = document.createElement(qName);
        pushNode(element);
        modes.push(LoadMode.BODY);

        for (int i = 0; i < attributes.getLength(); i++) {
          String name = attributes.getLocalName(i);
          String value = attributes.getValue(i);
          element.setAttribute(name, value);
        }

        break;

      case HEADER:
        modes.push(LoadMode.HEADER);
        headerElement(qName, attributes);
        break;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    int d = depth--;

    if (ignoreDepth != null) {
      if (d > ignoreDepth) {
        return;
      }

      stopIgnoringChildren();
    }

    LoadMode first = mode();
    modes.pop();
    LoadMode prev = mode();

    if (prev == LoadMode.BODY || first == LoadMode.BODY) {
      popNode();
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (ignoreDepth != null) {
      warnChildrenIgnored();
      return;
    }

    if (mode() != LoadMode.BODY) {
      return;
    }

    String s = String.valueOf(ch, start, length).trim();

    if (Strings.isNullOrEmpty(s) || s.isBlank()) {
      return;
    }

    Text textNode = document.createText();
    textNode.setTextContent(s);

    pushNode(textNode);
    popNode();
  }

  private String validateAttribute(String elementName, String attrib, Attributes attributes) {
    String value = attributes.getValue(attrib);
    return validateAttribute(elementName, attrib, value);
  }

  private String validateAttribute(String elementName, String attrib, String value) {
    if (!Strings.isNullOrEmpty(value)) {
      return value;
    }

    warn("Missing '%s' attribute on %s", attrib, elementName);
    return null;
  }

  private void headerElement(String name, Attributes attributes) throws SAXException {
    switch (name) {
      case OPTION_ELEMENT -> {
        beginIgnoringChildren(OPTION_ELEMENT);

        String key = validateAttribute(name, Attr.KEY, attributes);
        if (Strings.isNullOrEmpty(key)) {
          return;
        }

        String value = attributes.getValue(Attr.VALUE);
        if (value == null) {
          value = "";
        }

        document.setOption(key, value);
      }

      case STYLE_ELEMENT -> {
        beginIgnoringChildren(STYLE_ELEMENT);

        String src = validateAttribute(name, Attr.SOURCE, attributes);

        if (Strings.isNullOrEmpty(src)) {
          return;
        }

        resources.loadStylesheet(src)
            .mapError(string -> "Failed to load stylesheet from " + string + ": " + string)
            .ifSuccess(stylesheet -> document.addStylesheet(stylesheet))
            .ifError(this::error);
      }

      case SCREEN_ELEMENT -> {
        beginIgnoringChildren(SCREEN_ELEMENT);

        String widthStr = validateAttribute(name, ATTR_WIDTH, attributes);
        String heightStr = validateAttribute(name, ATTR_HEIGHT, attributes);

        if (Strings.isNullOrEmpty(widthStr) || Strings.isNullOrEmpty(heightStr)) {
          return;
        }

        parseScreenDimension(widthStr, ATTR_WIDTH, t -> this.width = t);
        parseScreenDimension(heightStr, ATTR_HEIGHT, t -> this.height = t);
      }

      default -> {
        // :shrug: idk, it's not a valid header element, so it doesn't really matter
        // but should it be logged? I don't care
      }
    }
  }

  private void parseScreenDimension(String str, String dim, FloatConsumer consumer) {
    float f;

    try {
      f = Float.parseFloat(str);
    } catch (NumberFormatException exc) {
      error("Failed to convert '%s' to number for screen %s", str, dim);
      return;
    }

    if (f < 1) {
      error("Screen %s cannot be less than 1, value: %s", dim, f);
      return;
    }

    consumer.accept(f);
  }


  @Override
  public void warning(SAXParseException e) throws SAXException {
    saxException(ErrorLevel.WARN, e);
  }

  @Override
  public void error(SAXParseException e) throws SAXException {
    saxException(ErrorLevel.ERROR, e);
  }

  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    saxException(ErrorLevel.ERROR, e);
  }

  private void warn(String message, Object... args) {
    log(ErrorLevel.WARN, message, args);
  }

  private void error(String message, Object... args) {
    log(ErrorLevel.ERROR, message, args);
  }

  private void log(ErrorLevel level, String message, Object... args) {
    saxException(level, new SAXParseException(String.format(message, args), locator));
  }

  private void saxException(ErrorLevel level, SAXParseException exc) {
    if (level == ErrorLevel.ERROR) {
      failed = true;
    }

    String message = "XML loading error at %s#%s:%s: %s".formatted(
        exc.getPublicId(),
        exc.getLineNumber(),
        exc.getColumnNumber(),
        exc.getMessage()
    );

    Error e = new Error(message, level);
    errors.add(e);

    if (listener != null) {
      listener.onError(e);
    }
  }

  private enum LoadMode {
    NONE,
    DOCUMENT,
    HEADER,
    BODY;
  }
}
