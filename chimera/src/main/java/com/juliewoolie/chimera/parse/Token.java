package com.juliewoolie.chimera.parse;

import com.google.common.base.Strings;
import javax.annotation.Nullable;

public record Token(
    int type,
    Location location,
    Location end,
    @Nullable String value
) {

  // Types
  public static final int UNKNOWN       =  -1;
  public static final int EOF           = 0x0;

  public static final int ID            = EOF + 1;
  public static final int STRING        = ID + 1;

  public static final int NUMBER        = STRING + 1;
  public static final int INT           = NUMBER + 1;
  public static final int HEX           = INT + 1;
  public static final int HEX_SHORT     = HEX + 1;
  public static final int HEX_ALPHA     = HEX_SHORT + 1;

  public static final int SQUIG_OPEN    = HEX_ALPHA + 1;
  public static final int SQUIG_CLOSE   = SQUIG_OPEN + 1;

  public static final int SQUARE_OPEN   = SQUIG_CLOSE + 1;
  public static final int SQUARE_CLOSE  = SQUARE_OPEN + 1;

  public static final int BRACKET_OPEN  = SQUARE_CLOSE + 1;
  public static final int BRACKET_CLOSE = BRACKET_OPEN + 1;

  public static final int COLON         = BRACKET_CLOSE + 1;
  public static final int DOUBLECOLON   = COLON + 1;
  public static final int SEMICOLON     = DOUBLECOLON + 1;
  public static final int DOLLAR_SIGN   = SEMICOLON + 1;
  public static final int DOT           = DOLLAR_SIGN + 1;
  public static final int HASHTAG       = DOT + 1;
  public static final int EQUALS        = HASHTAG + 1;
  public static final int WALL_EQ       = EQUALS + 1;
  public static final int SQUIG_EQ      = WALL_EQ + 1;
  public static final int CARET_EQ      = SQUIG_EQ + 1;
  public static final int DOLLAR_EQ     = CARET_EQ + 1;
  public static final int STAR_EQ       = DOLLAR_EQ + 1;
  public static final int WALL          = STAR_EQ + 1;
  public static final int SQUIGLY       = WALL + 1;
  public static final int UP_ARROW      = SQUIGLY + 1;
  public static final int STAR          = UP_ARROW + 1;
  public static final int AT            = STAR + 1;
  public static final int COMMA         = AT + 1;
  public static final int PERCENT       = COMMA + 1;
  public static final int EXCLAMATION   = PERCENT + 1;
  public static final int WHITESPACE    = EXCLAMATION + 1;
  public static final int PLUS          = WHITESPACE + 1;
  public static final int ANGLE_LEFT    = PLUS + 1;
  public static final int ANGLE_RIGHT   = ANGLE_LEFT + 1;
  public static final int MINUS         = ANGLE_RIGHT + 1;
  public static final int ELLIPSES      = MINUS + 1;
  public static final int LTE           = ELLIPSES + 1;
  public static final int GTE           = LTE + 1;
  public static final int EQUAL_TO      = GTE + 1;
  public static final int NOT_EQUAL_TO  = EQUAL_TO + 1;
  public static final int SLASH         = NOT_EQUAL_TO + 1;
  public static final int AMPERSAND     = SLASH + 1;

  public static final int AT_DEBUG      = AMPERSAND + 1;
  public static final int AT_PRINT      = AT_DEBUG + 1;
  public static final int AT_WARN       = AT_PRINT + 1;
  public static final int AT_ERROR      = AT_WARN + 1;
  public static final int AT_FUNCTION   = AT_ERROR + 1;
  public static final int AT_IF         = AT_FUNCTION + 1;
  public static final int AT_ELSE       = AT_IF + 1;
  public static final int AT_ID         = AT_ELSE + 1;
  public static final int AT_IMPORT     = AT_ID + 1;
  public static final int AT_BREAK      = AT_IMPORT + 1;
  public static final int AT_CONTINUE   = AT_BREAK + 1;
  public static final int AT_RETURN     = AT_CONTINUE + 1;
  public static final int AT_ASSERT     = AT_RETURN + 1;
  public static final int AT_MIXIN      = AT_ASSERT + 1;
  public static final int AT_INCLUDE    = AT_MIXIN + 1;

  public static final int LAST_TOKEN = AT_INCLUDE;

  public static String typeToString(int ttype) {
    return switch (ttype) {
      case EOF -> "end-of-input";

      case ID -> "identifier";
      case STRING -> "quoted-string";
      case NUMBER -> "number";
      case INT -> "integer";
      case HEX -> "hex-sequence";
      case HEX_SHORT -> "short-hex-sequence";
      case HEX_ALPHA -> "alpha-hex-sequence";
      case WHITESPACE -> "white-space";

      case SQUIG_OPEN -> "'{'";
      case SQUIG_CLOSE -> "'}'";
      case BRACKET_OPEN -> "'('";
      case BRACKET_CLOSE -> "')'";
      case SQUARE_OPEN -> "'['";
      case SQUARE_CLOSE -> "']'";

      case AT_DEBUG -> "'@debug'";
      case AT_PRINT -> "'@print'";
      case AT_ERROR -> "'@error'";
      case AT_WARN -> "'@warn'";
      case AT_FUNCTION -> "'@function'";
      case AT_IF -> "'@if'";
      case AT_ELSE -> "'@else'";
      case AT_IMPORT -> "'@import'";
      case AT_BREAK -> "'@break'";
      case AT_CONTINUE -> "'@continue'";
      case AT_RETURN -> "'@return'";
      case AT_ID -> "@-identifier";
      case AT_ASSERT -> "'@assert'";
      case AT_MIXIN -> "'@mixin'";
      case AT_INCLUDE -> "'@include'";

      case COLON -> "':'";
      case DOUBLECOLON -> "'::'";
      case SEMICOLON -> "';'";
      case DOLLAR_SIGN -> "'$'";
      case DOT -> "'.'";
      case HASHTAG -> "'#'";
      case EQUALS -> "'='";
      case WALL -> "'|'";
      case SQUIGLY -> "`~`";
      case UP_ARROW -> "'^'";
      case STAR -> "'*'";
      case AT -> "'@'";
      case COMMA -> "','";
      case PERCENT -> "'%'";
      case EXCLAMATION -> "'!'";
      case PLUS -> "'+'";
      case ANGLE_LEFT -> "'<'";
      case ANGLE_RIGHT -> "'>'";
      case MINUS -> "'-'";
      case ELLIPSES -> "'...'";
      case LTE -> "'<='";
      case GTE -> "'>='";
      case EQUAL_TO -> "'=='";
      case NOT_EQUAL_TO -> "'!='";
      case SLASH -> "'/'";
      case AMPERSAND -> "'&'";

      case WALL_EQ -> "'|='";
      case SQUIG_EQ -> "'~='";
      case CARET_EQ -> "'^='";
      case DOLLAR_EQ -> "'$='";
      case STAR_EQ -> "'*='";

      default -> "unknown";
    };
  }

  public String info() {
    if (Strings.isNullOrEmpty(value)) {
      return typeToString(type);
    }

    return String.format("%s(%s)", typeToString(type), value);
  }

  @Override
  public String toString() {
    return String.format("%s[type=%s, location=%s, value=%s]",
        getClass().getSimpleName(),
        typeToString(type),
        location,
        value
    );
  }
}
