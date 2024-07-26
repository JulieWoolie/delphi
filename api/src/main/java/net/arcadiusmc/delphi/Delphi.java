package net.arcadiusmc.delphi;

import net.arcadiusmc.delphi.resource.DelphiResources;
import net.arcadiusmc.delphi.resource.PagePath;
import net.arcadiusmc.dom.ParserException;

public interface Delphi {

  PagePath parsePath(String string) throws ParserException;

  DelphiResources getResources();
}
