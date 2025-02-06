package net.arcadiusmc.delphidom.parser;

import com.google.common.base.Strings;
import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.parse.ChimeraContext;
import net.arcadiusmc.chimera.parse.ChimeraParser;
import net.arcadiusmc.chimera.parse.CompilerErrors;
import net.arcadiusmc.chimera.parse.Interpreter;
import net.arcadiusmc.chimera.parse.Scope;
import net.arcadiusmc.chimera.parse.ast.SheetStatement;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.Loggers;
import org.slf4j.Logger;

public record StyleInputConsumer(String styleName, DelphiDocument doc) implements InputConsumer {

  private static final Logger LOGGER = Loggers.getDocumentLogger();

  @Override
  public void consumeInput(String input) {
    if (Strings.isNullOrEmpty(input)) {
      return;
    }

    ChimeraParser parser = new ChimeraParser(input);

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName(styleName);
    errors.setListener(error -> {
      LOGGER.atLevel(error.getLevel())
          .setMessage(error.getFormattedError())
          .log();
    });

    SheetStatement stylesheet = parser.stylesheet();

    ChimeraContext ctx = parser.createContext();
    Scope scope = Scope.createTopLevel();

    Interpreter intr = new Interpreter(ctx, scope);
    ChimeraStylesheet sheet = intr.sheet(stylesheet);

    doc.addStylesheet(sheet);
  }
}
