package net.arcadiusmc.delphidom;

import java.io.StringReader;
import java.util.StringJoiner;
import net.arcadiusmc.delphidom.parser.DelphiSaxParser;
import net.arcadiusmc.dom.Visitor;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SaxTest {

  static final String INPUT_STR = """
<delphi>
  <head>
    <style src="./style.scss"/>
  </head>
  <body>
    <h1 class="title shadowed">Hello, world!</h1>

    <div class="shadowed" style="color: gold;">This basically works</div>

    <button class="mt-2 p-btn" onclick="ev => sendMessage(ev.getPlayer(), 'Hello!')">
      <item src="./item.json" />
      I am a button :3
    </button>

    <button action="cmd: tellraw %player% &quot;Hello, %player%!&quot;" class="mt-2 mli-2 p-btn" id="btn-settings">Settings</button>
    <button action="close" class="mt-2 mli-2 p-btn" id="btn-quit">Quit</button>

    <i class="mt-2 block">This text is italic</i>
    <u class="mt-2 block">This text is underlined</u>
    <b class="mt-2 block">This text is bold <red>and red?</red></b>
  </body>
</delphi>
  """;

  @Test
  void run() throws Exception {
    Listener listener = new Listener();
    InputSource source = new InputSource("testsource.xml");
    source.setCharacterStream(new StringReader(INPUT_STR));

    XMLReader reader = DelphiSaxParser.createReader();
    reader.setContentHandler(listener);
    reader.setEntityResolver(listener);
    reader.setErrorHandler(listener);
    reader.setDTDHandler(listener);
    reader.parse(source);
  }

  @Test
  void testParser() throws Exception {
    InputSource source = new InputSource("testsource.xml");
    source.setCharacterStream(new StringReader(INPUT_STR));

    DelphiSaxParser handler = new DelphiSaxParser();
    handler.setListener(DelphiDocument.ERROR_LISTENER);

    DelphiSaxParser.runParser(source, handler);

    XmlPrintVisitor visitor = new XmlPrintVisitor();
    Visitor.visit(handler.getDocument().getDocumentElement(), visitor);
    System.out.println(visitor);
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
        joiner.add(name + "='" + val + "'");
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
