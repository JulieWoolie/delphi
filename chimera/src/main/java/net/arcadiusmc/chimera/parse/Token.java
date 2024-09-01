package net.arcadiusmc.chimera.parse;

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
  public static final int SEMICOLON     = COLON + 1;
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

  public static String typeToString(int ttype) {
    return switch (ttype) {
      case EOF -> "end-of-input";
      case ID -> "identifier";
      case NUMBER -> "number";
      case HEX -> "hex-sequence";
      case HEX_SHORT -> "short-hex-sequence";
      case HEX_ALPHA -> "alpha-hex-sequence";
      case SQUIG_OPEN -> "'{'";
      case SQUIG_CLOSE -> "'}'";
      case BRACKET_OPEN -> "'('";
      case BRACKET_CLOSE -> "')'";
      case SQUARE_OPEN -> "'['";
      case SQUARE_CLOSE -> "']'";
      case COLON -> "':'";
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
      case WHITESPACE -> "white-space";
      case EXCLAMATION -> "'!'";
      case PLUS -> "'+'";
      case ANGLE_LEFT -> "'<'";
      case ANGLE_RIGHT -> "'>'";
      case MINUS -> "'-'";
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
