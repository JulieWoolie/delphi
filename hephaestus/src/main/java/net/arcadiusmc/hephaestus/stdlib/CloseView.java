package net.arcadiusmc.hephaestus.stdlib;

import lombok.RequiredArgsConstructor;
import net.arcadiusmc.delphi.DocumentView;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

@RequiredArgsConstructor
public class CloseView implements ProxyExecutable {

  private final DocumentView view;

  @Override
  public Object execute(Value... arguments) {
    view.runLater(1L, new CloseViewTask(view));
    return null;
  }

  public record CloseViewTask(DocumentView view) implements Runnable {

    @Override
    public void run() {
      view.close();
    }
  }
}
