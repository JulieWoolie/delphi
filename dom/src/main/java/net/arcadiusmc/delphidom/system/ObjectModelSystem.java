package net.arcadiusmc.delphidom.system;

import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.ExtendedView;

public interface ObjectModelSystem {

  void onAttach(DelphiDocument document);

  void onDetach();

  default void onViewAttach(ExtendedView view) {

  }

  default void onViewDetach() {

  }
}
