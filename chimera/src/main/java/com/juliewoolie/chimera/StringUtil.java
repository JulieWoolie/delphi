package com.juliewoolie.chimera;

public class StringUtil {

  public static boolean containsWord(String containing, String word) {
    int index = containing.indexOf(word);

    if (index == -1) {
      return false;
    }

    if (index > 0) {
      char before = containing.charAt(index - 1);

      if (!Character.isWhitespace(before)) {
        return false;
      }
    }

    int endIndex = index + word.length();

    if (containing.length() > endIndex) {
      char after = containing.charAt(endIndex);
      return Character.isWhitespace(after);
    }

    return true;
  }
}
