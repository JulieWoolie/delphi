package com.juliewoolie.delphidom;

import com.juliewoolie.dom.ChoiceElement;
import com.juliewoolie.dom.SelectElement;
import com.juliewoolie.dom.TagNames;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelphiSelectElement extends DisableableElement implements SelectElement {

  public DelphiSelectElement(DelphiDocument document) {
    super(document, TagNames.SELECT);
  }

  @Override
  public @NotNull List<ChoiceElement> getChoices() {
    List<ChoiceElement> list = new ArrayList<>();

    for (int i = 0; i < children.size(); i++) {
      DelphiNode ch = children.get(i);

      if (!(ch instanceof ChoiceElement choice)) {
        continue;
      }

      list.addLast(choice);
    }

    return list;
  }

  @Override
  public @Nullable ChoiceElement getSelectedChoice() {
    for (int i = 0; i < children.size(); i++) {
      DelphiNode ch = children.get(i);

      if (!(ch instanceof ChoiceElement choice)) {
        continue;
      }
      if (!choice.isSelected()) {
        continue;
      }

      return choice;
    }

    return null;
  }
}
