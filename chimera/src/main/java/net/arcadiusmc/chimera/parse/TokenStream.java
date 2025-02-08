package net.arcadiusmc.chimera.parse;

import java.util.Stack;
import lombok.Getter;

public class TokenStream {

  static final int HEX_SHORT_LENGTH = 3;
  static final int HEX_LENGTH = 6;
  static final int HEX_ALPHA_LENGTH = 8;

  static final int EOF = -1;
  static final int LF = '\n';
  static final int CR = '\r';

  @Getter
  private final StringBuffer input;
  private final CompilerErrors errors;

  private int col = 0;
  private int lineno = 1;
  private int cursor = 0;

  private int currentChar = EOF;

  private Token peeked;
  private Location currentTokenStart;

  private final Stack<ParseMode> modeStack = new Stack<>();

  public TokenStream(StringBuffer input, CompilerErrors errors) {
    this.input = input;
    this.errors = errors;

    this.currentChar = charAt(0);
  }

  public StreamState saveState() {
    return new StreamState(this, cursor, col, lineno, peeked, currentTokenStart);
  }

  public void restoreState(StreamState state) {
    this.cursor = state.cursor();
    this.col = state.col();
    this.lineno = state.lineno();
    this.peeked = state.peeked();
    this.currentTokenStart = state.lastTokenStart();
    this.currentChar = charAt(cursor);
  }

  public CompilerErrors errors() {
    return errors;
  }

  ParseMode mode() {
    if (modeStack.isEmpty()) {
      return ParseMode.TOKENS;
    }

    return modeStack.peek();
  }

  public void pushMode(ParseMode parseMode) {
    modeStack.push(parseMode);
  }

  public ParseMode popMode() {
    return modeStack.pop();
  }

  public Location location() {
    return new Location(lineno, col, cursor);
  }

  int ahead() {
    return ahead(1);
  }

  int ahead(int off) {
    return charAt(cursor + off);
  }

  void skip(String sequence) {
    skip(sequence.length());
  }

  void skip(int chars) {
    for (int i = 0; i < chars; i++) {
      advance();
    }
  }

  void advance() {
    int nCursor = cursor + 1;

    if (nCursor >= input.length()) {
      currentChar = EOF;
      cursor = nCursor;
      return;
    }

    int nChar = charAt(nCursor);

    if (nChar == LF || nChar == CR) {
      lineno++;
      col = 0;

      if (nChar == CR && charAt(nCursor + 1) == LF) {
        nCursor++;
      }

      // Normalize all line breaks to LF
      nChar = LF;
    } else {
      col++;
    }

    cursor = nCursor;
    currentChar = nChar;
  }

  int charAt(int index) {
    if (index < 0 || index >= input.length()) {
      return EOF;
    }

    return input.charAt(index);
  }

  void skipIrrelevant() {
    while (currentChar != EOF) {
      if (Character.isWhitespace(currentChar)) {
        if (mode() == ParseMode.SELECTOR) {
          return;
        }

        advance();
        continue;
      }

      if (currentChar == '/') {
        int next = ahead();

        if (next == '*') {
          skipBlockComment();
          continue;
        }

        if (next == '/') {
          skipLineComment();
          continue;
        }

        break;
      }

      break;
    }
  }

  void skipLineComment() {
    while (currentChar != LF && currentChar != CR && currentChar != EOF) {
      advance();
    }
  }

  void skipBlockComment() {
    while (true) {
      if (currentChar == EOF) {
        Location l = location();
        errors.error(l, "Comment with no end");

        return;
      }

      if (currentChar == '*') {
        advance();

        if (currentChar == '/') {
          advance();
          break;
        }

        continue;
      }

      advance();
    }
  }

  public boolean hasNext() {
    return peek().type() != Token.EOF;
  }

  public Token next() {
    if (peeked != null) {
      Token p = peeked;
      peeked = null;
      return p;
    }

    return readToken();
  }

  public Token peek() {
    if (peeked != null) {
      return peeked;
    }

    return peeked = readToken();
  }

