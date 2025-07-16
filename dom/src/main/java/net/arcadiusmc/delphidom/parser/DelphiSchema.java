package net.arcadiusmc.delphidom.parser;

import org.ccil.cowan.tagsoup.Schema;

public class DelphiSchema extends Schema {

  public DelphiSchema() {
    setURI("http://delphi.arcadiusmc.net/delphi");
    setPrefix("delphi");
    elementType("<pcdata>", M_EMPTY, M_PCDATA, 0);
    elementType("<root>", M_ROOT, M_EMPTY, 0);
  }
}
