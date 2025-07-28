package com.juliewoolie.hephaestus.stdlib;

import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.DomQueryable;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;

public class DollarSignFunction implements ProxyExecutable {

  private final Document document;

  public DollarSignFunction(Document document) {
    this.document = document;
  }

  @Override
  public Object execute(Value... arguments) {
    DomQueryable target = document;
    String selector;

    switch (Math.min(arguments.length, 2)) {
      case 2:
        target = arguments[1].as(DomQueryable.class);
      case 1:
        selector = arguments[0].asString();
        break;

      default:
        throw new IllegalArgumentException("Not enough arguments");
    }

    return target.querySelectorAll(selector);
  }
}
