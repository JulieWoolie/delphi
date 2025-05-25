package net.arcadiusmc.hephaestus.stdlib;

import net.arcadiusmc.hephaestus.Scripting;
import net.arcadiusmc.hephaestus.lang.LanguageInterface;
import org.bukkit.entity.Player;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public enum GetPlayerFunction implements ProxyExecutable {
  INSTANCE;

  @Override
  public Object execute(Value... arguments) {
    if (arguments.length < 1) {
      return null;
    }

    if (arguments.length == 1) {
      Value arg = arguments[0];
      return arg.as(Player.class);
    }

    LanguageInterface js = Scripting.JS;
    Value resultArray = js.newArray();

    for (Value argument : arguments) {
      Player p = argument.as(Player.class);
      js.appendToArray(resultArray, p);
    }

    return resultArray;
  }
}
