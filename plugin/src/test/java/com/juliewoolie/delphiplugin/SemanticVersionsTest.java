package com.juliewoolie.delphiplugin;

import static com.juliewoolie.delphiplugin.SemanticVersions.compareVersions;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SemanticVersionsTest {

  @Test
  void should_return0_when_versionsEqual() {
    assertEquals(0, compareVersions("0", "0"));
    assertEquals(0, compareVersions("1.0", "1.0"));
    assertEquals(0, compareVersions("1.13.0", "1.13.0"));
  }

  @Test
  void should_returnPositive_when_predicateGreaterThanSubject() {
    assertEquals(1, compareVersions("1", "0"));
    assertEquals(1, compareVersions("1.1", "1.0"));
    assertEquals(1, compareVersions("1.1.2", "1.0.1"));

    assertEquals(1, compareVersions("1.1.1", "1.1"));
  }

  @Test
  void should_returnNegative_when_predicateLesserThanSubject() {
    assertEquals(-1, compareVersions("0", "1"));
    assertEquals(-1, compareVersions("1.0", "1.1"));
    assertEquals(-1, compareVersions("1.0.1", "1.1.2"));

    assertEquals(-1, compareVersions("1.1", "1.1.1"));
  }
}