package net.arcadiusmc.delphiplugin.devtools;

import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.dom.Document;

public class Devtools {

  private final DocumentView target;
  private final Document document;

  private DevToolTab tab = DevToolTab.INSPECT_ELEMENT;

  public Devtools(DocumentView targetView, Document devtoolsDocument) {
    this.target = targetView;
    this.document = devtoolsDocument;
  }
}
