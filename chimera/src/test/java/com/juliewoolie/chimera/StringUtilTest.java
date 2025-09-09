package com.juliewoolie.chimera;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringUtilTest {

  @Test
  void should_returnTrue_when_equalStrings() {
    assertTrue(StringUtil.containsWord("foo", "foo"));
  }

  @Test
  void should_returnTrue_when_stringContainsOnlyWord() {
    assertTrue(StringUtil.containsWord("foo bar", "bar"));
  }

  @Test
  void should_returnTrue_when_similarWordAndExactWordInString() {
    assertTrue(StringUtil.containsWord("foobar foo bar", "bar"));
  }

  @Test
  void should_returnFalse_when_stringContainsNotWord() {
    assertFalse(StringUtil.containsWord("foobar", "bar"));
  }

}