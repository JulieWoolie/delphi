package com.juliewoolie.hephaestus.stdlib;

import org.graalvm.polyglot.Value;

record RunnableValue(Value value) implements Runnable {

  @Override
  public void run() {
    value.execute();
  }
}
