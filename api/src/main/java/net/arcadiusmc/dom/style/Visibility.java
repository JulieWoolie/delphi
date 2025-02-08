package net.arcadiusmc.dom.style;

public enum Visibility implements KeywordRepresentable {
  VISIBLE ("visible"),
  HIDDEN ("hidden"),
  COLLAPSE ("collapse"),
  ;

  public static final Visibility DEFAULT = VISIBLE;

  private final String keyword;

  Visibility(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
}
