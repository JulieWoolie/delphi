package com.juliewoolie.dom.style;

public enum VerticalAlign implements KeywordRepresentable {
  SUB ("sub"),
  SUPER ("super"),
  TOP ("top"),
  MIDDLE ("middle"),
  BOTTOM ("bottom"),
  ;

  public static final VerticalAlign DEFAULT = BOTTOM;

  private final String keyword;

  VerticalAlign(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
}
