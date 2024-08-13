package net.arcadiusmc.delphiplugin.command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_ACCESS_DENIED;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_DOC_PARSE;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_IO_ERROR;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MISSING_PLUGINS;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_DIRECTORY_NOT_FOUND;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_ERROR;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_UNKNOWN;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_ZIP_ACCESS_DENIED;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_NO_FILE;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_SAX_PARSER_INIT;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_UNKNOWN;

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
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphiplugin.Debug;
import net.arcadiusmc.delphiplugin.DelphiPlugin;
import net.arcadiusmc.delphiplugin.PageManager;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.SessionManager;
import net.arcadiusmc.delphiplugin.resource.Modules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
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

  static final TranslatableExceptionType NO_FILE
      = new TranslatableExceptionType("delphi.error.noSuchFile");

  static final TranslatableExceptionType ACCESS_DENIED
      = new TranslatableExceptionType("delphi.error.accessDenied");

  static final TranslatableExceptionType IO_ERROR
      = new TranslatableExceptionType("delphi.error.ioError");

  static final TranslatableExceptionType MODULE_ERROR
      = new TranslatableExceptionType("delphi.error.moduleError");

  static final TranslatableExceptionType SAX_PARSER
      = new TranslatableExceptionType("delphi.error.saxParser");

  static final TranslatableExceptionType DOC_PARSE
      = new TranslatableExceptionType("delphi.error.docParseFail");

  static final TranslatableExceptionType MISSING_PLUGINS
      = new TranslatableExceptionType("delphi.error.missingPlugins");

  static final TranslatableExceptionType UNKNOWN
      = new TranslatableExceptionType("delphi.error.unknown");

  static final TranslatableExceptionType NO_MODULE_DIR
      = new TranslatableExceptionType("delphi.error.noModuleDir");

  static final TranslatableExceptionType MODULE_ACCESS_DENIED
      = new TranslatableExceptionType("delphi.error.moduleAccess");

  public static LiteralCommandNode<CommandSourceStack> createCommand() {
    LiteralArgumentBuilder<CommandSourceStack> literal = literal("delphi");

    literal.requires(stack -> stack.getSender().hasPermission(Permissions.COMMANDS));

    literal.then(open());
    literal.then(close());

    literal.then(
        literal("debug")
            .requires(stack -> stack.getSender().hasPermission(Permissions.DEBUG))
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
    return literal("close")
        .then(literal("target")
            .executes(context -> {
              PageView view = getAnyTargeted(context);
              view.close();

              context.getSource().getSender().sendMessage(
                  Component.translatable("delphi.closed.targeted", NamedTextColor.GRAY)
              );
              return SINGLE_SUCCESS;
            })
        )
        .then(literal("all")
            .then(argument("players", ArgumentTypes.players())
                .executes(c -> {
                  PlayerSelectorArgumentResolver resolver
                      = c.getArgument("players", PlayerSelectorArgumentResolver.class);

                  List<Player> players = resolver.resolve(c.getSource());
                  SessionManager manager = getPlugin().getSessions();

                  int playerCount = 0;
                  int closedCount = 0;

                  for (Player player : players) {
                    int closed = manager.endSession(player.getUniqueId());
                    if (closed < 1) {
                      continue;
                    }

                    playerCount++;
                    closedCount += closed;
                  }

                  CommandSender sender = c.getSource().getSender();

                  if (playerCount == 0) {
                    sender.sendMessage(
                        Component.translatable("delphi.closed.all.for.none", NamedTextColor.GRAY)
                    );

                    return SINGLE_SUCCESS;
                  }

                  sender.sendMessage(
                      Component.translatable(
                          "delphi.closed.all.for",
                          NamedTextColor.GRAY,
                          Component.text(playerCount),
                          Component.text(closedCount)
                      )
                  );
                  return SINGLE_SUCCESS;
                })
            )

            .executes(c -> {
              getPlugin().getSessions().closeAllSessions();

              c.getSource().getSender().sendMessage(
                  Component.translatable("delphi.closed.all", NamedTextColor.GRAY)
              );
              return SINGLE_SUCCESS;
            })
        )
        .build();
  }

  private static LiteralCommandNode<CommandSourceStack> dumpInfo() {
    return Commands.literal("dump-xml-info")
        .executes(context -> {
          PageView view = getAnyTargeted(context);
          Path path = Debug.dumpDebugTree("target-view-dump", view);

          if (path == null) {
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
          boolean state = Debug.debugOutlines;
          TranslatableComponent text;

          if (state) {
            text = Component.translatable("delphi.debug.outlineToggle.off", NamedTextColor.GRAY);
          } else {
            text = Component.translatable("delphi.debug.outlineToggle.on", NamedTextColor.YELLOW);
          }

          c.getSource().getSender().sendMessage(text);
          Debug.debugOutlines = !state;

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
    Result<DocumentView, DelphiException> result
        = pl.getManager().openDocument(path, player.getFirst());

    if (result.isSuccess()) {
      c.getSource().getSender().sendMessage(
          Component.translatable("delphi.docOpened", NamedTextColor.GRAY)
      );

      return SINGLE_SUCCESS;
    }

    throw toCommandError(result.error().get());
  }

  private static CommandSyntaxException toCommandError(DelphiException exc) {
    return switch (exc.getCode()) {
      case ERR_NO_FILE -> NO_FILE.create();
      case ERR_ACCESS_DENIED -> ACCESS_DENIED.create();
      case ERR_IO_ERROR -> IO_ERROR.create();
      case ERR_MODULE_ERROR -> MODULE_ERROR.create();
      case ERR_SAX_PARSER_INIT -> SAX_PARSER.create();
      case ERR_DOC_PARSE -> DOC_PARSE.create();
      case ERR_MISSING_PLUGINS -> MISSING_PLUGINS.create(exc.getBaseMessage());
      case ERR_UNKNOWN -> UNKNOWN.create();
      case ERR_MODULE_UNKNOWN -> PathParser.UNKNOWN_MODULE.create(exc.getBaseMessage());
      case ERR_MODULE_DIRECTORY_NOT_FOUND -> NO_MODULE_DIR.create();
      case ERR_MODULE_ZIP_ACCESS_DENIED -> MODULE_ACCESS_DENIED.create();
      default -> new CommandSyntaxException(NOP, new LiteralMessage(exc.getMessage()));
    };
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
