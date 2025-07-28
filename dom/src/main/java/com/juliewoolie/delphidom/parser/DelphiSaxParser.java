package com.juliewoolie.delphidom.parser;

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
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.DelphiDocumentElement;
import com.juliewoolie.delphidom.DelphiElement;
import com.juliewoolie.delphidom.DelphiNode;
import com.juliewoolie.delphidom.DelphiOptionElement;
import com.juliewoolie.delphidom.ExtendedView;
import com.juliewoolie.delphidom.Text;
import com.juliewoolie.dom.TagNames;
import org.apache.commons.lang3.StringUtils;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl;
import org.slf4j.event.Level;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

@Getter
public class DelphiSaxParser extends DefaultHandler {

  public static final SAXParserFactory PARSER_FACTORY = createFactory();

  @Setter
  private SaxParserCallbacks callbacks;
  @Setter
  private ExtendedView view;

  private DelphiDocument document;

  private final Stack<DelphiNode> elementStack = new Stack<>();
  private final StringBuffer chars = new StringBuffer();

  private Locator locator;

  private final List<Error> errors = new ArrayList<>();
  private boolean failed = false;

  @Setter
  private ErrorListener listener;

  public static SAXParserFactory createFactory() {
    SAXFactoryImpl factory = new SAXFactoryImpl();

    try {
      factory.setFeature(Parser.CDATAElementsFeature, false);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    factory.setXIncludeAware(false);
    factory.setNamespaceAware(false);

    return factory;
  }

  public static XMLReader createReader() throws ParserConfigurationException, SAXException {
    SAXParser parser = PARSER_FACTORY.newSAXParser();

    XMLReader reader = parser.getXMLReader();
    reader.setProperty(Parser.schemaProperty, new DelphiSchema());

    return reader;
  }

  public static DelphiSaxParser runParser(InputSource source, DelphiSaxParser handler)
      throws ParserConfigurationException, SAXException, IOException
  {
    XMLReader reader = createReader();

    reader.setContentHandler(handler);
    reader.setEntityResolver(handler);
    reader.setErrorHandler(handler);
    reader.setDTDHandler(handler);
    reader.parse(source);

    return handler;
  }

  public void pushNode(DelphiNode node) {
    if (!elementStack.isEmpty()) {
      DelphiNode p = elementStack.peek();
      if (p instanceof DelphiElement elem) {
        elem.appendChild(node);
      }
    } else {
      DelphiDocumentElement docElem = (DelphiDocumentElement) node;
      document.setRoot(docElem);
    }

    elementStack.push(node);
  }

  public void popNode() {
    elementStack.pop();
  }

  private void appendCharsIfNotEmpty() {
    if (chars.isEmpty()) {
      return;
    }

    String str = chars.toString();
    chars.setLength(0);

    DelphiNode n = elementStack.peek();
    DelphiElement head = document.getHead();

    // If we're currently not inside the <head> element, then filter the
    // input string so that all spaces are removed.
    // This is so we don't mess with the <style> element, which might have
    // quoted strings or other inputs that could be changed by this filter
    // and might change the resulting CSS.
    if (head == null || !(head.isDescendant(n) || head.equals(n))) {
      str = str
          .replace("\n", "")
          .replace("\r", "")
          .replace("\t", "")
          .trim();

      str = StringUtils.normalizeSpace(str);
    }

    if (Strings.isNullOrEmpty(str)) {
      return;
    }

    Text txt = document.createText(str);

    pushNode(txt);
    popNode();
  }

  @Override
  public void setDocumentLocator(Locator locator) {
    this.locator = locator;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    chars.append(ch, start, length);
  }

  @Override
  public void startDocument() throws SAXException {
    document = new DelphiDocument();

    if (callbacks != null) {
      callbacks.onDocumentCreated(document);
    }

    if (view != null) {
      document.setView(view);
    }
  }

  @Override
  public void startElement(
      String uri,
      String localName,
      String qName,
      Attributes attributes
  ) throws SAXException {
    if (elementStack.isEmpty()) {
      if (!qName.equalsIgnoreCase(TagNames.ROOT)) {
        throw new SAXException("Root tag must be <" + TagNames.ROOT + ">");
      }
    }

    appendCharsIfNotEmpty();

    DelphiElement element = document.createElement(qName);
    pushNode(element);

    for (int i = 0; i < attributes.getLength(); i++) {
      String name = attributes.getQName(i);
      String value = attributes.getValue(i);
      element.setAttribute(name, value);
    }

    if (element instanceof DelphiOptionElement opt) {
      if (callbacks != null) {
        callbacks.validateOptionDeclaration(opt);
      }

      String optName = opt.getName();
      String optVal = opt.getValue();

      if (!Strings.isNullOrEmpty(optName)) {
        document.setOption(optName, optVal);
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    appendCharsIfNotEmpty();
    popNode();
  }

  @Override
  public void warning(SAXParseException e) {
    saxException(Level.WARN, e);
  }

  @Override
  public void error(SAXParseException e) {
    saxException(Level.ERROR, e);
  }

  @Override
  public void fatalError(SAXParseException e) {
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
}
