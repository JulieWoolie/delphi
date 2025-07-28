package com.juliewoolie.delphidom;

import com.google.common.base.Strings;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.dom.Attributes;
import com.juliewoolie.dom.ScriptElement;
import com.juliewoolie.dom.TagNames;

public class DelphiScriptElement extends DelphiElement implements ScriptElement {

  public ResourcePath resourcePath;
  public ContentSource source;

  public DelphiScriptElement(DelphiDocument document) {
    super(document, TagNames.SCRIPT);
  }

  @Override
  public String getSource() {
    return getAttribute(Attributes.SOURCE);
  }

  @Override
  public ResourcePath getSourcePath() {
    return resourcePath;
  }

  @Override
  public boolean isDeferred() {
    String value = getAttribute(Attributes.DEFER);
    if (Strings.isNullOrEmpty(value)) {
      return false;
    }

    return Attributes.DEFER.equals(value) || value.equalsIgnoreCase("true");
  }
}
