package net.arcadiusmc.delphi;

import java.util.Map;
import net.arcadiusmc.delphi.dom.DelphiNode;
import net.arcadiusmc.delphi.dom.Text;
import net.arcadiusmc.delphi.dom.scss.DocumentStyles.ChangeSet;

public interface ExtendedView extends DocumentView {

  void styleChanged(int dirtyBits, DelphiNode element);

  void styleUpdated(DelphiNode node, ChangeSet set);

  void sheetAdded(ChangeSet changed);

  void textChanged(Text text);

  Map<String, Object> getStyleVariables();
}
