package com.juliewoolie.delphiplugin.command;

import static com.juliewoolie.delphi.resource.DelphiException.ERR_ACCESS_DENIED;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_DOC_PARSE;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_ILLEGAL_INSTANCE_NAME;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_INSTANCE_NAME_USED;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_IO_ERROR;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_MISSING_PLUGINS;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_MODULE_DIRECTORY_NOT_FOUND;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_MODULE_ERROR;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_MODULE_UNKNOWN;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_MODULE_ZIP_ACCESS_DENIED;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_NO_FILE;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_OLD_GAME_VERSION;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_SAX_PARSER_INIT;
import static com.juliewoolie.delphi.resource.DelphiException.ERR_UNKNOWN;
import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

import com.juliewoolie.delphi.DelphiProvider;
import com.juliewoolie.delphi.DocumentView;
import com.juliewoolie.delphi.DocumentViewBuilder;
import com.juliewoolie.delphi.resource.DelphiException;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphi.util.Result;
import com.juliewoolie.delphiplugin.Debug;
import com.juliewoolie.delphiplugin.DelphiImpl;
import com.juliewoolie.delphiplugin.DelphiPlugin;
import com.juliewoolie.delphiplugin.PageView;
import com.juliewoolie.delphiplugin.ViewManager;
import com.juliewoolie.delphiplugin.devtools.DevtoolModule;
import com.juliewoolie.delphiplugin.resource.PluginResources;
import com.juliewoolie.dom.Canvas;
import com.juliewoolie.dom.CanvasElement;
import com.juliewoolie.dom.Element;
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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("UnstableApiUsage")
public class DelphiCommand {

  static final Component PREFIX = Component.translatable("delphi.prefix");

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

  static final TranslatableExceptionType GAME_TOO_OLD
      = new TranslatableExceptionType("delphi.error.gameTooOld");

  static final TranslatableExceptionType NOT_A_CANVAS
      = new TranslatableExceptionType("delphi.error.notACanvas");

  static final TranslatableExceptionType CANVAS_DUMP_FAILED
      = new TranslatableExceptionType("delphi.error.canvasDumpFailed");

  static final TranslatableExceptionType DEVTOOLS_OPEN_FAILED
      = new TranslatableExceptionType("delphi.devtools.alreadyHasDevtools");

