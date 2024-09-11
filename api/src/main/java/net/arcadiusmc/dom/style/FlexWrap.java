package net.arcadiusmc.dom.style;

/**
 * CSS {@code flex-wrap} value type
 */
public enum FlexWrap implements KeywordRepresentable {
  NOWRAP ("nowrap"),
  WRAP ("wrap"),
  WRAP_REVERSE ("wrap-reverse"),
  ;

  public static final FlexWrap DEFAULT = NOWRAP;

  private final String keyword;

  FlexWrap(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
}
