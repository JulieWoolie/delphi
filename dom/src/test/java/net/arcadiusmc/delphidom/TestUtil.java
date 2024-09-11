package net.arcadiusmc.delphidom;

public class TestUtil {

  public static DelphiDocument createDoc() {
    DelphiDocument doc = new DelphiDocument();
    DelphiElement body = doc.createElement("body");
    doc.setBody(body);
    return doc;
  }

}
