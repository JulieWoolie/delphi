package com.juliewoolie.chimera.parse;

import lombok.Getter;

@Getter
public class ChimeraException extends RuntimeException {

  private final ChimeraError error;

  public ChimeraException(ChimeraError error) {
    super(error.getFormattedError());
    this.error = error;
  }

}
