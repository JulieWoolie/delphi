package net.arcadiusmc.hephaestus;

import static net.arcadiusmc.hephaestus.ScriptElementSystem.JS_LANGUAGE;

import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.hephaestus.lang.JavaScriptInterface;
import net.arcadiusmc.hephaestus.lang.LanguageInterface;
import net.arcadiusmc.hephaestus.stdlib.CommandFunction;
import net.arcadiusmc.hephaestus.stdlib.GetPlayerFunction;
import net.arcadiusmc.hephaestus.stdlib.SendMessageFunction;
import net.arcadiusmc.hephaestus.typemappers.ComponentTypeMapper;
import net.arcadiusmc.hephaestus.typemappers.PlayerTypeMapper;
import net.arcadiusmc.hephaestus.typemappers.TypeMapper;
import net.arcadiusmc.hephaestus.typemappers.VectorTypeMapper;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;
import org.slf4j.Logger;

public class Scripting {

  public static final LanguageInterface JS = new JavaScriptInterface();
  private static final Logger LOGGER = Loggers.getLogger();

  public static void scriptingInit() {
    try {
      JS.initLanguage();
    } catch (Exception e) {
      LOGGER.error("Failed to initialize JS:", e);
    }
  }

  public static void shutdownScripting() {
    JS.shutdown();
  }

  public static void initStandardValues(Value scope) {
    // Values
    scope.putMember("server", Bukkit.getServer());

    // Functions
    scope.putMember("command", CommandFunction.INSTANCE);
    scope.putMember("getPlayer", GetPlayerFunction.INSTANCE);
    scope.putMember("sendMessage", SendMessageFunction.CHAT);
    scope.putMember("sendActionBar", SendMessageFunction.ACTIONBAR);
  }

  public static Context setupContext() {
    Context.Builder ctx = Context.newBuilder(JS_LANGUAGE);
    HostAccess.Builder builder = HostAccess.newBuilder(HostAccess.ALL);

    TypeMapper.addTypeMapper(builder, Value.class, Player.class, new PlayerTypeMapper());
    TypeMapper.addTypeMapper(builder, Value.class, Component.class, new ComponentTypeMapper());
    TypeMapper.addTypeMapper(builder, Value.class, Vector.class, new VectorTypeMapper());

    Context built = ctx
        .option("engine.WarnInterpreterOnly", "false")
        .option("js.polyglot-builtin", "false")
        .option("js.load", "false")
        .allowCreateProcess(false)
        .allowCreateThread(false)
        .allowEnvironmentAccess(EnvironmentAccess.NONE)
        .allowHostAccess(builder.build())
        .allowHostClassLoading(true)
        .allowValueSharing(true)
        .allowIO(
            IOAccess.newBuilder()
                .allowHostFileAccess(true)
                .allowHostSocketAccess(false)
                .build()
        )
        .allowNativeAccess(true)
        .build();

    Value jsValues = built.getBindings(JS_LANGUAGE);
    initStandardValues(jsValues);

    return built;
  }

  public static Component toComponent(Value value, Pointered target) {
    if (value.isHostObject()) {
      Object host = value.asHostObject();
      if (host instanceof ComponentLike like) {
        return like.asComponent();
      }
      if (host instanceof Entity entity) {
        return entity.teamDisplayName();
      }
      if (host instanceof ItemStack item) {
        return item.displayName();
      }
      return Component.empty();
    }

    if (value.isBoolean() || value.isString() || value.isNumber() || value.isNull()) {
      MiniMessage serializer = MiniMessage.miniMessage();

      if (target == null) {
        return serializer.deserialize(value.asString());
      } else {
        return serializer.deserialize(value.asString(), target);
      }
    }

    String json = Scripting.JS.toJson(value);

    try {
      return GsonComponentSerializer.gson().deserialize(json);
    } catch (Exception e) {
      return Component.empty();
    }
  }
}
