package net.arcadiusmc.delphidom.parser;

import net.arcadiusmc.dom.OptionElement;

public interface SaxParserCallbacks {

  void validateOptionDeclaration(OptionElement opt) throws PluginMissingException;
}
