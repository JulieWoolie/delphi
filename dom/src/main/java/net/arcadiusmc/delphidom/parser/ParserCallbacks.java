package net.arcadiusmc.delphidom.parser;

import net.arcadiusmc.delphi.util.Nothing;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.dom.Document;

public interface ParserCallbacks {

  boolean isPluginEnabled(String pluginName);

  Result<Nothing, Exception> loadDomClass(Document document, String className);
}
