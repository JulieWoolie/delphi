package com.juliewoolie.delphiplugin.resource;

import java.util.Comparator;

public enum FontComparator implements Comparator<MeasuredFont> {
  COMPARATOR,
  ;

  @Override
  public int compare(MeasuredFont o1, MeasuredFont o2) {
    // Parameters reversed because higher priority has to be first
    int priorityComparison = Integer.compare(o2.getPriority(), o1.getPriority());
    if (priorityComparison != 0) {
      return priorityComparison;
    }

    if (!o1.getFontId().equals(o2.getFontId())) {
      return 0;
    }

    boolean o1Res = o1.isResourceLoaded();
    boolean o2Res = o2.isResourceLoaded();

    if (o1Res == o2Res) {
      return 0;
    }

    if (o1Res) {
      return -1;
    }

    // Else o2Res = true
    return 1;
  }
}
