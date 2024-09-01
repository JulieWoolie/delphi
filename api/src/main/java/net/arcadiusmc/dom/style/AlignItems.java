package net.arcadiusmc.dom.style;

public enum AlignItems implements KeywordRepresentable {
  FLEX_START ("flex-start"),
  FLEX_END ("flex-end"),
  CENTER ("center"),
  STRETCH ("stretch"),
  BASELINE ("baseline"),
  ;

  public static final AlignItems DEFAULT = AlignItems.FLEX_START;

  private final String keyword;

  AlignItems(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
}
