package com.juliewoolie.hephaestus.stdlib;

import lombok.RequiredArgsConstructor;
import com.juliewoolie.delphi.DocumentView;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

@RequiredArgsConstructor
public class CancelTask implements ProxyExecutable {

  private final DocumentView view;

  @Override
  public Object execute(Value... arguments) {
    if (arguments.length < 1) {
      throw new IllegalArgumentException("At least 1 argument required");
    }

    int taskId = arguments[0].asInt();
    return view.cancelTask(taskId);
  }
}
