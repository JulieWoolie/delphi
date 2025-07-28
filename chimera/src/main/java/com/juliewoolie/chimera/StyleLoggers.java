package com.juliewoolie.chimera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface StyleLoggers {

  String LOGGER_NAME = "Document";

  static Logger getLogger() {
    return LoggerFactory.getLogger(LOGGER_NAME);
  }
}
