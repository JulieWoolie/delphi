package net.arcadiusmc.delphiplugin.command;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_ACCESS_DENIED;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_DOC_PARSE;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_ILLEGAL_INSTANCE_NAME;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_INSTANCE_NAME_USED;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_IO_ERROR;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MISSING_PLUGINS;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_DIRECTORY_NOT_FOUND;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_ERROR;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_UNKNOWN;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_ZIP_ACCESS_DENIED;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_NO_FILE;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_SAX_PARSER_INIT;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_UNKNOWN;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
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
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.arcadiusmc.delphi.DelphiProvider;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.DocumentViewBuilder;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphiplugin.Debug;
import net.arcadiusmc.delphiplugin.DelphiImpl;
import net.arcadiusmc.delphiplugin.DelphiPlugin;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.ViewManager;
import net.arcadiusmc.delphiplugin.resource.PluginResources;
import net.arcadiusmc.dom.Element;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class DelphiCommand {

  static final Component PREFIX = MiniMessage.miniMessage()
      .deserialize("<dark_gray>[<b><gradient:gold:red>Delphi</gradient></b>]");

  static final CommandExceptionType NOP = new CommandExceptionType() {};

  static final TranslatableExceptionType ONLY_PLAYERS
      = new TranslatableExceptionType("permissions.requires.player");

  static final TranslatableExceptionType NONE_TARGETED
      = new TranslatableExceptionType("delphi.error.noTarget");

  static final TranslatableExceptionType DUMP_FAIL
      = new TranslatableExceptionType("delphi.error.debugDumpFail");

  static final TranslatableExceptionType DEBUG_NO_TARGET
      = new TranslatableExceptionType("delphi.error.debugNoTarget");

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

  static final TranslatableExceptionType INSTANCE_NAME_IN_USE
      = new TranslatableExceptionType("delphi.error.instanceNameUsed");

  static final TranslatableExceptionType VIEW_NOT_FOUND
      = new TranslatableExceptionType("delphi.error.viewNotFound");

  static final TranslatableExceptionType REQUIRES_LOCATION
      = new TranslatableExceptionType("delphi.error.requiresLocation");

  static final TranslatableExceptionType ILLEGAL_INSTANCE_NAME
      = new TranslatableExceptionType("delphi.error.instanceNameIllegal");

  public static LiteralCommandNode<CommandSourceStack> createCommand() {
    LiteralArgumentBuilder<CommandSourceStack> literal = literal("delphi");

    literal.requires(stack -> stack.getSender().hasPermission(Permissions.COMMANDS));

    literal.then(open());
    literal.then(close());

    literal.then(debugArguments());
    literal.then(reloadConfig());

    return literal.build();
  }

  private static Component prefixTranslatable(String message, TextColor color, Component... args) {
    TranslatableComponent msg = Component.translatable(message, color, args);
    return Component.text().append(PREFIX, Component.space(), msg).build();
  }

  private static PageView getAnyTargeted(CommandContext<CommandSourceStack> context)
      throws CommandSyntaxException
  {
    if (!(context.getSource().getSender() instanceof Player player)) {
      throw ONLY_PLAYERS.create();
    }

    DelphiImpl pages = getPlugin().getManager();
    Optional<DocumentView> opt = pages.getAnyTargetedView(player);

    if (opt.isEmpty()) {
      throw NONE_TARGETED.create();
    }

    return (PageView) opt.get();
  }

  private static LiteralCommandNode<CommandSourceStack> reloadConfig() {
    return literal("reload-config")
        .executes(c -> {
          getPlugin().reloadConfig();

          c.getSource().getSender().sendMessage(
              prefixTranslatable("delphi.reloadedConfig", NamedTextColor.GRAY)
          );
          return SINGLE_SUCCESS;
        })
        .build();
  }

  private static LiteralCommandNode<CommandSourceStack> close() {
    return literal("close")
        .then(argument("instance name", new InstanceNameType())
            .executes(c -> {
              PageView view = c.getArgument("instance name", PageView.class);
              view.close();

              c.getSource().getSender().sendMessage(
                  prefixTranslatable("delphi.closed.named",
                      NamedTextColor.GRAY,
                      Component.text(view.getInstanceName())
                  )
              );
              return SINGLE_SUCCESS;
            })
        )

        .then(literal("targeted")
            .executes(context -> {
              PageView view = getAnyTargeted(context);
              view.close();

              context.getSource().getSender().sendMessage(
                  prefixTranslatable("delphi.closed.targeted", NamedTextColor.GRAY)
              );
              return SINGLE_SUCCESS;
            })
        )
        .then(literal("all")
            .executes(c -> {
              ViewManager views = getPlugin().getViewManager();
              List<PageView> viewList = new ArrayList<>(views.getOpenViews());

              for (PageView view : viewList) {
                view.close();
              }

              c.getSource().getSender().sendMessage(
                  prefixTranslatable("delphi.closed.all", NamedTextColor.GRAY)
              );
              return SINGLE_SUCCESS;
            })
        )
        .build();
  }

  private static LiteralCommandNode<CommandSourceStack> debugArguments() {
    return literal("debug")
        .requires(stack -> stack.getSender().hasPermission(Permissions.DEBUG))
        .then(literal("dump-page-xml")
            .executes(dumpCommand("page-dump", false))
        )
        .then(literal("dump-targeted-element-xml")
            .executes(dumpCommand("target-element-dump", true))
        )
        .then(toggleDebugLines())
        .build();
  }

  private static Command<CommandSourceStack> dumpCommand(String fname, boolean targetElement) {
    return context -> {
      PageView view = getAnyTargeted(context);
      Element target;

      if (targetElement) {
        target = view.getDocument().getHoveredElement();

        if (target == null) {
          throw DEBUG_NO_TARGET.create();
        }
      } else {
        target = null;
      }

      Path path = Debug.dumpDebugTree(fname, view, target);

      if (path == null) {
        throw DUMP_FAIL.create();
      }

      context.getSource().getSender().sendMessage(
          prefixTranslatable(
              "delphi.debug.dumpedXml",
              NamedTextColor.GRAY,
              Component.text(path.toString())
          )
      );
      return SINGLE_SUCCESS;
    };
  }

  private static LiteralCommandNode<CommandSourceStack> toggleDebugLines() {
    return Commands.literal("toggle-outlines")
        .executes(c -> {
          boolean state = Debug.debugOutlines;
          Component text;

          if (state) {
            text = prefixTranslatable("delphi.debug.outlineToggle.off", NamedTextColor.GRAY);
          } else {
            text = prefixTranslatable("delphi.debug.outlineToggle.on", NamedTextColor.YELLOW);
          }

          c.getSource().getSender().sendMessage(text);
          Debug.debugOutlines = !state;

          return SINGLE_SUCCESS;
        })
        .build();
  }

  private static LiteralCommandNode<CommandSourceStack> open() {
    return Commands.literal("open")
        .then(argument("player", ArgumentTypes.players())
            .then(createOpenPath(false))
        )
        .then(literal("all-players")
            .then(createOpenPath(true))
        )
        .build();
  }

  private static RequiredArgumentBuilder<CommandSourceStack, ?> createOpenPath(boolean allPlayers) {
    return argument("path", new PathType())
        .executes(c -> openDocument(c, allPlayers, false, false, false))

        .then(argument("instance name", StringArgumentType.word())
            .executes(c -> openDocument(c, allPlayers, true, false, false))
        )

        .then(argument("position", ArgumentTypes.finePosition())
            .executes(c -> openDocument(c, allPlayers, false, true, false))

            .then(argument("instance name", StringArgumentType.word())
                .executes(c -> openDocument(c, allPlayers, true, true, false))
            )

            .then(argument("yaw", FloatArgumentType.floatArg())
                .then(argument("pitch", FloatArgumentType.floatArg())
                    .executes(c -> openDocument(c, allPlayers, false, true, true))

                    .then(argument("instance name", StringArgumentType.word())
                        .executes(c -> openDocument(c, allPlayers, true, true, true))
                    )
                )
            )
        );
  }

  private static int openDocument(
      CommandContext<CommandSourceStack> c,
      boolean allPlayers,
      boolean hasInstanceName,
      boolean hasPosition,
      boolean hasRotation
  ) throws CommandSyntaxException {
    CommandSourceStack stack = c.getSource();

    ResourcePath path = getPath(c);
    String instanceName;
    Location location;
    Collection<Player> players;

    if (allPlayers) {
      players = (Collection<Player>) Bukkit.getOnlinePlayers();
    } else {
      PlayerSelectorArgumentResolver resolver
          = c.getArgument("player", PlayerSelectorArgumentResolver.class);
      players = resolver.resolve(stack);
    }

    if (hasInstanceName) {
      instanceName = c.getArgument("instance name", String.class);
    } else {
      instanceName = null;
    }

    if (hasPosition) {
      FinePositionResolver posResolver = c.getArgument("position", FinePositionResolver.class);
      FinePosition pos = posResolver.resolve(stack);
      location = new Location(stack.getLocation().getWorld(), pos.x(), pos.y(), pos.z());

      if (hasRotation) {
        float yaw = c.getArgument("yaw", Float.class);
        float pitch = c.getArgument("pitch", Float.class);

        location.setYaw(yaw);
        location.setPitch(pitch);
      }
    } else {
      if (players.size() != 1 || allPlayers) {
        throw REQUIRES_LOCATION.create();
      }

      location = null;
    }

    DocumentViewBuilder builder = DelphiProvider.newViewBuilder()
        .setPath(path)
        .setInstanceName(instanceName);

    if (allPlayers) {
      builder.allPlayers();
    } else {
      builder.setPlayers( players);
    }

    if (location != null) {
      builder.setSpawnLocation(location);
    }

    Result<DocumentView, DelphiException> result = builder.open();

    if (result.isSuccess()) {
      c.getSource().getSender().sendMessage(
          prefixTranslatable("delphi.docOpened", NamedTextColor.GRAY)
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
      case ERR_INSTANCE_NAME_USED -> INSTANCE_NAME_IN_USE.create(exc.getBaseMessage());
      case ERR_ILLEGAL_INSTANCE_NAME -> ILLEGAL_INSTANCE_NAME.create(exc.getBaseMessage());
      default -> new CommandSyntaxException(NOP, new LiteralMessage(exc.getMessage()));
    };
  }

  private static DelphiPlugin getPlugin() {
    return JavaPlugin.getPlugin(DelphiPlugin.class);
  }

  private static ResourcePath getPath(CommandContext<CommandSourceStack> context) {
    return context.getArgument("path", ResourcePath.class);
  }

  static class InstanceNameType implements CustomArgumentType<DocumentView, String> {

    private final StringArgumentType argType = StringArgumentType.word();

    @Override
    public @NotNull DocumentView parse(@NotNull StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      String string = argType.parse(reader);
      DelphiImpl manager = getPlugin().getManager();

      return manager.getByInstanceName(string).orElseThrow(() -> {
        reader.setCursor(start);
        return VIEW_NOT_FOUND.createWithContext(reader, start);
      });
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(
        @NotNull CommandContext<S> context,
        @NotNull SuggestionsBuilder builder
    ) {
      Set<String> keys = getPlugin().getViewManager().getByInstanceName().keySet();
      String token = builder.getRemainingLowerCase();

      for (String key : keys) {
        String lkey = key.toLowerCase(Locale.ROOT);

        if (!lkey.contains(token)) {
          continue;
        }

        builder.suggest(key);
      }

      return builder.buildFuture();
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
      return argType;
    }
  }

  static class PathType implements CustomArgumentType<ResourcePath, PlayerProfileListResolver> {

    private PluginResources getModules() {
      ClassLoader loader = DelphiPlugin.class.getClassLoader();

      if (!(loader instanceof ConfiguredPluginClassLoader pluginLoader)) {
        return null;
      }

      JavaPlugin plugin = pluginLoader.getPlugin();
      if (!(plugin instanceof DelphiPlugin delphi)) {
        return null;
      }

      return delphi.getPluginResources();
    }

    @Override
    public @NotNull ResourcePath parse(@NotNull StringReader reader) throws CommandSyntaxException {
      PathParser<?> parser = new PathParser<>(getModules(), reader);
      parser.parse();
      return parser.getPath();
    }

    @Override
    public @NotNull ArgumentType<PlayerProfileListResolver> getNativeType() {
      return ArgumentTypes.playerProfiles();
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
