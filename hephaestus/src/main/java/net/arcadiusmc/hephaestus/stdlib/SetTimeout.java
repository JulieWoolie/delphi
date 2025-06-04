package net.arcadiusmc.hephaestus.stdlib;

import lombok.RequiredArgsConstructor;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.hephaestus.Scripting;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

@RequiredArgsConstructor
public class SetTimeout implements ProxyExecutable {

  private final DocumentView view;

  @Override
  public Object execute(Value... arguments) {
    Value func;
    long delay;

    switch (arguments.length) {
      case 0:
        throw new IllegalArgumentException("At least 1 argument is required");
      case 1:
        func = arguments[0];
        delay = 20L;
        break;
      default:
        func = arguments[1];
        delay = arguments[2].asLong();
        break;
    }

    if (!func.canExecute()) {
      throw new IllegalArgumentException("Specified task is not executable");
    }

    Scripting.verifyExecutable(func);
    return view.runLater(delay, new RunnableValue(func));
  }
}
