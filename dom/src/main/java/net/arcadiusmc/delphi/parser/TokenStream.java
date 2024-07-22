package net.arcadiusmc.delphi.parser;

import java.util.Stack;

public class TokenStream {

  static final int HEX_SHORT_LENGTH = 3;
  static final int HEX_LENGTH = 6;
  static final int HEX_ALPHA_LENGTH = 8;

  static final int EOF = -1;
  static final int LF = '\n';
  static final int CR = '\r';

  private final StringBuffer input;
  private final ParserErrors errors;

  private int col = 0;
  private int lineno = 1;
  private int cursor = 0;

  private int currentChar = EOF;

  private Token peeked;
  private Location currentTokenStart;

  private final Stack<ParseMode> modeStack = new Stack<>();
  private boolean whitespaceMatters = false;

  public TokenStream(StringBuffer input, ParserErrors errors) {
    this.input = input;
    this.errors = errors;

    this.currentChar = charAt(0);
  }

  public ParserErrors errors() {
    return errors;
  }

  public void whitespaceMatters(boolean whitespaceMatters) {
    this.whitespaceMatters = whitespaceMatters;
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

  Location location() {
    return new Location(lineno, col, cursor);
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
      if (currentChar == ' ' && whitespaceMatters) {
        return;
      }

      if (Character.isWhitespace(currentChar)) {
        advance();
        continue;
      }

      if (currentChar == '/') {
        int next = charAt(cursor + 1);

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
    while (currentChar != LF && currentChar != CR) {
      advance();
    }
  }

  void skipBlockComment() {
    while (true) {
      if (currentChar == EOF) {
        Location l = location();
        errors.err(l, "Comment with no end");

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

  public Token expect(int tokenType) {
    Token n = next();

    if (n.type() != tokenType) {
      errors.fatal(
          n.location(),
          "Expected %s, found %s",
          Token.typeToString(tokenType),
          n.info()
      );
    }

    return n;
  }

  Token readToken() {
    skipIrrelevant();

    currentTokenStart = location();

    if (currentChar == EOF) {
      return token(Token.EOF);
    }

    return switch (currentChar) {
      case '{' -> singleChar(Token.SQUIG_OPEN);
      case '}' -> singleChar(Token.SQUIG_CLOSE);
      case '(' -> singleChar(Token.BRACKET_OPEN);
      case ')' -> singleChar(Token.BRACKET_CLOSE);
      case '[' -> singleChar(Token.SQUARE_OPEN);
      case ']' -> singleChar(Token.SQUARE_CLOSE);
      case '$' -> singleChar(Token.DOLLAR_SIGN);
      case ';' -> singleChar(Token.SEMICOLON);
      case '.' -> singleChar(Token.DOT);
      case ',' -> singleChar(Token.COMMA);
      case '*' -> singleChar(Token.STAR);
      case '^' -> singleChar(Token.UP_ARROW);
      case '~' -> singleChar(Token.SQUIGLY);
      case ':' -> singleChar(Token.COLON);
      case '|' -> singleChar(Token.WALL);
      case '@' -> singleChar(Token.AT);
      case '=' -> singleChar(Token.EQUALS);
      case '%' -> singleChar(Token.PERCENT);

      case ' ' -> {
        while (currentChar == ' ') {
          advance();
        }

        yield token(Token.SPACE);
      }

      case '#' -> {
        advance();

        if (!isHexNumber(currentChar) || mode() == ParseMode.TOKENS) {
          yield token(Token.HASHTAG);
        }

        StringBuffer hexSequence = new StringBuffer();
        while (isHexNumber(currentChar)) {
          hexSequence.appendCodePoint(currentChar);
          advance();
        }

        int len = hexSequence.length();
        if (len != HEX_LENGTH && len != HEX_SHORT_LENGTH && len != HEX_ALPHA_LENGTH) {
          errors.err(currentTokenStart, "Invalid hex sequence: %s", hexSequence);
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
        if (isNumberPrefix(currentChar)) {
          String number = parseNumberValue();
          yield token(Token.NUMBER, number);
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

  private boolean isNumberPrefix(int ch) {
    return isNumber(ch);
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
        errors.fatal(start, "Invalid string start");
        return "";
      }
    }

    advance();

    boolean escaped = false;
    StringBuilder builder = new StringBuilder();

    while (true) {
      if (currentChar == EOF) {
        errors.err(start, "Unclosed string");
        break;
      }
      if (currentChar == LF || currentChar == CR) {
        errors.err(location(), "Line break inside string");
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
            errors.err(location(), "Invalid escape sequence");
            continue;
          }
        };
      }

      builder.appendCodePoint(currentChar);
      advance();
    }

    return builder.toString();
  }

  private String readId() {
    if (!isIdStart(currentChar)) {
      errors.err(location(), "Invalid identifier");
      return "";
    }

    StringBuffer buf = new StringBuffer();
    boolean escaped = false;

    while (true) {
      if (currentChar == EOF) {
        break;
      }

      if (escaped) {
        buf.appendCodePoint(currentChar);
        advance();
        escaped = false;
        continue;
      }

      if (currentChar == '\\') {
        advance();
        escaped = true;
        continue;
      }

      if (!isIdPart(currentChar)) {
        break;
      }

      buf.appendCodePoint(currentChar);
      advance();
    }

    return buf.toString();
  }

  private String parseNumberValue() {
    StringBuffer result = new StringBuffer();
    boolean decimalSet = false;

    while (true) {
      if (currentChar == '.') {
        if (decimalSet) {
          errors.err(currentTokenStart, "Invalid number sequence");
          return result.toString();
        }

        decimalSet = true;
        result.append(".");
        advance();

        continue;
      }

      if (!isNumber(currentChar)) {
        break;
      }

      result.appendCodePoint(currentChar);
      advance();
    }

    if (result.charAt(result.length() - 1) == '.') {
      errors.err(location(), "Invalid number, ending in '.': %s", result);
      result.deleteCharAt(result.length() - 1);
    }

    return result.toString();
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

    int len = this.cursor - l.cursor();
    return new Token(type, l, len, value);
  }

  public enum ParseMode {
    TOKENS,
    VALUES;
  }
}
