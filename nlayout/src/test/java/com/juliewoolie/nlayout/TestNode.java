package com.juliewoolie.nlayout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.base.Strings;

abstract class TestNode {

  LayoutNode node;

  Float expectedX = null;
  Float expectedY = null;
  Float expectedWidth = null;
  Float expectedHeight = null;

  int lineno = 0;
  int colno = 0;

  String testCaseName;

  void runTestRecursive() {
    assertEq(expectedX, node.position.x, "x position");
    assertEq(expectedY, node.position.y, "y position");
    assertEq(expectedWidth, node.size.x, "width");
    assertEq(expectedHeight, node.size.y, "height");
  }

  void assertEq(Float expect, float actual, String field) {
    if (expect == null) {
      return;
    }

    assertEquals(
        expect,
        actual,
        "Found different " + field + " than expected,"
            + " line: " + lineno + " column: " + colno
            + (Strings.isNullOrEmpty(testCaseName) ? "" : (" test-case: " + testCaseName))
    );
  }
}
