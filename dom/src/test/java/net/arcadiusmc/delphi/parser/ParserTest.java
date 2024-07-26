package net.arcadiusmc.delphi.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import net.arcadiusmc.delphi.Loggers;
import net.arcadiusmc.delphi.dom.selector.Selector;
import net.arcadiusmc.delphi.parser.ParserErrors.ErrorLevel;
import net.arcadiusmc.dom.ParserException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class ParserTest {

  static final String[] SELECTORS = {
      ".class-name",
      ".class-name .other-class",
      ".class-name .other-class[attr=\"value\"]",
      ".class-name .other-class[attr~=\"value\"]",
      ".class-name .other-class[attr*=\"value\"]",
      ".class-name .other-class[attr^=\"value\"]",
      ".class-name .other-class[attr|=\"value\"]",
      ".class-name .other-class[attr|=\"value\"][second=\"other-value\"]",
      ".class-name .other-class[attr|=\"value\"][second=\"other-value\"]:hover",
      ".class-name .other-class[attr|=\"value\"][second=\"other-value\"]:active",
      ".class-name:hover .other-class[attr|=\"value\"][second=\"other-value\"]:active",
      "div",
      "div div[id=\"facts\"]",
      "div #idddd",
      "#id-element",
      "*"
  };

  @Test
  void testSpecificity() {
    SelectorAndInput[] selectors = new SelectorAndInput[SELECTORS.length];
    for (int i = 0; i < SELECTORS.length; i++) {
      selectors[i] = new SelectorAndInput(parse(SELECTORS[i]), SELECTORS[i]);
    }

    Arrays.sort(selectors);

    System.out.println("ordered list: ");
    int i = 0;

    for (SelectorAndInput s : selectors) {
      logSelector("test-spec", i, s.in, s.selector);
      i++;
    }
  }

  @Test
  void testCanParse() {
    int i = 0;
    for (String selector : SELECTORS) {
      tryParse(selector, i++);
    }
  }

  Selector parse(String in) {
    StringBuffer buffer = new StringBuffer(in);
    Parser parser = new Parser(buffer);
    ParserErrors errors = parser.getErrors();

    errors.setListener(error -> {
      Logger logger = Loggers.getLogger("parser");

      if (error.level() == ErrorLevel.WARN) {
        logger.warn(error.message());
      } else {
        logger.error(error.message());
      }
    });

    Selector selector;

    try {
      selector = parser.selector();
    } catch (ParserException exc) {
      selector = null;
      // Ignored
    }

    if (errors.isErrorPresent()) {
      fail("Parser failure");
    }

    return selector;
  }

  void tryParse(String in, int i) {
    StringBuffer buffer = new StringBuffer(in);
    Parser parser = new Parser(buffer);
    ParserErrors errors = parser.getErrors();

    Selector selector = parser.selector();
    errors.orThrow();

    logSelector("try-parse", i, in, selector);

    if (selector == null) {
      return;
    }

    String out = selector.toString();
    assertEquals(in, out);
  }

  private void logSelector(String testName, int i, String in, Selector selector) {
    System.out.printf("%s-%s:\n  input  = %s\n  output = %s\n", testName, i, in, selector);
  }

  record SelectorAndInput(Selector selector, String in) implements Comparable<SelectorAndInput> {

    @Override
    public int compareTo(SelectorAndInput o) {
      return selector.compareTo(o.selector);
    }
  }
}