package net.arcadiusmc.delphidom;

import java.util.Map;
import net.arcadiusmc.delphi.DocumentView;

public interface ExtendedView extends DocumentView {

  void styleUpdated(DelphiNode node, int changes);

  void contentChanged(DelphiNode text);

  Map<String, Object> getStyleVariables();

  void removeRenderElement(DelphiElement element);

  void titleChanged(DelphiElement element, DelphiNode old, DelphiNode titleNode);
}
