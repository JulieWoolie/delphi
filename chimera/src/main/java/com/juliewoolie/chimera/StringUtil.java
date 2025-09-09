package com.juliewoolie.chimera;

public class StringUtil {

  public static boolean containsWord(String containing, String word) {
    int index = containing.indexOf(word);

    while (index != -1) {
      if (index > 0) {
        char before = containing.charAt(index - 1);

        if (!Character.isWhitespace(before)) {
          index = containing.indexOf(word, index+1);
          continue;
        }
      }

      int endIndex = index + word.length();
      if (containing.length() > endIndex) {
        char after = containing.charAt(endIndex);

        if (!Character.isWhitespace(after)) {
          index = containing.indexOf(word, index+1);
          continue;
        }
      }

      return true;
    }

    return false;
  }
}
