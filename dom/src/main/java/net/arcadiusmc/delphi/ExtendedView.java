package net.arcadiusmc.delphi;

import net.arcadiusmc.delphi.dom.DelphiNode;
import net.arcadiusmc.delphi.dom.Text;
import net.arcadiusmc.delphi.dom.scss.DocumentStyles.ChangeSet;

public interface ExtendedView extends DocumentView {

  void styleChanged(int dirtyBits, DelphiNode element);

  void styleUpdated(DelphiNode node, ChangeSet set);

  void sheetAdded();

  void textChanged(Text text);
}
