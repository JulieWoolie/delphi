package net.arcadiusmc.delphidom.parser;

import com.google.common.base.Strings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiElement;
import net.arcadiusmc.delphidom.DelphiItemElement;
import net.arcadiusmc.delphidom.DelphiNode;
import net.arcadiusmc.delphidom.ExtendedView;
import net.arcadiusmc.delphidom.Text;
import net.arcadiusmc.delphidom.event.Mutation;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Options;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.event.EventTypes;
import org.slf4j.event.Level;
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
  static final String OPTIONS_ELEMENT = "options";
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

  @Setter
  private ParserCallbacks callbacks;

  private Locator locator;

  private DelphiElement root;

  private final List<Error> errors = new ArrayList<>();
  private boolean failed = false;

  @Setter
  private ErrorListener listener;

  @Setter
  private ExtendedView view;

  public DocumentSaxParser(ViewResources resources) {
    this.resources = resources;
  }

  public static DocumentSaxParser runParser(InputSource source, DocumentSaxParser handler)
      throws ParserConfigurationException, SAXException, IOException
  {
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

      Mutation mutation = new Mutation(EventTypes.APPEND_CHILD, document);
      mutation.initEvent(null, false, false, n, 0);

      document.dispatchEvent(mutation);
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
  public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes)
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

        if (element instanceof DelphiItemElement item) {
          String src = item.getAttribute(Attributes.SOURCE);

          if (!Strings.isNullOrEmpty(src)) {
            resources.loadItemStack(src)
                .mapError(exc -> "Failed to load item stack from " + src + ": " + exc.getMessage())
                .ifError(this::warn)
                .ifSuccess(item::setItemStack);
          }
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

  private String validateAttribute(String elementName, String attrib, org.xml.sax.Attributes attributes) {
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

  private void pushOption(String key, String value) {
    document.setOption(key, value);

    if (callbacks == null) {
      return;
    }

    switch (key) {
      case Options.REQUIRED_PLUGINS -> {
        String[] split = value.split("\\s+");
        List<String> missingPlugins = new ArrayList<>();

        for (String s : split) {
          if (callbacks.isPluginEnabled(s)) {
            continue;
          }

          missingPlugins.add(s);
        }

        if (!missingPlugins.isEmpty()) {
          throw new PluginMissingException(missingPlugins);
        }
      }

      default -> {

      }
    }
  }

  private void headerElement(String name, org.xml.sax.Attributes attributes) throws SAXException {
    switch (name) {
      case OPTIONS_ELEMENT -> {
        for (int i = 0; i < attributes.getLength(); i++) {
          String optionName = attributes.getLocalName(i);
          String optionValue = attributes.getValue(i);
          pushOption(optionName, optionValue);
        }
      }

      case OPTION_ELEMENT -> {
        beginIgnoringChildren(OPTION_ELEMENT);

        String key = validateAttribute(name, Attributes.NAME, attributes);
        if (Strings.isNullOrEmpty(key)) {
          return;
        }

        String value = attributes.getValue(Attributes.VALUE);
        if (value == null) {
          value = "";
        }

        pushOption(key, value);
      }

      case STYLE_ELEMENT -> {
        beginIgnoringChildren(STYLE_ELEMENT);

        String src = validateAttribute(name, Attributes.SOURCE, attributes);

        if (Strings.isNullOrEmpty(src)) {
          return;
        }

        resources.loadStylesheet(src)
            .mapError(exc -> "Failed to load stylesheet from " + src + ": " + exc.getMessage())
            .ifSuccess(stylesheet -> document.addStylesheet(stylesheet))
            .ifError(this::error);
      }

      default -> {
        // :shrug: idk, it's not a valid header element, so it doesn't really matter
        // but should it be logged? I don't care
      }
    }
  }

  @Override
  public void warning(SAXParseException e) throws SAXException {
    saxException(Level.WARN, e);
  }

  @Override
  public void error(SAXParseException e) throws SAXException {
    saxException(Level.ERROR, e);
  }

  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    saxException(Level.ERROR, e);
  }

  private void warn(String message, Object... args) {
    log(Level.WARN, message, args);
  }

  private void error(String message, Object... args) {
    log(Level.ERROR, message, args);
  }

  private void log(Level level, String message, Object... args) {
    saxException(level, new SAXParseException(String.format(message, args), locator));
  }

  private void saxException(Level level, SAXParseException exc) {
    if (level == Level.ERROR) {
      failed = true;
    }

    String message = "XML loading error at %s#%s:%s: %s".formatted(
        exc.getPublicId(),
        exc.getLineNumber(),
        exc.getColumnNumber(),
        exc.getMessage()
    );

    Error e = new Error(level, message);
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
