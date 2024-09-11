package net.arcadiusmc.dom.style;

/**
 * CSS {@code display} property value type.
 */
public enum DisplayType implements KeywordRepresentable {
  NONE ("none"),
  INLINE ("inline"),
  BLOCK ("block"),
  INLINE_BLOCK ("inline-block"),
  FLEX ("flex"),
  ;

  public static final DisplayType DEFAULT = INLINE;

  private final String keyword;

  DisplayType(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
}
