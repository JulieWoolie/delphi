package com.juliewoolie.delphidom.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.google.common.io.Resources;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.xml.sax.InputSource;

class DocumentSaxParserTest {

  static DelphiSaxParser runHandlerSafe(String resourceId) {
    return assertDoesNotThrow(() -> {
      DelphiSaxParser handler = createHandler();
      DelphiSaxParser.runParser(getInput(resourceId), handler);
      return handler;
    });
  }

  static InputSource getInput(String resourceId) {
    String in = getStringResource(resourceId);
    InputSource source = new InputSource(resourceId);

    source.setPublicId(resourceId);
    source.setCharacterStream(new StringReader(in));

    return source;
  }

  static DelphiSaxParser createHandler() {
    return new DelphiSaxParser();
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