package com.juliewoolie.chimera.parse;

public record StreamState(
    TokenStream stream,
    int cursor,
    int col,
    int lineno,
    Token peeked,
    Location lastTokenStart
) implements AutoCloseable {

  @Override
  public void close() {
    stream.restoreState(this);
  }
}
