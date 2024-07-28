package net.arcadiusmc.delphidom;

import java.util.Map;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphidom.scss.DocumentStyles.ChangeSet;

public interface ExtendedView extends DocumentView {

  void styleChanged(int dirtyBits, DelphiNode element);

  void styleUpdated(DelphiNode node, ChangeSet set);

  void sheetAdded(ChangeSet changed);

  void textChanged(Text text);

  Map<String, Object> getStyleVariables();
}
