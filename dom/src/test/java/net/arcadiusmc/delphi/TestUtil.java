package net.arcadiusmc.delphi;

import net.arcadiusmc.delphi.dom.DelphiDocument;
import net.arcadiusmc.delphi.dom.DelphiElement;

public class TestUtil {

  public static DelphiDocument createDoc() {
    DelphiDocument doc = new DelphiDocument();
    DelphiElement body = doc.createElement("body");
    doc.setBody(body);
    return doc;
  }

}
