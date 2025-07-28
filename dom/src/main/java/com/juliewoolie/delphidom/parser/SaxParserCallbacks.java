package com.juliewoolie.delphidom.parser;

import com.juliewoolie.delphi.resource.DelphiException;
import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.dom.OptionElement;

public interface SaxParserCallbacks {

  void validateOptionDeclaration(OptionElement opt) throws DelphiException;

  void onDocumentCreated(DelphiDocument document);
}
