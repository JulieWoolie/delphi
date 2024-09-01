package net.arcadiusmc.delphidom.parser;

import org.slf4j.Logger;
import org.slf4j.event.Level;

public interface ErrorListener {

  static ErrorListener logging(Logger logger) {
    return error -> {
      if (error.level() == Level.WARN) {
        logger.warn(error.message());
      } else {
        logger.error(error.message());
      }
    };
  }

  void onError(Error error);
}
