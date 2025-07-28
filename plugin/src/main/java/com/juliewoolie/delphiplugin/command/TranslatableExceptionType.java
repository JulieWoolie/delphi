package com.juliewoolie.delphiplugin.command;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;

public class TranslatableExceptionType implements CommandExceptionType {

  private final String translationKey;

  public TranslatableExceptionType(String translationKey) {
    this.translationKey = translationKey;
  }

  public CommandSyntaxException create(Object... args) {
    return new CommandSyntaxException(this, createText(args));
  }

  public CommandSyntaxException createWithContext(ImmutableStringReader reader, Object... args) {
    return new CommandSyntaxException(
        this, createText(args), reader.getString(), reader.getCursor()
    );
  }

  private Message createText(Object... args) {
    if (args == null || args.length < 1) {
      return MessageComponentSerializer.message()
          .serialize(Component.translatable(translationKey));
    }

    List<Component> arguments = new ArrayList<>(args.length);

    for (Object arg : args) {
      if (arg instanceof ComponentLike like) {
        arguments.add(like.asComponent());
        continue;
      }

      String string = String.valueOf(arg);
      arguments.add(Component.text(string));
    }

    TranslatableComponent c = Component.translatable()
        .key(translationKey)
        .arguments(arguments)
        .build();

    return MessageComponentSerializer.message().serialize(c);
  }
}