  Token readToken() {
    skipIrrelevant();

    currentTokenStart = location();

    if (currentChar == EOF) {
      return token(Token.EOF);
    }

    if (Character.isWhitespace(currentChar)) {
      while (Character.isWhitespace(currentChar)) {
        advance();
      }

      return token(Token.WHITESPACE);
    }

    return switch (currentChar) {
      case '{' -> singleChar(Token.SQUIG_OPEN);
      case '}' -> singleChar(Token.SQUIG_CLOSE);
      case '(' -> singleChar(Token.BRACKET_OPEN);
      case ')' -> singleChar(Token.BRACKET_CLOSE);
      case '[' -> singleChar(Token.SQUARE_OPEN);
      case ']' -> singleChar(Token.SQUARE_CLOSE);

      case '<' -> singleOrEq(Token.ANGLE_LEFT, Token.LTE);
      case '>' -> singleOrEq(Token.ANGLE_RIGHT, Token.GTE);
      case '=' -> singleOrEq(Token.EQUALS, Token.EQUAL_TO);
      case '!' -> singleOrEq(Token.EXCLAMATION, Token.NOT_EQUAL_TO);
      case '$' -> singleOrEq(Token.DOLLAR_SIGN, Token.DOLLAR_EQ);
      case '*' -> singleOrEq(Token.STAR, Token.STAR_EQ);
      case '^' -> singleOrEq(Token.UP_ARROW, Token.CARET_EQ);
      case '~' -> singleOrEq(Token.SQUIGLY, Token.SQUIG_EQ);
      case '|' -> singleOrEq(Token.WALL, Token.WALL_EQ);

      case ';' -> singleChar(Token.SEMICOLON);
      case ',' -> singleChar(Token.COMMA);
      case ':' -> singleChar(Token.COLON);
      case '%' -> singleChar(Token.PERCENT);
      case '/' -> singleChar(Token.SLASH);
      case '+' -> singleChar(Token.PLUS);
      case '&' -> singleChar(Token.AMPERSAND);

      case '@' -> {
        advance();

        if (isIdStart(currentChar)) {
          String id = readId();

          yield switch (id.toLowerCase()) {
            case "if" -> token(Token.AT_IF);
            case "else" -> token(Token.AT_ELSE);
            case "function" -> token(Token.AT_FUNCTION);
            case "print" -> token(Token.AT_PRINT);
            case "debug" -> token(Token.AT_DEBUG);
            case "error" -> token(Token.AT_ERROR);
            case "warn" -> token(Token.AT_WARN);
            case "return" -> token(Token.AT_RETURN);
            case "import" -> token(Token.AT_IMPORT);
            case "break" -> token(Token.AT_BREAK);
            case "continue" -> token(Token.AT_CONTINUE);
            case "assert" -> token(Token.AT_ASSERT);
            case "include" -> token(Token.AT_INCLUDE);
            case "mixin" -> token(Token.AT_MIXIN);
            default -> token(Token.AT_ID, id);
          };
        }

        yield token(Token.AT);
      }

      case '#' -> {
        advance();

        ParseMode mode = mode();
        if (!isHexNumber(currentChar) || mode != ParseMode.VALUES) {
          yield token(Token.HASHTAG);
        }

        StringBuffer hexSequence = new StringBuffer();
        while (isHexNumber(currentChar)) {
          hexSequence.appendCodePoint(currentChar);
          advance();
        }

        int len = hexSequence.length();
        if (len != HEX_LENGTH && len != HEX_SHORT_LENGTH && len != HEX_ALPHA_LENGTH) {
          errors.error(currentTokenStart, "Invalid hex sequence: %s", hexSequence);
        }

        int ttype;

        if (len == HEX_LENGTH) {
          ttype = Token.HEX;
        } else if (len == HEX_ALPHA_LENGTH) {
          ttype = Token.HEX_ALPHA;
        } else {
          ttype = Token.HEX_SHORT;
        }

        yield token(ttype, hexSequence.toString());
      }

      case '\'', '"', '`' -> {
        String str = readString();
        yield token(Token.STRING, str);
      }

      default -> {
        if (isNumberStart()) {
          yield parseNumberValue();
        }

        // Dot has to be checked after, it could be a number decimal
        if (currentChar == '.') {
          advance();

          if (currentChar == '.') {
            advance();

            if (currentChar == '.') {
              advance();
              yield token(Token.ELLIPSES);
            }

            yield token(Token.UNKNOWN, "..");
          }

          yield token(Token.DOT);
        }
        if (currentChar == '-') {
          yield singleChar(Token.MINUS);
        }

        if (isIdStart(currentChar)) {
          String id = readId();
          yield token(Token.ID, id);
        }

        Token t = token(Token.UNKNOWN, Character.toString(currentChar));
        advance();

        yield t;
      }
    };
  }

  private boolean isHexNumber(int ch) {
    return (ch >= '0' && ch <= '9')
        || (ch >= 'A' && ch <= 'F')
        || (ch >= 'a' && ch <= 'f');
  }

