package net.arcadiusmc.delphidom.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.parser.ParserErrors.ErrorLevel;
import net.arcadiusmc.delphidom.selector.Combinator;
import net.arcadiusmc.delphidom.selector.GroupedIndexSelector;
import net.arcadiusmc.delphidom.selector.IndexSelector;
import net.arcadiusmc.delphidom.selector.PseudoFuncFunction;
import net.arcadiusmc.delphidom.selector.Selector;
import net.arcadiusmc.delphidom.selector.SelectorNode;
import net.arcadiusmc.delphidom.selector.SimpleIndexSelector;
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
      "*",
      ":first-child",
      ":last-child",
      ":not(div)",
      ":is(div, span)",
      ":nth-child(4)"
  };

  @Test
  void testCombinators() {
    assertCombinator("node1 > node2", Combinator.PARENT);
    assertCombinator("node1 ~ node2", Combinator.SIBLING);
    assertCombinator("node1 + node2", Combinator.DIRECT_SIBLING);
    assertCombinator("node1 node2", Combinator.DESCENDANT);
    assertCombinator("node1 node2 node3", Combinator.DESCENDANT, Combinator.DESCENDANT);
    assertCombinator("node1 > node2 ~ node3", Combinator.PARENT, Combinator.SIBLING);
    assertCombinator("node1 + node2 ~ node3", Combinator.DIRECT_SIBLING, Combinator.SIBLING);
  }

  @Test
  void testNth() {
    Selector selector = Selector.parse(":nth-child(1)");
    PseudoFuncFunction<IndexSelector> func = assertInstanceOf(
        PseudoFuncFunction.class,
        selector.getNodes()[0].getFunctions()[0]
    );

    IndexSelector idxSelector = func.argument();
    SimpleIndexSelector simple = assertInstanceOf(SimpleIndexSelector.class, idxSelector);

    assertEquals(0, simple.anb().a());
    assertEquals(1, simple.anb().b());
  }

  @Test
  void testAnb_simple() {
    Selector selector = Selector.parse(":nth-child(2n+3)");
    PseudoFuncFunction<IndexSelector> func = assertInstanceOf(
        PseudoFuncFunction.class,
        selector.getNodes()[0].getFunctions()[0]
    );

    IndexSelector idxSelector = func.argument();
    SimpleIndexSelector simple = assertInstanceOf(SimpleIndexSelector.class, idxSelector);

    assertEquals(2, simple.anb().a());
    assertEquals(3, simple.anb().b());
  }

  @Test
  void testAnb_negative() {
    Selector selector = Selector.parse(":nth-child(-n+3)");
    PseudoFuncFunction<IndexSelector> func = assertInstanceOf(
        PseudoFuncFunction.class,
        selector.getNodes()[0].getFunctions()[0]
    );

    IndexSelector idxSelector = func.argument();
    SimpleIndexSelector simple = assertInstanceOf(SimpleIndexSelector.class, idxSelector);

    assertEquals(-1, simple.anb().a());
    assertEquals(3, simple.anb().b());
  }

  @Test
  void testNth_odd() {
    Selector selector = Selector.parse(":nth-child(odd)");
    PseudoFuncFunction<IndexSelector> func = assertInstanceOf(
        PseudoFuncFunction.class,
        selector.getNodes()[0].getFunctions()[0]
    );

    IndexSelector idxSelector = func.argument();
    assertSame(IndexSelector.ODD, idxSelector);
  }

  @Test
  void testNth_even() {
    Selector selector = Selector.parse(":nth-child(even)");
    PseudoFuncFunction<IndexSelector> func = assertInstanceOf(
        PseudoFuncFunction.class,
        selector.getNodes()[0].getFunctions()[0]
    );

    IndexSelector idxSelector = func.argument();
    assertSame(IndexSelector.EVEN, idxSelector);
  }

  @Test
  void testNth_ofSelector() {
    Selector selector = Selector.parse(":nth-child(3 of div)");
    PseudoFuncFunction<IndexSelector> func = assertInstanceOf(
        PseudoFuncFunction.class,
        selector.getNodes()[0].getFunctions()[0]
    );

    IndexSelector idxSelector = func.argument();
    GroupedIndexSelector grouped = assertInstanceOf(GroupedIndexSelector.class, idxSelector);

    assertEquals(0, grouped.anb().a());
    assertEquals(3, grouped.anb().b());
    assertEquals("div", grouped.group().toString());
  }

  void assertCombinator(String str, Combinator... combinator) {
    Selector selector = assertDoesNotThrow(() -> Selector.parse(str));
    SelectorNode[] nodes = selector.getNodes();

    assertEquals(combinator.length + 1, nodes.length);

    for (int i = 0; i < nodes.length; i++) {
      SelectorNode node = nodes[i];

      if (i == (nodes.length - 1)) {
        assertEquals(Combinator.DESCENDANT, node.getCombinator());
        continue;
      }

      assertEquals(combinator[i], node.getCombinator());
    }
  }

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