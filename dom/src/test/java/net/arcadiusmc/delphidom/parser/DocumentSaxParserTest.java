package net.arcadiusmc.delphidom.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.io.Resources;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Options;
import net.arcadiusmc.dom.style.Stylesheet;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

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
    public DocumentView getView() {
      return null;
    }

    @Override
    public Result<ItemStack, String> loadItemStack(String uri) {
      return Result.err("NOP");
    }

    @Override
    public Result<Document, String> loadDocument(String uri) {
      return Result.err("NOP");
    }

    @Override
    public Result<Stylesheet, String> loadStylesheet(String uri) {
      return Result.err("NOP");
    }
  };

  @Test
  void pluginOptionTest() {
    DocumentSaxParser handler = createHandler();
    handler.setCallbacks(pluginName -> false);

    InputSource in = getInput("test-pages/option/pl-req.xml");

    assertThrows(PluginMissingException.class, () -> {
      DocumentSaxParser.runParser(in, handler);
    });
  }

  @Test
  void screenSizeTest() {

  }

  @Test
  void testOptionsTag() {
    DocumentSaxParser parser = runHandlerSafe("test-pages/option/options-tag.xml");
    assertFalse(parser.isFailed());

    DelphiDocument document = parser.getDocument();
    assertEquals("3", document.getOption(Options.SCREEN_WIDTH));
    assertEquals("2", document.getOption(Options.SCREEN_HEIGHT));
    assertEquals("bar", document.getOption("foo"));
    assertEquals("true", document.getOption("foobar"));
  }

  @Test
  void optionsTest() {
    DocumentSaxParser correct = runHandlerSafe("test-pages/option/correct.xml");
    assertFalse(correct.isFailed());

    DelphiDocument doc = correct.getDocument();
    assertEquals("bar", doc.getOption("foo"));

    DocumentSaxParser missingkey = runHandlerSafe("test-pages/option/missing-key.xml");
    assertFalse(missingkey.getErrors().isEmpty());

    assertTrue(
        missingkey.getErrors()
            .getFirst()
            .message()
            .contains("Missing '" + Attributes.NAME + "' attribute")
    );

    DocumentSaxParser missingValue = runHandlerSafe("test-pages/option/missing-value.xml");
    assertTrue(missingValue.getErrors().isEmpty());
    assertEquals("", missingValue.getDocument().getOption("foo"));
  }

  static DocumentSaxParser runHandlerSafe(String resourceId) {
    return assertDoesNotThrow(() -> {
      DocumentSaxParser handler = createHandler();
      DocumentSaxParser.runParser(getInput(resourceId), handler);
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

  static DocumentSaxParser createHandler() {
    return new DocumentSaxParser(NOP);
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