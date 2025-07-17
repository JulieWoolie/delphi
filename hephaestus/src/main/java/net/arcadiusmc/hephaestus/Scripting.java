package net.arcadiusmc.hephaestus;

import static net.arcadiusmc.hephaestus.typemappers.TypeMapper.addTypeMapper;

import com.oracle.truffle.api.interop.InteropLibrary;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.hephaestus.interop.DelphiObjectTypeRegistry;
import net.arcadiusmc.hephaestus.interop.DelphiScriptObject;
import net.arcadiusmc.hephaestus.interop.Interop;
import net.arcadiusmc.hephaestus.lang.JavaScriptInterface;
import net.arcadiusmc.hephaestus.lang.LanguageInterface;
import net.arcadiusmc.hephaestus.stdlib.CancelTask;
import net.arcadiusmc.hephaestus.stdlib.CloseView;
import net.arcadiusmc.hephaestus.stdlib.CommandFunction;
import net.arcadiusmc.hephaestus.stdlib.DollarSignFunction;
import net.arcadiusmc.hephaestus.stdlib.GetPlayerFunction;
import net.arcadiusmc.hephaestus.stdlib.HsvFunction;
import net.arcadiusmc.hephaestus.stdlib.RgbFunction;
import net.arcadiusmc.hephaestus.stdlib.SendMessageFunction;
import net.arcadiusmc.hephaestus.stdlib.SetInterval;
import net.arcadiusmc.hephaestus.stdlib.SetTimeout;
import net.arcadiusmc.hephaestus.typemappers.ComponentTypeMapper;
import net.arcadiusmc.hephaestus.typemappers.LocationTypeMapper;
import net.arcadiusmc.hephaestus.typemappers.PlayerTypeMapper;
import net.arcadiusmc.hephaestus.typemappers.VectorTypeMapper;
import net.arcadiusmc.hephaestus.typemappers.WorldTypeMapper;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
  public static final DelphiObjectTypeRegistry typeRegistry = new DelphiObjectTypeRegistry();
  public static final String JS_LANGUAGE = "js";
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

    // Color, ig
    scope.putMember("hsv", HsvFunction.HSV);
    scope.putMember("rgb", RgbFunction.RGB);
  }

  public static void initDocumentScope(Value scope, Document document) {
    scope.putMember("$", new DollarSignFunction(document));
  }

  public static void initViewScope(Value scope, DocumentView view) {
    scope.putMember("setTimeout", new SetTimeout(view));
    scope.putMember("setInterval", new SetInterval(view));

    CancelTask task = new CancelTask(view);
    scope.putMember("clearTimeout", task);
    scope.putMember("clearInterval", task);

    scope.putMember("closeView", new CloseView(view));
  }

  public static Context setupContext() {
    Context.Builder ctx = Context.newBuilder(JS_LANGUAGE);
    HostAccess.Builder builder = HostAccess.newBuilder(HostAccess.ALL);

    addTypeMapper(builder, Value.class, Player.class,     new PlayerTypeMapper());
    addTypeMapper(builder, Value.class, Component.class,  new ComponentTypeMapper());
    addTypeMapper(builder, Value.class, Vector.class,     new VectorTypeMapper());
    addTypeMapper(builder, Value.class, World.class,      new WorldTypeMapper());
    addTypeMapper(builder, Value.class, Location.class,   new LocationTypeMapper());
    
    Context built = ctx
        .allowExperimentalOptions(true)
        .useSystemExit(false)
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

  public static Object wrapReturn(Object o) {
    if (o == null) {
      return Interop.getHostAccessor().hostSupport().getHostNull();
    }
    if (InteropLibrary.isValidValue(o)) {
      return o;
    }

    DelphiScriptObject<Object> value = Scripting.typeRegistry.wrapObject(o);
    if (value != null) {
      return value;
    }

    return Interop.getHostAccessor()
        .hostSupport()
        .toDisconnectedHostObject(o);
  }

  public <T> Object wrapReturn(Class<T> interfaceType, T obj) {
    DelphiScriptObject<T> sobj = typeRegistry.wrapObject(interfaceType, obj);
    if (sobj != null) {
      return sobj;
    }
    return obj;
  }

  public static double toDouble(Value value) {
    return toDouble(value, 0.0d);
  }

  public static double toDouble(Value value, double fallback) {
    if (value == null || !value.isNumber()) {
      return fallback;
    }
    return value.asDouble();
  }

  public static float toFloat(Value value) {
    return toFloat(value, 0.0f);
  }

  public static float toFloat(Value value, float fallback) {
    if (value == null || !value.isNumber()) {
      return fallback;
    }
    if (value.fitsInFloat()) {
      return value.asFloat();
    }
    if (value.fitsInDouble()) {
      return (float) value.asDouble();
    }
    return fallback;
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

  public static void verifyExecutable(Value func) {
    if (func.canExecute()) {
      return;
    }

    throw new IllegalArgumentException("Callback is not executable");
  }

  public static int toInt(Value argument, int fallback) {
    if (!argument.isNumber()) {
      return fallback;
    }
    if (argument.fitsInInt()) {
      return argument.asInt();
    }
    if (argument.fitsInLong()) {
      long l = argument.asLong();
      return (int) l;
    }
    return fallback;
  }
}
