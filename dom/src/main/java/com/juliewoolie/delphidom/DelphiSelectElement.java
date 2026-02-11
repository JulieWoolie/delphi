package com.juliewoolie.delphidom;

import com.juliewoolie.delphidom.event.SelectionImpl;
import com.juliewoolie.dom.ChoiceElement;
import com.juliewoolie.dom.SelectElement;
import com.juliewoolie.dom.TagNames;
import com.juliewoolie.dom.event.EventTypes;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
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

  @Override
  public void select(@Nullable String value) {
    select(null, value);
  }

  @Override
  public void select(@Nullable Player player, @Nullable String value) {
    List<ChoiceElement> choices = getChoices();
    ChoiceElement el = null;

    if (value != null) {
      for (ChoiceElement choice : choices) {
        String v = choice.getValue();
        if (v == null) {
          continue;
        }

        if (v.equals(value)) {
          el = choice;
          break;
        }
      }

      if (el != null && el.isSelected()) {
        return;
      }
    } else {
      boolean anySelected = false;
      for (ChoiceElement choice : choices) {
        if (!choice.isSelected()) {
          continue;
        }

        anySelected = true;
        break;
      }

      if (!anySelected) {
        return;
      }
    }

    SelectionImpl ev = new SelectionImpl(EventTypes.SELECT, document);
    ev.initEvent(this, true, true, player, (DelphiChoiceElement) el);

    dispatchEvent(ev);

    if (ev.isCancellable()) {
      return;
    }

    for (ChoiceElement choice : choices) {
      if (el == null) {
        choice.setSelected(false);
        continue;
      }

      choice.setSelected(el == choice);
    }
  }
}
