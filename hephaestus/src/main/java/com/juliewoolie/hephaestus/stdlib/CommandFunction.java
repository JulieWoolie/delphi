package com.juliewoolie.hephaestus.stdlib;

import java.util.StringJoiner;
import org.bukkit.Bukkit;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public enum CommandFunction implements ProxyExecutable {
  INSTANCE,
  ;

  @Override
  public Object execute(Value... arguments) {
    StringJoiner builder = new StringJoiner(" ");
    for (Value argument : arguments) {
      String str = argument.asString();
      builder.add(str);
    }

    String command = builder.toString();

    return Bukkit.dispatchCommand(
        Bukkit.getConsoleSender(),
        command
    );
  }
}
