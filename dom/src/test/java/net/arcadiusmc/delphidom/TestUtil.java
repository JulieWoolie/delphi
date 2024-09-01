package net.arcadiusmc.delphidom;

import net.arcadiusmc.chimera.system.StyleSystem;

public class TestUtil {

  public static DelphiDocument createDoc() {
    DelphiDocument doc = new DelphiDocument();
    DelphiElement body = doc.createElement("body");
    doc.setBody(body);

    StyleSystem styleSystem = new StyleSystem();
    styleSystem.initialize(doc);
    doc.setStyles(styleSystem);

    return doc;
  }

}
