package com.juliewoolie.chimera.parse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.event.Level;

@Getter
@RequiredArgsConstructor
public class ChimeraError {

  private final String message;
  private final Location location;
  private final String formattedError;
  private final Level level;
}
