package com.juliewoolie.chimera.parse;

import com.juliewoolie.chimera.parse.ast.InlineStyleStatement;
import com.juliewoolie.chimera.parse.ast.SheetStatement;

public final class Tests {
  private Tests() {}

  public static ChimeraParser parser(String input) {
    ChimeraParser parser = new ChimeraParser(input);

    CompilerErrors errors = parser.getErrors();
    errors.setSourceName("test-src.scss");
    errors.setListener(error -> {
      throw new ChimeraException(error);
    });

    return parser;
  }

  public static SheetStatement sheet(String in) {
    return parser(in).stylesheet();
  }

  public static InlineStyleStatement inline(String in) {
    return parser(in).inlineStyle();
  }
}
