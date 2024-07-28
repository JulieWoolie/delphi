package net.arcadiusmc.delphi.dom.scss;

import net.arcadiusmc.delphi.dom.DelphiDocument;

public class DocumentSheetBuilder extends SheetBuilder {

  private final DelphiDocument document;

  public DocumentSheetBuilder(DelphiDocument document) {
    this.document = document;
  }

  @Override
  public Sheet build() {
    Sheet s = super.build();
    document.addStylesheet(s);
    return s;
  }
}
