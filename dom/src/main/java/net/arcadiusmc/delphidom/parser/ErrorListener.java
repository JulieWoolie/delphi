package net.arcadiusmc.delphidom.parser;

import net.arcadiusmc.delphidom.parser.ParserErrors.Error;
import net.arcadiusmc.delphidom.parser.ParserErrors.ErrorLevel;
import org.slf4j.Logger;

public interface ErrorListener {

  static ErrorListener logging(Logger logger) {
    return error -> {
      if (error.level() == ErrorLevel.WARN) {
        logger.warn(error.message());
      } else {
        logger.error(error.message());
      }
    };
  }

  void onError(Error error);
}
