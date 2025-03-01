package net.arcadiusmc.delphiplugin;

import joptsimple.internal.Strings;
import net.arcadiusmc.dom.InputElement;
import net.arcadiusmc.dom.InputElement.InputType;
import net.arcadiusmc.dom.event.EventListener;
import net.arcadiusmc.dom.event.MouseButton;
import net.arcadiusmc.dom.event.MouseEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InputConversationListener implements EventListener.Typed<MouseEvent> {

  @Override
  public void handleEvent(MouseEvent event) {
    if (!(event.getTarget() instanceof InputElement el)) {
      return;
    }
    if (el.isDisabled()) {
      return;
    }

    Player player = event.getPlayer();
    if (event.getButton() != MouseButton.LEFT) {
      return;
    }

    Plugin plugin = JavaPlugin.getPlugin(DelphiPlugin.class);

    Conversation conversation = new Conversation(plugin, player, new DelphiPrompt(el));
    player.beginConversation(conversation);
  }

  static class DelphiPrompt implements Prompt {

    final InputElement inputElement;

    public DelphiPrompt(InputElement inputElement) {
      this.inputElement = inputElement;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
      return inputElement.getPlaceholder();
    }

    @Override
    public boolean blocksForInput(@NotNull ConversationContext context) {
      return true;
    }

    @Override
    public @Nullable Prompt acceptInput(
        @NotNull ConversationContext context,
        @Nullable String input
    ) {
      if (inputElement.isDisabled()) {
        return Prompt.END_OF_CONVERSATION;
      }

      InputType type = inputElement.getType();
      Player player = (Player) context.getForWhom();

      if (type == InputType.NUMBER && !Strings.isNullOrEmpty(input)) {
        try {
          Double.parseDouble(input);
        } catch (NumberFormatException exc) {
          player.sendMessage(
              Component.translatable("delphi.inputError.invalidNumber")
                  .color(NamedTextColor.RED)
                  .arguments(Component.text(input))
          );

          return Prompt.END_OF_CONVERSATION;
        }
      }

      inputElement.setValue(input, player);
      return Prompt.END_OF_CONVERSATION;
    }
  }
}
