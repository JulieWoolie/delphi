package net.arcadiusmc.delphidom;

import java.lang.StackWalker.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Loggers {
  private Loggers() {}

  private static final boolean USE_GLOBAL_LOGGER = false;

  public static Logger getDocumentLogger() {
    return getLogger("Document");
  }

  public static Logger getLogger() {
    if (USE_GLOBAL_LOGGER) {
      return LoggerFactory.getLogger("Delphi");
    }

    StackWalker walker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
    Class<?> caller = walker.getCallerClass();

    return getLogger(caller.getSimpleName());
  }

  public static Logger getLogger(String name) {
    return LoggerFactory.getLogger(name);
  }
}
