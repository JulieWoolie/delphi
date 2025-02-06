package net.arcadiusmc.delphidom.parser;

import net.arcadiusmc.delphi.util.Nothing;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.dom.ComponentElement;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.ItemElement;

public interface ParserCallbacks {

  boolean isPluginEnabled(String pluginName);

  Result<Nothing, Exception> loadDomClass(Document document, String className);

  ElementInputConsumer<ItemElement> createItemJsonParser();

  ElementInputConsumer<ComponentElement> createTextJsonParser();
}
