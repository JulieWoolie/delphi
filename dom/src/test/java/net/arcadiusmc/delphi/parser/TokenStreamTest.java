package net.arcadiusmc.delphi.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import net.arcadiusmc.delphi.parser.TokenStream.ParseMode;
import org.junit.jupiter.api.Test;

class TokenStreamTest {

  @Test
  void recognizeNumbers() {
    recognizeToken(Token.NUMBER, "1");
    recognizeToken(Token.NUMBER, "1.234");
    recognizeToken(Token.NUMBER, "0.234");
    recognizeToken(Token.NUMBER, "0.0");
  }

  @Test
  void recognizeIds() {
    recognizeToken(Token.ID, "foo");
    recognizeToken(Token.ID, "foo-bar");
    recognizeToken(Token.ID, "foobar");
    recognizeToken(Token.ID, "foo\\:bar");
    recognizeToken(Token.ID, "java\\.util\\.Collection");
    recognizeToken(Token.ID, "aName\\(java\\.util\\.Collection\\)");
    recognizeToken(Token.ID, "\\.aName\\(java\\.util\\.Collection\\)");
  }

  @Test
  void recognizeCharTokens() {
    recognizeToken(Token.SQUIG_OPEN, "{");
    recognizeToken(Token.SQUIG_CLOSE, "}");
    recognizeToken(Token.BRACKET_OPEN, "(");
    recognizeToken(Token.BRACKET_CLOSE, ")");
    recognizeToken(Token.DOT, ".");
    recognizeToken(Token.COLON, ":");
    recognizeToken(Token.SEMICOLON, ";");
    recognizeToken(Token.DOLLAR_SIGN, "$");
    recognizeToken(Token.SQUARE_OPEN, "[");
    recognizeToken(Token.SQUARE_CLOSE, "]");
    recognizeToken(Token.HASHTAG, "#");
    recognizeToken(Token.EQUALS, "=");
    recognizeToken(Token.SQUIGLY, "~");
    recognizeToken(Token.STAR, "*");
    recognizeToken(Token.UP_ARROW, "^");
    recognizeToken(Token.AT, "@");
  }

  @Test
  void recognizeHex() {
    recognizeToken(Token.HEX, true, "#ffcc00");
    recognizeToken(Token.HEX_SHORT, true, "#fc0");
    recognizeToken(Token.HEX_ALPHA, true, "#ffcc2200");
  }

  private void recognizeToken(int ttype, String input) {
    recognizeToken(ttype, false, input);
  }

  private void recognizeToken(int ttype, boolean valueMode, String input) {
    StringBuffer buf = new StringBuffer(input);

    ParserErrors err = new ParserErrors(buf);
    TokenStream stream = new TokenStream(buf, err);

    if (valueMode) {
      stream.pushMode(ParseMode.VALUES);
    } else {
      stream.pushMode(ParseMode.TOKENS);
    }

    Token t = stream.next();

    assertEquals(
        t.type(), ttype,
        String.format("Expected %s, found %s, input: %s",
            Token.typeToString(ttype),
            t.info(),
            input
        )
    );

    if (!stream.hasNext()) {
      return;
    }

    Token n = stream.next();
    int start = n.location().cursor();

    String rem = input.substring(0, start);
    fail("Only consumed '" + rem + "'");
  }
}