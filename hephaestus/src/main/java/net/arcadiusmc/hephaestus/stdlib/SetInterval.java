package net.arcadiusmc.hephaestus.stdlib;

import lombok.RequiredArgsConstructor;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.hephaestus.Scripting;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

@RequiredArgsConstructor
public class SetInterval implements ProxyExecutable {

  private final DocumentView view;

  @Override
  public Object execute(Value... arguments) {
    Value func;
    long interval;
    long delay;

    switch (arguments.length) {
      case 0:
        throw new IllegalArgumentException("At least 1 argument is required");
      case 1:
        func = arguments[0];
        interval = delay = 20L;
        break;
      case 2:
        func = arguments[0];
        interval = delay = arguments[1].asLong();
        break;
      default:
        func = arguments[0];
        delay = arguments[1].asLong();
        interval = arguments[2].asLong();
        break;
    }

    Scripting.verifyExecutable(func);
    return view.runRepeating(delay, interval, new RunnableValue(func));
  }
}
