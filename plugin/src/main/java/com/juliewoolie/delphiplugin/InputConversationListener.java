package com.juliewoolie.delphiplugin;

import java.util.Objects;
import joptsimple.internal.Strings;
import com.juliewoolie.dom.InputElement;
import com.juliewoolie.dom.InputElement.InputType;
import com.juliewoolie.dom.event.EventListener;
import com.juliewoolie.dom.event.MouseButton;
import com.juliewoolie.dom.event.MouseEvent;
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
    player.sendMessage(Component.translatable("delphi.input.useToClear", NamedTextColor.GRAY));

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

      if (Objects.equals(input, CLEAR_CHAR)) {
        input = null;
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
