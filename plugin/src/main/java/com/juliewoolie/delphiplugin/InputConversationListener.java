package com.juliewoolie.delphiplugin;

import com.juliewoolie.dom.InputElement;
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
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class InputConversationListener implements EventListener.Typed<MouseEvent> {

  static final String CLEAR_CHAR = "-";

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

    Plugin plugin = JavaPlugin.getPlugin(DelphiPlugin.class);
    player.playSound(PageInputSystem.CLICK_SOUND);

    Dialog dialog = createDialog(player, el);
    player.showDialog(dialog);
  }

  static Component translate(Player audience, String trans) {
    return GlobalTranslator.render(
        Component.translatable(trans),
        audience.locale()
    );
  }

  static Dialog createDialog(Player player, InputElement element) {
    return Dialog.create(factory -> {
      DialogRegistryEntry.Builder builder = factory.empty();
      builder.type(
          DialogType.confirmation(
              ActionButton.builder(translate(player, "delphi.input.yes"))
                  .action(DialogAction.customClick(
                      (response, audience) -> {
                        element.setValue(response.getText("input_value"), (Player) audience);
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
                      DialogInput.text("input_value", Component.text(element.getPlaceholder()))
                          .maxLength(Integer.MAX_VALUE)
                          .initial(element.getValue())
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
