package net.arcadiusmc.dom.style;

public enum BoxSizing implements KeywordRepresentable {
  BORDER_BOX ("border-box"),
  CONTENT_BOX ("content-box"),
  ;

  private final String keyword;

  BoxSizing(String keyword) {
    this.keyword = keyword;
  }

  @Override
  public String getKeyword() {
    return keyword;
  }
}
