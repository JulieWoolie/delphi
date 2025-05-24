package net.arcadiusmc.delphidom;

import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.dom.Attributes;
import net.arcadiusmc.dom.ScriptElement;
import net.arcadiusmc.dom.TagNames;

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
}