  public static LiteralCommandNode<CommandSourceStack> createCommand() {
    LiteralArgumentBuilder<CommandSourceStack> literal = literal("delphi");

    literal.requires(stack -> stack.getSender().hasPermission(Permissions.COMMANDS));

    literal.then(open());
    literal.then(close());

    literal.then(debugArguments());
    literal.then(reloadConfig());

    literal.then(devtools());

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

  private static LiteralCommandNode<CommandSourceStack> devtools() {
    return literal("devtools")
        .executes(context -> {
          PageView view = getAnyTargeted(context);
          return openDevtools(context.getSource(), view);
        })
        .then(argument("instance name", new InstanceNameType())
            .executes(context -> {
              PageView view = context.getArgument("instance name", PageView.class);
              return openDevtools(context.getSource(), view);
            })
        )
        .build();
  }

  private static int openDevtools(CommandSourceStack stack, PageView view)
      throws CommandSyntaxException
  {
    if (!(stack.getSender() instanceof Player player)) {
      throw ONLY_PLAYERS.create();
    }

    var result = DevtoolModule.openDevtoolsFor(player, view);

    if (result.isSuccess()) {
      stack.getSender().sendMessage(
          prefixTranslatable("delphi.devtools.opened", NamedTextColor.GRAY)
      );
      return SINGLE_SUCCESS;
    }

    DelphiException exc = result.error().orElseThrow();
    if (exc.getCode() == ERR_INSTANCE_NAME_USED) {
      throw DEVTOOLS_OPEN_FAILED.create();
    }

    throw toCommandError(exc);
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
        .then(literal("dump-targeted-canvas-png")
            .executes(dumpCanvas())
        )
        .then(toggleDebugLines())
        .build();
  }

  private static Command<CommandSourceStack> dumpCanvas() {
    return context -> {
      PageView view = getAnyTargeted(context);
      Element targetted = view.getDocument().getHoveredElement();

      if (!(targetted instanceof CanvasElement canvas)) {
        throw NOT_A_CANVAS.create();
      }

      Canvas gfx = canvas.getCanvas();
      BufferedImage image = new BufferedImage(gfx.getWidth(), gfx.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Vector4i c = new Vector4i();

      for (int y = 0; y < gfx.getHeight(); y++) {
        for (int x = 0; x < gfx.getWidth(); x++) {
          gfx.getColori(x, y, c);
          Color awtColor = new Color(c.x, c.y, c.z, c.w);
          image.setRGB(x, y, awtColor.getRGB());
        }
      }

      Path p = getPlugin()
          .getDataPath()
          .resolve("debug")
          .resolve("canvas-dump.png");

      try (var s = Files.newOutputStream(p)) {
        ImageIO.write(image, "PNG", s);
      } catch (IOException e) {
        getPlugin().getSLF4JLogger().error("Error writing canvas dump", e);
        throw CANVAS_DUMP_FAILED.create();
      }

      context.getSource().getSender().sendMessage(
          Component.translatable("delphi.debug.dumpedCanvas")
      );
      return SINGLE_SUCCESS;
    };
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

    return handleDocumentOpen(builder.open(), c.getSource(), path, instanceName);
  }

  private static int handleDocumentOpen(
      Result<DocumentView, DelphiException> result,
      CommandSourceStack source,
      ResourcePath path,
      String instanceName
  ) throws CommandSyntaxException {
    if (result.isSuccess()) {
      source.getSender().sendMessage(
          prefixTranslatable("delphi.docOpened", NamedTextColor.GRAY)
      );

      return SINGLE_SUCCESS;
    }

    DelphiException error = result.error().get();
    Logger logger = LoggerFactory.getLogger("DelphiCommand");

    if (logger.isDebugEnabled() || error.getCode() == ERR_UNKNOWN) {
      logger.error("Error opening page with path {}, instanceName={}", path, instanceName, error);
    }

    throw toCommandError(error);
  }

  private static CommandSyntaxException toCommandError(DelphiException exc) {
    return switch (exc.getCode()) {
      case ERR_NO_FILE -> NO_FILE.create();
      case ERR_ACCESS_DENIED -> ACCESS_DENIED.create();
      case ERR_IO_ERROR -> IO_ERROR.create();
      case ERR_MODULE_ERROR -> MODULE_ERROR.create(exc.getBaseMessage());
      case ERR_SAX_PARSER_INIT -> SAX_PARSER.create();
      case ERR_DOC_PARSE -> DOC_PARSE.create();
      case ERR_MISSING_PLUGINS -> MISSING_PLUGINS.create(exc.getBaseMessage());
      case ERR_UNKNOWN -> UNKNOWN.create();
      case ERR_MODULE_UNKNOWN -> PathParser.UNKNOWN_MODULE.create(exc.getBaseMessage());
      case ERR_MODULE_DIRECTORY_NOT_FOUND -> NO_MODULE_DIR.create();
      case ERR_MODULE_ZIP_ACCESS_DENIED -> MODULE_ACCESS_DENIED.create();
      case ERR_INSTANCE_NAME_USED -> INSTANCE_NAME_IN_USE.create(exc.getBaseMessage());
      case ERR_ILLEGAL_INSTANCE_NAME -> ILLEGAL_INSTANCE_NAME.create(exc.getBaseMessage());
      case ERR_OLD_GAME_VERSION -> GAME_TOO_OLD.create(exc.getBaseMessage());
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
        return VIEW_NOT_FOUND.createWithContext(reader, string);
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
