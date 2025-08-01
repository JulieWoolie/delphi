package com.juliewoolie.chimera.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.juliewoolie.chimera.parse.TokenStream.ParseMode;
import org.junit.jupiter.api.Test;

class TokenStreamTest {

  @Test
  void testTokenNames() {
    String unknown = "unknown";

    for (int i = 0; i < Token.LAST_TOKEN; i++) {
      String name = Token.typeToString(i);
      assertNotEquals(unknown, name, "Token type " + i + " was unknown");
    }
  }

  @Test
  void recognizeNumbers() {
    recognizeToken(Token.INT, "1");
    recognizeToken(Token.INT, "-1");
    recognizeToken(Token.INT, "15");
    recognizeToken(Token.INT, "15475");
    recognizeToken(Token.INT, "-15475");
    recognizeToken(Token.NUMBER, "1.234");
    recognizeToken(Token.NUMBER, "0.234");
    recognizeToken(Token.NUMBER, "0.0");
    recognizeToken(Token.NUMBER, "-1.0");
    recognizeToken(Token.NUMBER, "1.0e12");
    recognizeToken(Token.NUMBER, "1.0e+12");
    recognizeToken(Token.NUMBER, "1.0e-12");
    recognizeToken(Token.NUMBER, "-1.0e-12");
    recognizeToken(Token.NUMBER, "1.0e-12");
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
    recognizeToken(Token.COMMA, ",");
    recognizeToken(Token.PERCENT, "%");
    recognizeToken(Token.EXCLAMATION, "!");
    recognizeToken(Token.PLUS, "+");
    recognizeToken(Token.ANGLE_RIGHT, ">");
    recognizeToken(Token.ANGLE_LEFT, "<");
    recognizeToken(Token.MINUS, "-");
    recognizeToken(Token.ELLIPSES, "...");
    recognizeToken(Token.GTE, ">=");
    recognizeToken(Token.LTE, "<=");
    recognizeToken(Token.EQUAL_TO, "==");
    recognizeToken(Token.NOT_EQUAL_TO, "!=");
    recognizeToken(Token.SLASH, "/");
    recognizeToken(Token.AMPERSAND, "&");
  }

  @Test
  void recognizeAttributeOperators() {
    recognizeToken(Token.CARET_EQ, "^=");
    recognizeToken(Token.DOLLAR_EQ, "$=");
    recognizeToken(Token.STAR_EQ, "*=");
    recognizeToken(Token.SQUIG_EQ, "~=");
    recognizeToken(Token.WALL_EQ, "|=");
  }

  @Test
  void recognizeWhitespace() {
    recognizeToken(Token.WHITESPACE, ParseMode.SELECTOR, " ");
    recognizeToken(Token.WHITESPACE, ParseMode.SELECTOR, "  ");
    recognizeToken(Token.WHITESPACE, ParseMode.SELECTOR, "\n");
    recognizeToken(Token.WHITESPACE, ParseMode.SELECTOR, "\n ");
    recognizeToken(Token.WHITESPACE, ParseMode.SELECTOR, " \n ");
    recognizeToken(Token.WHITESPACE, ParseMode.SELECTOR, " \n\n    ");
  }

  @Test
  void recognizeHex() {
    recognizeToken(Token.HEX, ParseMode.VALUES, "#ffcc00");
    recognizeToken(Token.HEX_SHORT, ParseMode.VALUES, "#fc0");
    recognizeToken(Token.HEX_ALPHA, ParseMode.VALUES, "#ffcc2200");
  }

  @Test
  void recognizeAtTokens() {
    recognizeToken(Token.AT_FUNCTION, "@function");
    recognizeToken(Token.AT_WARN, "@warn");
    recognizeToken(Token.AT_DEBUG, "@debug");
    recognizeToken(Token.AT_ERROR, "@error");
    recognizeToken(Token.AT_PRINT, "@print");
    recognizeToken(Token.AT_IF, "@if");
    recognizeToken(Token.AT_ELSE, "@else");
    recognizeToken(Token.AT_RETURN, "@return");
    recognizeToken(Token.AT_IMPORT, "@import");
    recognizeToken(Token.AT_BREAK, "@break");
    recognizeToken(Token.AT_CONTINUE, "@continue");
    recognizeToken(Token.AT_ID, "@identifier-token");
  }

  @Test
  void testSaveState() {
    TokenStream stream = createStream("a b c d");
    StreamState state = stream.saveState();

    Token n = stream.next();
    assertEquals(Token.ID, n.type());
    assertEquals("a", n.value());

    state.close();

    n = stream.next();
    assertEquals(Token.ID, n.type());
    assertEquals("a", n.value());
  }

  private TokenStream createStream(String in) {
    StringBuffer buf = new StringBuffer(in);
    CompilerErrors errors = new CompilerErrors(buf);
    errors.setSourceName("<test-src.scss>");
    errors.setListener(error -> fail(error.getFormattedError()));

    return new TokenStream(buf, errors);
  }

  private void recognizeToken(int ttype, String input) {
    recognizeToken(ttype, ParseMode.TOKENS, input);
  }

  private void recognizeToken(int ttype, ParseMode parseMode, String input) {
    StringBuffer buf = new StringBuffer(input);

    CompilerErrors err = new CompilerErrors(buf);
    TokenStream stream = new TokenStream(buf, err);

    stream.pushMode(parseMode);

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