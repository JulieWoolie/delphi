package net.arcadiusmc.chimera.parse.ast;

public enum Keyword {
  INHERIT,
  INITIAL,
  AUTO,
  UNSET,

  // Align items
  FLEX_START,
  FLEX_END,
  CENTER,
  STRETCH,
  BASELINE,

  // Display options
  NONE,
  INLINE,
  BLOCK,
  INLINE_BLOCK,
  FLEX,

  // Flex types
  ROW,
  ROW_REVERSE,
  COLUMN,
  COLUMN_REVERSE,

  // Flex wrap
  NOWRAP,
  WRAP,
  WRAP_REVERSE,

  // Justify content
  SPACE_BETWEEN,
  SPACE_AROUND,
  SPACE_EVENLY,

  // Box Sizing
  CONTENT_BOX,
  BORDER_BOX,

  // Boolean values
  TRUE,
  FALSE,
  ;

  @Override
  public String toString() {
    return name().toLowerCase().replace("_", "-");
  }

  static Keyword valueOf(boolean b) {
    return b ? TRUE : FALSE;
  }
}
