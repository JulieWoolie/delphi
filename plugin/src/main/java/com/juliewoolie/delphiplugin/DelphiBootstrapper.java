package com.juliewoolie.delphiplugin;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import com.juliewoolie.delphiplugin.command.DelphiCommand;
import org.jetbrains.annotations.NotNull;

public class DelphiBootstrapper implements PluginBootstrap {

  @Override
  public void bootstrap(@NotNull BootstrapContext context) {
    LocaleLoader.load(context.getDataDirectory());

    context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      try {
        LiteralCommandNode<CommandSourceStack> literal = DelphiCommand.createCommand();
        event.registrar().register(literal);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    });
  }
}
