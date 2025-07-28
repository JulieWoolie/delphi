package com.juliewoolie.dom.style;

/**
 * CSS {@code flex-direction} value type
 */
public enum FlexDirection implements KeywordRepresentable {
  ROW ("row"),
  ROW_REVERSE ("row-reverse"),
  COLUMN ("column"),
  COLUMN_REVERSE ("column-reverse"),
  ;

  public static final FlexDirection DEFAULT = ROW;

  private final String keyword;

  FlexDirection(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
}
