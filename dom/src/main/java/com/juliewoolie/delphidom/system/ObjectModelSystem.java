package com.juliewoolie.delphidom.system;

import com.juliewoolie.delphidom.DelphiDocument;
import com.juliewoolie.delphidom.ExtendedView;

public interface ObjectModelSystem {

  void onAttach(DelphiDocument document);

  void onDetach();

  default void onViewAttach(ExtendedView view) {

  }

  default void onViewDetach() {

  }
}
