package net.arcadiusmc.hephaestus;

import static net.arcadiusmc.hephaestus.ScriptElementSystem.JS_LANGUAGE;

import net.arcadiusmc.hephaestus.stdlib.CommandFunction;
import org.bukkit.Bukkit;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;

public class Scripting {

  public static void initStandardValues(Value scope) {
    scope.putMember("server", Bukkit.getServer());
    scope.putMember("command", CommandFunction.INSTANCE);
  }

  public static Context setupContext() {
    Context.Builder ctx = Context.newBuilder(JS_LANGUAGE);
    HostAccess.Builder builder = HostAccess.newBuilder(HostAccess.ALL);

    Context built = ctx
        .option("engine.WarnInterpreterOnly", "false")
        .allowCreateProcess(false)
        .allowCreateThread(false)
        .allowEnvironmentAccess(EnvironmentAccess.NONE)
        .allowHostAccess(builder.build())
        .allowHostClassLoading(true)
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
}