  private boolean isNumberStart() {
    int ahead1 = charAt(cursor + 1);
    int ahead2 = charAt(cursor + 2);

    if (currentChar == '-') {
      if (isNumber(ahead1)) {
        return true;
      }

      if (ahead1 != '.') {
        return false;
      }

      return isNumber(ahead2);
    }

    if (currentChar == '.') {
      return isNumber(ahead1);
    }

    return isNumber(currentChar);
  }

  private boolean isNumber(int ch) {
    return ch >= '0' && ch <= '9';
  }

  private boolean isIdStart(int ch) {
    return (ch >= 'a' && ch <= 'z')
        || (ch >= 'A' && ch <= 'Z')
        || ch == '_'
        || ch == '-'
        || ch == '\\';
  }

  private boolean isIdPart(int ch) {
    return isIdStart(ch) || (ch >= '0' && ch <= '9');
  }

  private String readString() {
    int quote;
    Location start = location();

    switch (currentChar) {
      case '\'' -> quote = '\'';
      case '"' -> quote = '"';
      case '`' -> quote = '`';

      default -> {
        errors.error(start, "Invalid string start");
        return "";
      }
    }

    advance();

    boolean escaped = false;
    StringBuilder builder = new StringBuilder();

    while (true) {
      if (currentChar == EOF) {
        errors.error(start, "Unclosed string");
        break;
      }
      if (currentChar == LF || currentChar == CR) {
        errors.error(location(), "Line break inside string");
        break;
      }

      if (currentChar == quote) {
        advance();

        if (escaped) {
          builder.appendCodePoint(quote);
          escaped = false;
          continue;
        }

        break;
      }

      if (currentChar == '\\') {
        advance();

        if (escaped) {
          builder.append("\\");
          escaped = false;
          continue;
        }

        escaped = true;
        continue;
      }

      if (escaped) {
        escaped = false;
        int ch = currentChar;

        advance();

        switch (ch) {
          case 't', 'T' -> builder.append("\t");
          case 'n', 'N' -> builder.append("\n");
          case 'r', 'R' -> builder.append("\r");

          default -> {
            errors.error(location(), "Invalid escape sequence");
            continue;
          }
        };
      }

      builder.appendCodePoint(currentChar);
      advance();
    }

    return builder.toString();
  }

  private boolean isValidEscape() {
    if (currentChar != '\\') {
      return false;
    }

    int ahead = ahead();
    return ahead != LF && ahead != CR && ahead != EOF;
  }

  private String readId() {
    StringBuffer buf = new StringBuffer();

    while (true) {
      if (currentChar == EOF) {
        break;
      }

      if (isValidEscape()) {
        // Skip the '\' character
        advance();
      } else if (!isIdPart(currentChar)) {
        break;
      }

      buf.appendCodePoint(currentChar);
      advance();
    }

    return buf.toString();
  }

  private Token parseNumberValue() {
    StringBuilder repr = new StringBuilder();
    int ttype = Token.INT;

    if (currentChar == '-') {
      repr.append('-');
      advance();
    }

    while (isNumber(currentChar)) {
      repr.appendCodePoint(currentChar);
      advance();
    }

    if (currentChar == '.' && isNumber(charAt(cursor + 1))) {
      repr.append('.');
      advance();
      ttype = Token.NUMBER;

      while (isNumber(currentChar)) {
        repr.appendCodePoint(currentChar);
        advance();
      }
    }

    if (currentChar == 'e' || currentChar == 'E') {
      int ahead = charAt(cursor + 1);

      if (ahead == '+' || ahead == '-') {
        ahead = charAt(cursor + 2);
      }

      if (isNumber(ahead)) {
        ttype = Token.NUMBER;
        advance();

        if (currentChar == '+' || currentChar == '-') {
          repr.appendCodePoint(currentChar);
          advance();
        }

        while (isNumber(currentChar)) {
          repr.appendCodePoint(currentChar);
          advance();
        }
      }
    }

    return token(ttype, repr.toString());
  }

  private Token singleOrEq(int type, int eqType) {
    advance();

    if (currentChar == '=') {
      advance();
      return token(eqType);
    }

    return token(type);
  }

  private Token singleChar(int type) {
    advance();
    return token(type);
  }

  private Token token(int type) {
    return token(type, null);
  }

  private Token token(int type, String value) {
    Location l;

    if (currentTokenStart == null) {
      l = location();
    } else {
      l = currentTokenStart;
    }

    return new Token(type, l, location(), value);
  }

  public enum ParseMode {
    TOKENS,
    SELECTOR,
    VALUES;
  }
}
