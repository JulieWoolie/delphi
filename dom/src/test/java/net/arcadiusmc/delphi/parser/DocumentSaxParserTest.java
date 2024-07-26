package net.arcadiusmc.delphi.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.io.Resources;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import net.arcadiusmc.delphi.Loggers;
import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.delphi.parser.ParserErrors.Error;
import net.arcadiusmc.delphi.parser.ParserErrors.ErrorLevel;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.style.Stylesheet;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class DocumentSaxParserTest {

  static final ViewResources NOP = new ViewResources() {
    @Override
    public ResourceModule getModule() {
      return null;
    }

    @Override
    public String getModuleName() {
      return "test-module";
    }

    @Override
    public Optional<Document> loadDocument(String uri) {
      return Optional.empty();
    }

    @Override
    public Optional<Stylesheet> loadStylesheet(String uri) {
      return Optional.empty();
    }
  };

  @Test
  void screenSizeTest() {
    DocumentSaxParser handler = runSafe("test-pages/screen/w1h1.xml");
    assertEquals(1, handler.getWidth());
    assertEquals(1, handler.getHeight());

    DocumentSaxParser w = runSafe("test-pages/screen/missing-w.xml");
    assertFalse(w.getErrors().isEmpty());
    assertTrue(w.getErrors().getFirst().message().contains("Missing 'width' attribute"));

    DocumentSaxParser h = runSafe("test-pages/screen/missing-h.xml");
    assertFalse(h.getErrors().isEmpty());
    assertTrue(h.getErrors().getFirst().message().contains("Missing 'height' attribute"));
  }

  @Test
  void optionsTest() {
    DocumentSaxParser correct = runSafe("test-pages/option/correct.xml");
    assertFalse(correct.isFailed());

    DelphiDocument doc = correct.getDocument();
    assertEquals("bar", doc.getOption("foo"));

    DocumentSaxParser missingkey = runSafe("test-pages/option/missing-key.xml");
    assertFalse(missingkey.getErrors().isEmpty());
    assertTrue(missingkey.getErrors().getFirst().message().contains("Missing 'key' attribute"));

    DocumentSaxParser missingValue = runSafe("test-pages/option/missing-value.xml");
    assertTrue(missingValue.getErrors().isEmpty());
    assertEquals("", missingValue.getDocument().getOption("foo"));
  }

  static DocumentSaxParser runSafe(String resourceId) {
    return assertDoesNotThrow(() -> run(resourceId));
  }

  static DocumentSaxParser run(String resourceId)
      throws ParserConfigurationException, IOException, SAXException
  {
    String in = getStringResource(resourceId);

    InputSource source = new InputSource(resourceId);
    source.setPublicId(resourceId);
    source.setCharacterStream(new StringReader(in));

    DocumentSaxParser ran = DocumentSaxParser.runParser(NOP, source);

    for (Error error : ran.getErrors()) {
      if (error.level() != ErrorLevel.WARN) {
        continue;
      }

      Loggers.getLogger().warn(error.message());
    }

    return ran;
  }

  static String getStringResource(String uri) {
    URL url = assertDoesNotThrow(
        () -> Resources.getResource(uri),
        "Unable to find test resource: " + uri
    );

    return assertDoesNotThrow(() -> {
      return Resources.toString(url, StandardCharsets.UTF_8);
    });
  }
}