package net.arcadiusmc.delphiplugin.command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.argument;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphiplugin.DelphiPlugin;
import net.arcadiusmc.delphiplugin.PageManager;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.render.RenderTreePrint;
import net.arcadiusmc.delphiplugin.resource.Modules;
import net.arcadiusmc.dom.Visitor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class DelphiCommand {

  static final CommandExceptionType NOP = new CommandExceptionType() {};

  static final TranslatableExceptionType ONLY_PLAYERS
      = new TranslatableExceptionType("permissions.requires.player");

  static final TranslatableExceptionType NONE_TARGETED
      = new TranslatableExceptionType("delphi.error.noTarget");

  static final TranslatableExceptionType DUMP_FAIL
      = new TranslatableExceptionType("delphi.error.debugDumpFail");

  public static LiteralCommandNode<CommandSourceStack> createCommand() {
    LiteralArgumentBuilder<CommandSourceStack> literal = Commands.literal("delphi");
    literal.then(open());
    literal.then(close());

    literal.then(
        Commands.literal("debug")
            .then(dumpInfo())
            .then(toggleDebugLines())
            .build()
    );

    return literal.build();
  }

  private static PageView getAnyTargeted(CommandContext<CommandSourceStack> context)
      throws CommandSyntaxException
  {
    if (!(context.getSource().getSender() instanceof Player player)) {
      throw ONLY_PLAYERS.create();
    }

    PageManager pages = getPlugin().getManager();
    Optional<DocumentView> opt = pages.getAnyTargetedView(player);

    if (opt.isEmpty()) {
      throw NONE_TARGETED.create();
    }

    return (PageView) opt.get();
  }

  private static LiteralCommandNode<CommandSourceStack> close() {
    return Commands.literal("close")
        .executes(context -> {
          PageView view = getAnyTargeted(context);
          view.close();

          context.getSource().getSender().sendMessage(
              Component.translatable("delphi.closed", NamedTextColor.GRAY)
          );
          return SINGLE_SUCCESS;
        })
        .build();
  }

  private static LiteralCommandNode<CommandSourceStack> dumpInfo() {
    return Commands.literal("dump-xml-info")
        .executes(context -> {
          PageView view = getAnyTargeted(context);

          RenderTreePrint print = new RenderTreePrint(view);
          print.appendDocumentInfo();
          Visitor.visit(view.getDocument().getBody(), print);

          Path path = getPlugin().getDataPath().resolve("dump.xml");
          String string = print.toString();

          try {
            Files.writeString(path, string, StandardCharsets.UTF_8);
          } catch (IOException exc) {
            Loggers.getDocumentLogger().error("Failed to dump XML info", exc);
            throw DUMP_FAIL.create();
          }

          context.getSource().getSender().sendMessage(
              Component.translatable(
                  "delphi.debug.dumpedXml",
                  NamedTextColor.GRAY,
                  Component.text(path.toString())
              )
          );
          return SINGLE_SUCCESS;
        })
        .build();
  }

  private static LiteralCommandNode<CommandSourceStack> toggleDebugLines() {
    return Commands.literal("toggle-debug-outlines")
        .executes(c -> {
          boolean state = PageView.debugOutlines;
          String msg;
          TextColor color;

          if (state) {
            msg = "delphi.debug.outlineToggle.off";
            color = NamedTextColor.GRAY;
          } else {
            msg = "delphi.debug.outlineToggle.on";
            color = NamedTextColor.YELLOW;
          }

          c.getSource().getSender().sendMessage(Component.translatable(msg, color));
          PageView.debugOutlines = !state;

          return SINGLE_SUCCESS;
        })
        .build();
  }

  private static LiteralCommandNode<CommandSourceStack> open() {
    return Commands.literal("open")
        .then(argument("player", ArgumentTypes.player())
            .then(argument("path", new PathType())
                .executes(DelphiCommand::openDocument)
            )
        )
        .build();
  }

  private static int openDocument(CommandContext<CommandSourceStack> c)
      throws CommandSyntaxException
  {
    DelphiPlugin pl = getPlugin();
    ResourcePath path = getPath(c);

    PlayerSelectorArgumentResolver resolver
        = c.getArgument("player", PlayerSelectorArgumentResolver.class);

    List<Player> player = resolver.resolve(c.getSource());
    Result<DocumentView, String> result = pl.getManager().openDocument(path, player.getFirst());

    if (result.isSuccess()) {
      c.getSource().getSender().sendMessage(
          Component.translatable("delphi.docOpened", NamedTextColor.GRAY)
      );

      return SINGLE_SUCCESS;
    }

    throw new CommandSyntaxException(NOP, new LiteralMessage(result.error().orElse("")));
  }

  private static DelphiPlugin getPlugin() {
    return JavaPlugin.getPlugin(DelphiPlugin.class);
  }

  private static ResourcePath getPath(CommandContext<CommandSourceStack> context) {
    return context.getArgument("path", ResourcePath.class);
  }

  static class PathType implements CustomArgumentType<ResourcePath, String> {

    private Modules getModules() {
      ClassLoader loader = DelphiPlugin.class.getClassLoader();

      if (!(loader instanceof ConfiguredPluginClassLoader pluginLoader)) {
        return null;
      }

      JavaPlugin plugin = pluginLoader.getPlugin();
      if (!(plugin instanceof DelphiPlugin delphi)) {
        return null;
      }

      return delphi.getModules();
    }

    @Override
    public @NotNull ResourcePath parse(@NotNull StringReader reader) throws CommandSyntaxException {
      DelphiPlugin plugin = DelphiPlugin.getPlugin(DelphiPlugin.class);

      PathParser<?> parser = new PathParser<>(getModules(), reader);
      parser.parse();
      return parser.getPath();
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
      return StringArgumentType.greedyString();
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(
        @NotNull CommandContext<S> context,
        @NotNull SuggestionsBuilder builder
    ) {
      StringReader reader = new StringReader(builder.getInput());
      reader.setCursor(builder.getStart());

      PathParser<S> parser = new PathParser<>(getModules(), reader);

      try {
        parser.parse();
      } catch (CommandSyntaxException exc) {
        // Ignored
      }

      return parser.getSuggestions(context, builder);
    }
  }
}
