package net.arcadiusmc.dom.style;

/**
 * CSS {@code justify-content} value type
 */
public enum JustifyContent implements KeywordRepresentable {
  FLEX_START ("flex-start"),
  FLEX_END ("flex-end"),
  CENTER ("center"),
  SPACE_BETWEEN ("space-between"),
  SPACE_AROUND ("space-around"),
  SPACE_EVENLY ("space-evenly")
  ;

  public static final JustifyContent DEFAULT = FLEX_START;

  private final String keyword;

  JustifyContent(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
}
