package com.juliewoolie.delphiplugin;

import static com.juliewoolie.delphiplugin.TextUtil.translate;

import com.google.common.base.Strings;
import com.juliewoolie.dom.InputElement;
import com.juliewoolie.dom.InputElement.InputType;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.MouseButton;
import com.juliewoolie.dom.event.MouseEvent;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class InputConversationListener implements EventListener.Typed<MouseEvent> {

  @Override
  public void handleEvent(MouseEvent event) {
    if (!(event.getTarget() instanceof InputElement el)) {
      return;
    }

    Player player = event.getPlayer();

    if (el.isDisabled()) {
      player.playSound(PageInputSystem.DISABLED_BUTTON_SOUND);
      return;
    }

    if (event.getButton() != MouseButton.LEFT) {
      return;
    }

    Dialog dialog = createDialog(player, el);

    player.playSound(PageInputSystem.CLICK_SOUND);
    player.showDialog(dialog);
  }

  static void submitInput(Player player, InputElement element, String input) {
    if (element.getType() == InputType.NUMBER && !Strings.isNullOrEmpty(input)) {
      try {
        Double.parseDouble(input);
      } catch (NumberFormatException exc) {
        player.sendMessage(
            Component.translatable("delphi.input.invalidNumber")
                .color(NamedTextColor.RED)
                .arguments(Component.text(input))
        );
        return;
      }
    }

    element.setValue(input, player);
  }

  static String getInputLabel(InputElement el) {
    if (Strings.isNullOrEmpty(el.getPrompt())) {
      return el.getPlaceholder();
    }
    return el.getPrompt();
  }

  static Dialog createDialog(Player player, InputElement element) {
    return Dialog.create(factory -> {
      DialogRegistryEntry.Builder builder = factory.empty();
      builder.type(
          DialogType.confirmation(
              ActionButton.builder(translate(player, "delphi.input.yes"))
                  .action(DialogAction.customClick(
                      (response, audience) -> {
                        submitInput(
                            (Player) audience,
                            element,
                            response.getText("input_value")
                        );
                      },
                      Options.builder().build()
                  ))
                  .build(),
              ActionButton.builder(translate(player,"delphi.input.no"))
                  .build()
          )
      );
      builder.base(
          DialogBase.builder(translate(player,"delphi.input.title"))
              .inputs(
                  List.of(
                      DialogInput.text("input_value", Component.text(getInputLabel(element)))
                          .maxLength(Integer.MAX_VALUE)
                          .initial(Strings.nullToEmpty(element.getValue()))
                          .width(350)
                          .build()
                  )
              )
              .afterAction(DialogAfterAction.CLOSE)
              .canCloseWithEscape(true)
              .build()
      );
    });
  }
}
