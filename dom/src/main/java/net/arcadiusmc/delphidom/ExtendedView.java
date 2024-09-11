package net.arcadiusmc.delphidom;

import net.arcadiusmc.delphi.DocumentView;

public interface ExtendedView extends DocumentView {

  void contentChanged(DelphiNode text);

  void removeRenderElement(DelphiElement element);

  void titleChanged(DelphiElement element, DelphiNode old, DelphiNode titleNode);
}
