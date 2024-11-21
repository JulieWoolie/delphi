package net.arcadiusmc.delphidom;

import net.arcadiusmc.delphi.DocumentView;

public interface ExtendedView extends DocumentView {

  void contentChanged(DelphiNode text);

  void removeRenderElement(DelphiElement element);

  void tooltipChanged(DelphiElement element, DelphiNode old, DelphiNode titleNode);
}
