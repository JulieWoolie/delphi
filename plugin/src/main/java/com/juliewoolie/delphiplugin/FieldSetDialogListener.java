package com.juliewoolie.delphiplugin;

import static com.juliewoolie.delphiplugin.InputConversationListener.getInputLabel;
import static com.juliewoolie.delphiplugin.TextUtil.translate;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.juliewoolie.delphidom.DelphiFieldSetElement;
import com.juliewoolie.dom.Disableable;
import com.juliewoolie.dom.Element;
import com.juliewoolie.dom.FieldSetElement;
import com.juliewoolie.dom.InputElement;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.MouseEvent;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry.Builder;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback.Options;
import org.bukkit.entity.Player;

public class FieldSetDialogListener implements EventListener.Typed<MouseEvent> {

  @Override
  public void handleEvent(MouseEvent event) {
    Element target = event.getTarget();
    if (!DelphiFieldSetElement.isInputElement(target)) {
      return;
    }

    FieldSetElement fieldset = DelphiFieldSetElement.getFieldSetParent(target);
    if (fieldset == null) {
      return;
    }

    List<Element> fieldSetElements = fieldset.getFieldSetElements();
    if (fieldSetElements.isEmpty()) {
      return;
    }

    HashBiMap<String, Element> keyMap = HashBiMap.create();

    for (Element fieldSetElement : fieldSetElements) {
      if (isDisabled(fieldSetElement)) {
        continue;
      }

      keyMap.put(keyMap.size() + "", fieldSetElement);
    }

    Player player = event.getPlayer();

    FieldSetContext ctx = new FieldSetContext(keyMap, fieldSetElements);
    Dialog dialog = createDialog(player, ctx);

    player.showDialog(dialog);
  }

  static boolean isDisabled(Element element) {
    if (!(element instanceof Disableable dis)) {
      return false;
    }
    return dis.isDisabled();
  }

  static Dialog createDialog(Player player, FieldSetContext ctx) {
    return Dialog.create(factory -> {
      Builder builder = factory.empty();

      builder.type(
          DialogType.confirmation(
              ActionButton.builder(translate(player, "delphi.input.yes"))
                  .action(DialogAction.customClick(
                      new FieldSetCallback(ctx),
                      Options.builder().build()
                  ))
                  .build(),
              ActionButton.builder(translate(player,"delphi.input.no"))
                  .build()
          )
      );

      List<DialogInput> inputs = new ArrayList<>();
      BiMap<Element, String> inv = ctx.keyMap.inverse();

      for (Element element : ctx.elementList) {
        DialogInput input;

        if (element instanceof InputElement inp) {
          input = DialogInput.text(inv.get(inp), Component.text(getInputLabel(inp)))
              .maxLength(Integer.MAX_VALUE)
              .initial(Strings.nullToEmpty(inp.getValue()))
              .width(350)
              .build();
        } else {
          continue;
        }

        inputs.addLast(input);
      }

      builder.base(
          DialogBase.builder(translate(player,"delphi.input.title"))
              .inputs(inputs)
              .afterAction(DialogAfterAction.CLOSE)
              .canCloseWithEscape(true)
              .build()
      );
    });
  }

  record FieldSetCallback(FieldSetContext ctx) implements DialogActionCallback {

    @Override
    public void accept(DialogResponseView response, Audience audience) {
      Player player = (Player) audience;

      for (Entry<String, Element> entry : ctx.keyMap.entrySet()) {
        String key = entry.getKey();
        Element value = entry.getValue();

        if (isDisabled(value)) {
          continue;
        }

        if (value instanceof InputElement inp) {
          String str = Strings.nullToEmpty(response.getText(key));
          inp.setValue(str, player);
        }
      }
    }
  }

  record FieldSetContext(BiMap<String, Element> keyMap, List<Element> elementList) {

  }
}
