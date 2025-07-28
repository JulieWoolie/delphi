package com.juliewoolie.delphiplugin;

public class SemanticVersions {

  public static int[] decompose(String version) {
    String[] split = version.split("\\.");
    int[] nums = new int[split.length];

    for (int i = 0; i < split.length; i++) {
      nums[i] = Integer.parseUnsignedInt(split[i]);
    }

    return nums;
  }

  public static int compareVersions(String predicate, String version) {
    int[] dPred = decompose(predicate);
    int[] dVersion = decompose(version);
    return compare(dPred, dVersion);
  }

  public static int compare(int[] predicate, int[] subject) {
    // -1 => pred < subject
    //  0 => pred == subject
    //  1 => pred > subject

    if (subject.length > predicate.length) {
      return -1;
    }
    if (predicate.length > subject.length) {
      return 1;
    }

    for (int i = 0; i < predicate.length; i++) {
      int p = predicate[i];
      int v = subject[i];

      if (p < v) {
        return -1;
      }
      if (p == v) {
        continue;
      }

      return 1;
    }

    return 0;
  }
}
