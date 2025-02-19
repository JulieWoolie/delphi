package net.arcadiusmc.delphidom;

import java.io.StringReader;
import java.util.StringJoiner;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxTest {

  static final String INPUT_STR = """
  <delphi>
    <head>
      <option key="asd" val="asd"/>
    </head>
    <body>
      <h1>Hello, world!</h1>
    </body>
  </delphi>
  """;

  @Test
  void run() throws Exception {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();

    Listener listener = new Listener();
    InputSource source = new InputSource("testsource.xml");
    source.setCharacterStream(new StringReader(INPUT_STR));

    saxParser.parse(source, listener);
  }

  class Listener extends DefaultHandler {

    int indent = 0;

    void p(Object... args) {
      if (indent > 0) {
        System.out.printf("  ".repeat(indent));
      }

      for (Object arg : args) {
        System.out.print(arg);
      }
      System.out.println();
    }

    @Override
    public void startDocument() throws SAXException {
      p("Start document");
    }

    @Override
    public void endDocument() throws SAXException {
      p("End document");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
    {
      indent++;

      StringJoiner joiner = new StringJoiner(" ", "[", "]");
      for (int i = 0; i < attributes.getLength(); i++) {
        String name = attributes.getLocalName(i);
        String val = attributes.getValue(i);
        joiner.add(name + "=" + val);
      }

      p("Start element: ", qName, ", attrs: ", joiner);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      p("End element: ", qName);
      indent--;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      StringBuffer buf = new StringBuffer(length);
      buf.append(ch, start, length);

      String chStr = buf.toString()
          .replace("\n", "\\n")
          .replace("\r", "\\r")
          .replace("\t", "\\t");

      p("chars: ", '"', chStr, '"');
    }
  }
}
